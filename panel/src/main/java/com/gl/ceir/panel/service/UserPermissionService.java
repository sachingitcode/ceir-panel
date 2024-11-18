package com.gl.ceir.panel.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.gl.ceir.panel.constant.AccessEnum;
import com.gl.ceir.panel.constant.StatusEnum;
import com.gl.ceir.panel.dto.PermissionDto;
import com.gl.ceir.panel.dto.UserGroupPermissionDto;
import com.gl.ceir.panel.entity.FeatureEntity;
import com.gl.ceir.panel.entity.GroupEntity;
import com.gl.ceir.panel.entity.GroupRoleEntity;
import com.gl.ceir.panel.entity.RoleEntity;
import com.gl.ceir.panel.entity.RoleFeatureModuleAccessEntity;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.entity.UserGroupEntity;
import com.gl.ceir.panel.repository.AclRepository;
import com.gl.ceir.panel.repository.GroupRepository;
import com.gl.ceir.panel.repository.GroupRoleRepository;
import com.gl.ceir.panel.repository.UserGroupRepository;
import com.gl.ceir.panel.repository.UserRepository;
import com.gl.ceir.panel.security.jwt.service.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor()
@Slf4j
public class UserPermissionService {
	private final UserRepository userRepository;
	private final GroupRepository groupRepository;
	private final AclRepository aclRepository;
	private final GroupRoleRepository groupRoleRepository;
	private final UserGroupRepository userGroupRepository;

	public UserGroupPermissionDto permissions() {
		try {
			UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			UserEntity userEntity = userRepository.findByUserName(user.getUsername()).get();
			
			return this.permissions(userEntity);
		}catch(Exception e) {
			e.printStackTrace();
			log.error("Invalid user's token id: {}", e.getMessage());
		}
		return UserGroupPermissionDto.builder().build();
	}
	private void findAllGroup(List<GroupEntity> glist, Set<Long> groupIds) {
		glist.forEach(g -> {
			groupIds.add(g.getId());
			if(CollectionUtils.isNotEmpty(g.getChildren())) {
				this.findAllGroup(g.getChildren(), groupIds);
			}
		});
	}
	private void getGroupPerAccess(List<GroupEntity> groups, UserEntity userEntity, Set<Long> userIds, Set<Long> roles) {
		userIds.add(userEntity.getId());
		groups.forEach(group -> {
			List<GroupRoleEntity> groles = groupRoleRepository.findByIdGroupIdAndStatus(group.getId(), StatusEnum.ACTIVE.status);
			groles.stream().filter(r -> r.getStatus().equals(StatusEnum.ACTIVE.status)).forEach(role -> {
				roles.add(role.getRole().getId());
				List<GroupEntity> glist = groupRepository.findByParent(group);
				if(role.getRole().getAccess()==AccessEnum.all) {
					Set<Long> groupIds = new HashSet<Long>();
					this.findAllGroup(glist, groupIds);
					List<UserGroupEntity> ugroups = userGroupRepository.findByIdGroupIdIn(groupIds);
					userIds.addAll(ugroups.stream().map(g -> g.getId().getUserId()).collect(Collectors.toSet()));
				} else if(role.getRole().getAccess()==AccessEnum.group) {
					List<UserGroupEntity> ugroups = userGroupRepository.findByIdGroupId(group.getId());
					userIds.addAll(ugroups.stream().map(g -> g.getId().getUserId()).collect(Collectors.toSet()));
				}
			});
		});
		List<UserEntity> users = userRepository.findByCreatedByIn(userIds);
		userIds.addAll(users.stream().map(u -> u.getId()).collect(Collectors.toSet()));
	}
	public List<GroupEntity> getUserGroups() {
		List<GroupEntity> groups = new ArrayList<GroupEntity>();
		try {
			UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			UserEntity userEntity = userRepository.findByUserName(user.getUsername()).get();
			List<UserGroupEntity> groupslist = userGroupRepository.findByIdUserIdAndStatus(userEntity.getId(), StatusEnum.ACTIVE.status);
			groups = groupslist.stream().map(g -> g.getGroup()).collect(Collectors.toList());
			groups.addAll(groupRepository.findByCreatedByInAndStatusAndParentIsNull(this.permissions().getUserIds(),StatusEnum.ACTIVE.status));
		}catch(Exception e) {
			e.printStackTrace();
			log.error("Invalid user's token id: {}", e.getMessage());
		}
		return groups;
	}
	public UserGroupPermissionDto permissions(UserEntity userEntity) {
		try {
			List<UserGroupEntity> groupslist = userGroupRepository.findByIdUserIdAndStatus(userEntity.getId(), StatusEnum.ACTIVE.status);
			List<GroupEntity> glist = groupslist.stream().map(g -> g.getGroup()).collect(Collectors.toList());
			Set<Long> userIds = new HashSet<Long>();
			Set<Long> roles = new HashSet<Long>();
			this.getGroupPerAccess(glist, userEntity, userIds, roles);
			List<RoleFeatureModuleAccessEntity> acls = aclRepository.findByIdRoleIdInAndStatus(roles, StatusEnum.ACTIVE.status);
			log.info("user ids: {}", userIds);
			final Map<String, Set<String>> permissions = new HashMap<String, Set<String>>();
			List<PermissionDto> permlist = new ArrayList<>();
			acls.forEach(acl -> {
				try {
					PermissionDto permission = PermissionDto.builder().tag(acl.getFeature().getModuleTag().getModuleTagName()).build();
					Set<String> modules = permissions.getOrDefault(acl.getFeature().getModuleTag().getModuleTagName(), new HashSet<String>());
					modules.add(acl.getModule().getModuleName());
					permissions.put(acl.getFeature().getModuleTag().getModuleTagName(), modules);
				}catch(Exception e) {
					log.error("error in permission for acl:{}",acl);
				}
			});
			for (String key : permissions.keySet()) {
		        permlist.add(PermissionDto.builder().tag(key).modules(permissions.get(key)).build());
		    }
			log.info("permission:{}", permlist);
			return UserGroupPermissionDto.builder().userIds(userIds).permissions(permlist).build();
		}catch(Exception e) {
			e.printStackTrace();
			log.error("Invalid user's token id: {}", e.getMessage());
		}
		return UserGroupPermissionDto.builder().build();
	}
	private Set<Long> getRoles(UserEntity userEntity) {
		Set<Long> groups= userGroupRepository.findByIdUserIdAndStatus(userEntity.getId(), StatusEnum.ACTIVE.status).stream().map(g -> g.getId().getGroupId()).collect(Collectors.toSet());
		log.info("groups: {}", groups);
		List<GroupRoleEntity> list = groupRoleRepository.findByIdGroupIdInAndStatus(groups, StatusEnum.ACTIVE.status);
		log.info("Group role size:{}", list.size());
		return list.stream().map(r -> r.getId().getRoleId()).collect(Collectors.toSet());
	}

	private AccessEnum getAccess(List<GroupRoleEntity> list) {
		Set<AccessEnum> access = list.stream().map(l -> l.getRole().getAccess()).collect(Collectors.toSet());
		return access.contains(AccessEnum.all) ? AccessEnum.all
				: access.contains(AccessEnum.group) ? AccessEnum.group : AccessEnum.self;
	}

	private Set<Long> getGroupsUsers(Set<Long> groups) {
		Set<Long> userids = userGroupRepository.findByIdGroupIdIn(groups).stream().map(g -> g.getId().getUserId()).collect(Collectors.toSet());
		return userids;
	}

	private Set<Long> getChildGroupsUsers(List<GroupEntity> groups) {
		log.info("Parent groups: {}", groups.stream().map(g -> g.getId()).collect(Collectors.toList()));
		List<GroupEntity> glist = groupRepository.findByParentIn(groups);
		Set<Long> list = glist.stream().map(g -> g.getId()).collect(Collectors.toSet());
		Set<Long> userids = userGroupRepository.findByIdGroupIdIn(list).stream().map(g -> g.getId().getUserId()).collect(Collectors.toSet());
		return userids;
	}
	public List<Long> groups(List<Long> groups, Long groupId) {
		GroupEntity group = groupRepository.findById(groupId).get();
		List<GroupRoleEntity> grlist = groupRoleRepository.findByIdGroupId(groupId);
		List<RoleEntity> roles = grlist.stream().map(gr -> gr.getRole()).collect(Collectors.toList());
		List<RoleEntity> aroles = roles.stream().filter(r -> r.getAccess() == AccessEnum.all).collect(Collectors.toList());
		List<RoleEntity> groles = roles.stream().filter(r -> r.getAccess() == AccessEnum.group).collect(Collectors.toList());
		List<RoleEntity> sroles = roles.stream().filter(r -> r.getAccess() == AccessEnum.self).collect(Collectors.toList());
		AccessEnum access = CollectionUtils.isNotEmpty(aroles) ? AccessEnum.all: CollectionUtils.isNotEmpty(groles) ? AccessEnum.group: AccessEnum.self;
		groups.add(groupId);
		if(access == AccessEnum.all || access == AccessEnum.group) {
			if(CollectionUtils.isEmpty(group.getChildren())) {
				return groups;
			} else {
				group.getChildren().forEach(g -> groups(groups, g.getId()));
			}
		}
		return groups;
	}
}
