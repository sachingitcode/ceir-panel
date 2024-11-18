package com.gl.ceir.panel.service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.gl.ceir.panel.constant.ActionEnum;
import com.gl.ceir.panel.constant.FeatureEnum;
import com.gl.ceir.panel.constant.StatusEnum;
import com.gl.ceir.panel.entity.AuditTrailEntity;
import com.gl.ceir.panel.entity.FeatureEntity;
import com.gl.ceir.panel.entity.GroupRoleEntity;
import com.gl.ceir.panel.entity.RoleEntity;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.entity.UserGroupEntity;
import com.gl.ceir.panel.repository.AuditTrailRepository;
import com.gl.ceir.panel.repository.FeatureRepository;
import com.gl.ceir.panel.repository.GroupRoleRepository;
import com.gl.ceir.panel.repository.RoleRepository;
import com.gl.ceir.panel.repository.UserGroupRepository;
import com.gl.ceir.panel.repository.UserRepository;
import com.gl.ceir.panel.repository.UserRoleRepository;
import com.gl.ceir.panel.security.jwt.service.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditTrailService {
	private final AuditTrailRepository auditTrailRepository;
	private final FeatureRepository featureRepository;
	private final GroupRoleRepository groupRoleRepository;
	private final UserGroupRepository userGroupRepository;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	public void audit(HttpServletRequest request, FeatureEnum featureEnum, ActionEnum action, String details) {
		try {
			UserEntity entity = this.getLoggedInUser();
			if (ObjectUtils.isNotEmpty(entity)) {
				Map<String, FeatureEntity> map = this.loadfeature();
				FeatureEntity feature = map.get(featureEnum.getName());
				if (ObjectUtils.isNotEmpty(feature)) {
					String roleType = getRole(entity);
					AuditTrailEntity audit = AuditTrailEntity.builder().featureId(feature.getId())
							.featureName(feature.getFeatureName()).publicIp(request.getRemoteAddr()).roleType(roleType)
							.subFeature(action.name()).txnId(String.valueOf(System.currentTimeMillis()))
							.userId(entity.getId()).userName(entity.getUserName()).details(details).build();
					auditTrailRepository.save(audit);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getRole(UserEntity entity) {
		String roles = null;
		try {
			List<UserGroupEntity> groups = userGroupRepository.findByIdUserId(entity.getId());
			if(CollectionUtils.isNotEmpty(groups)) {
				List<GroupRoleEntity> groles = groupRoleRepository.findByIdGroupIdInAndStatus(
						groups.stream().map(g -> g.getId().getGroupId()).collect(Collectors.toSet()),
						StatusEnum.ACTIVE.status());
				if(CollectionUtils.isNotEmpty(groles)) {
					List<RoleEntity> rroles = roleRepository
							.findByIdIn(groles.stream().map(r -> r.getId().getRoleId()).collect(Collectors.toList()));
					if(CollectionUtils.isNotEmpty(rroles)) {
						roles = rroles.stream().map(r -> r.getRoleName()).collect(Collectors.joining(","));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roles;
	}

	private Map<String, FeatureEntity> loadfeature() {
		Map<String, FeatureEntity> map = new HashMap<>();
		List<FeatureEntity> features = featureRepository.findAll();
		features.forEach(f -> map.putIfAbsent(f.getLink(), f));
		return map;
	}
	public UserEntity getLoggedInUser() {
		UserEntity entity = null;
		try {
			UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			entity = userRepository.findByUserName(user.getUsername()).get();
		} catch (Exception e) {
			log.info("Error while user regiestered by public api: {}", e.getMessage());
		}
		return entity;
	}
}
