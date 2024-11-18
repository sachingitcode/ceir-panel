package com.gl.ceir.panel.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gl.ceir.panel.constant.StatusEnum;
import com.gl.ceir.panel.dto.ChangePasswordDto;
import com.gl.ceir.panel.dto.UserCreateDto;
import com.gl.ceir.panel.dto.request.LoginRequest;
import com.gl.ceir.panel.dto.request.SignupRequest;
import com.gl.ceir.panel.dto.response.JwtResponse;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.entity.UserGroupEntity;
import com.gl.ceir.panel.repository.UserGroupRepository;
import com.gl.ceir.panel.repository.UserRepository;
import com.gl.ceir.panel.security.jwt.JwtUtils;
import com.gl.ceir.panel.security.jwt.service.UserDetailsImpl;
import com.gl.ceir.panel.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:4200")
@SuppressWarnings("unused")
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final PasswordEncoder encoder;
	private final JwtUtils jwtUtils;
	private final UserService userService;
	private UserGroupRepository userGroupRepository;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		try {
			log.info("login request:{}", loginRequest);
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
			return ResponseEntity.ok(JwtResponse.builder().userName(userDetails.getUsername()).id(userDetails.getId())
					.token(jwt).email(userDetails.getEmail()).apiResult(message).message(message).build());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok(JwtResponse.builder().apiResult("fail").message("invalidCredentials").build());
		}
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest,HttpServletRequest request) {
		UserCreateDto ucd = UserCreateDto.builder().build();
		BeanUtils.copyProperties(signUpRequest, ucd);
		return userService.save(ucd,request);
	}
	
	@GetMapping("/email/{email}")
	public ResponseEntity<?> emailExist(@PathVariable String email) {
		return new ResponseEntity<>(userRepository.existsByProfileEmail(email), HttpStatus.OK);
	}
	
	@PostMapping("/change-password")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto cpd,HttpServletRequest request) {
		log.info("change password:{}", cpd);
		return new ResponseEntity<>(userService.changePassword(cpd,request), HttpStatus.OK);
	}
	@GetMapping("/isLogin")
	public ResponseEntity<?> isLogin() {
		return userService.prevalidate();
	}
	@GetMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) new SecurityContextLogoutHandler().logout(request, response, authentication);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
