package com.gl.ceir.panel.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.gl.ceir.panel.constant.StatusEnum;
import com.gl.ceir.panel.dto.request.LoginRequest;
import com.gl.ceir.panel.dto.response.JwtResponse;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.entity.UserFeatureIpAccessListEntity;
import com.gl.ceir.panel.entity.UserGroupEntity;
import com.gl.ceir.panel.repository.UserFeatureIpAccessListRepository;
import com.gl.ceir.panel.repository.UserGroupRepository;
import com.gl.ceir.panel.repository.UserRepository;
import com.gl.ceir.panel.security.jwt.JwtUtils;
import com.gl.ceir.panel.security.jwt.service.UserDetailsImpl;
import com.gl.ceir.panel.service.AclService;
import com.gl.ceir.panel.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthSecurity {
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final UserService userService;
	private final AclService aclService;
	private final UserGroupRepository userGroupRepository;
	private final UserFeatureIpAccessListRepository featureIpAccessListRepository;
	private final JwtUtils jwtUtils;
	private final Map<String, Integer> sessionMap;
	@Value("${allowed.failed.login.attempts:3}")
	private int allowedFailedLoginAttempts;
	@Value("${allowed.parallel.session:3}")
	private int allowedParallelSession;
	
	public JwtResponse isSecurityPassed(LoginRequest loginRequest, HttpServletRequest request) {
		JwtResponse jwtResponse = JwtResponse.builder().apiResult("success").build();
		UserEntity entity = UserEntity.builder().build();
		this.isAllowedSessionLimitBreached(loginRequest, jwtResponse, request)
				.isLoggedIn(loginRequest, jwtResponse, request, entity)
				.isIpBlackListed(loginRequest, jwtResponse, request, entity)
				.isOutsideRegion(loginRequest, jwtResponse, request)
				.isFailedAttemptReached(loginRequest, jwtResponse, request);
		return jwtResponse;
	}
	private AuthSecurity isAllowedSessionLimitBreached(LoginRequest loginRequest, JwtResponse jwtResponse, HttpServletRequest request) {
		if(sessionMap.getOrDefault(request, 0) >= allowedParallelSession) {
			jwtResponse.setMessage("parallelSessionLimitBreached");
			jwtResponse.setApiResult("fail");
		}
		return this;
	}
	private AuthSecurity isLoggedIn(LoginRequest loginRequest, JwtResponse jwtResponse, HttpServletRequest request, UserEntity entity) {
		try {
			if(jwtResponse.getApiResult().equals("fail")) return this;
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
					.collect(Collectors.toList());
			Optional<UserEntity> ueo = userRepository.findByUserName(userDetails.getUsername());
			String message = "success";
			if(ueo.isPresent() && ueo.get().getCurrentStatus().equals(StatusEnum.ACTIVE.status)==false) {
				message = ueo.get().getCurrentStatus().equals("0") ? "inactive": message;
			} else {
				List<UserGroupEntity> ugroups = userGroupRepository.findByIdUserId(ueo.get().getId());
				message = CollectionUtils.isEmpty(ugroups) ? "groupNotAssigned": message;
			}
			if(ueo.isPresent()) {
				userRepository.save(ueo.get().toBuilder().lastLoginDate(LocalDateTime.now()).failedAttempt(0).build());
				entity.setId(ueo.get().getId());
			}
			jwtResponse.setUserName(userDetails.getUsername());
			jwtResponse.setToken(jwt);
			jwtResponse.setEmail(userDetails.getEmail());
			jwtResponse.setMessage(message);
			jwtResponse.setApiResult(message);
			log.info("Response:{}", jwtResponse);
			sessionMap.put(userDetails.getUsername(), sessionMap.getOrDefault(request, 0) + 1);
		}catch(Exception e) {
			jwtResponse.setMessage("invalidCredentials");
			jwtResponse.setApiResult("fail");
		}
		return this;
	}
	
	private AuthSecurity isOutsideRegion(LoginRequest loginRequest, JwtResponse jwtResponse, HttpServletRequest request) {
		log.info("Response:{}", jwtResponse);
		if(jwtResponse.getApiResult().equals("fail") == false && aclService.isAccessAllow(request.getRemoteAddr()) == false) {
			jwtResponse.setMessage("loginFromOutsideRegion");
			jwtResponse.setApiResult("fail");
		}
		return this;
	}
	private AuthSecurity isIpBlackListed(LoginRequest loginRequest, JwtResponse jwtResponse, HttpServletRequest request, UserEntity entity) {
		log.info("Logged in ip:{} for user id:{}", request.getRemoteAddr(), entity.getId());
		if(jwtResponse.getApiResult().equals("fail") == false) {
			List<UserFeatureIpAccessListEntity> list = featureIpAccessListRepository.findByUserId(entity.getId());
			if(CollectionUtils.isNotEmpty(list)) {
				if(list.stream().anyMatch(l -> l.getIpAddress().contains(request.getRemoteAddr()))) {
					log.info("Ip: {} blacklisted, so not to allow login", request.getRemoteAddr());
					jwtResponse.setMessage("ipNotWhitelisted");
					jwtResponse.setApiResult("fail");
				}
			}
		}
		return this;
	}
	private AuthSecurity isFailedAttemptReached(LoginRequest loginRequest, JwtResponse jwtResponse, HttpServletRequest request) {
		if(jwtResponse.getApiResult().equals("fail")) {
			jwtResponse.setApiResult("fail");
			UserEntity entity = userService.updateFailedAttempt(loginRequest);
			if(ObjectUtils.isNotEmpty(entity) && entity.getFailedAttempt() >= allowedFailedLoginAttempts) {
				userRepository.save(entity.toBuilder().currentStatus(StatusEnum.DELETED.status).build());
				jwtResponse.setMessage("failedAttemptReached");
			}
		}
		return this;
	}
}
