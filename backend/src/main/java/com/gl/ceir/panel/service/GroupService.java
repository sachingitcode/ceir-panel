package com.gl.ceir.panel.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.gl.ceir.panel.constant.ActionEnum;
import com.gl.ceir.panel.constant.FeatureEnum;
import com.gl.ceir.panel.constant.StatusEnum;
import com.gl.ceir.panel.dto.GroupDto;
import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.dto.UserGroupPermissionDto;
import com.gl.ceir.panel.entity.GroupEntity;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.entity.UserGroupEntity;
import com.gl.ceir.panel.repository.GroupRepository;
import com.gl.ceir.panel.repository.UserGroupRepository;
import com.gl.ceir.panel.repository.UserRepository;
import com.gl.ceir.panel.security.jwt.JwtUtils;
import com.gl.ceir.panel.security.jwt.service.UserDetailsImpl;
import com.gl.ceir.panel.service.criteria.GroupCriteriaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Service
@Slf4j
@RequiredArgsConstructor()
public class GroupService {
	private final JwtUtils jwtUtils;
	private final GroupRepository groupRepository;
	private final GroupCriteriaService groupCriteriaService;
	private final UserRepository userRepository;
	private final UserPermissionService permissionService;
	private final AuditTrailService auditTrailService;
	private final UserGroupRepository userGroupRepository;

	public GroupEntity save(GroupDto groupDto, HttpServletRequest request) {
		UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity userEntity = userRepository.findByUserName(user.getUsername()).get();
		GroupEntity parent = groupRepository
				.findById(ObjectUtils.isEmpty(groupDto.getParentGroupId()) ? 0L : groupDto.getParentGroupId())
				.orElse(null);
		GroupEntity entity = groupRepository.save(GroupEntity.builder().id(groupDto.getId())
				.groupName(groupDto.getGroupName()).parent(parent).description(groupDto.getDescription())
				.createdBy(userEntity.getId()).modifiedBy(userEntity.getId()).status(StatusEnum.ACTIVE.status).build());
		
		FeatureEnum feature = FeatureEnum.Group;
		ActionEnum action = ObjectUtils.isEmpty(groupDto.getId()) ? ActionEnum.Add : ActionEnum.Update;
		String details = String.format("%s [%s] is %s", feature, entity.getId(), action.getName());
		auditTrailService.audit(request, feature, action, details);
		return entity;
	}

	public List<GroupEntity> getParents() {
		return permissionService.getUserGroups();
	}

	public List<GroupEntity> getGroups() {
		UserEntity entity = auditTrailService.getLoggedInUser();
		List<UserGroupEntity> ugroups = userGroupRepository.findByIdUserId(entity.getId());
		List<GroupEntity> groups = ugroups.stream().filter(u -> u.getStatus().equals(StatusEnum.ACTIVE.status)).map(g -> g.getGroup())
				.filter(g -> g.getStatus().equals(StatusEnum.ACTIVE.status)).collect(Collectors.toList());
		groups.addAll(groupRepository.findByCreatedByInAndStatus(permissionService.permissions().getUserIds(),
				StatusEnum.ACTIVE.status));
		return groups;
	}

	public GroupEntity getById(Long id) {
		Optional<GroupEntity> optional = groupRepository.findById(id);
		return optional.isPresent() ? optional.get() : GroupEntity.builder().build();
	}

	public GroupEntity update(GroupDto groupDto, Long id, HttpServletRequest request) {
		GroupEntity group = this.getById(id);
		return this.save(groupDto.toBuilder().id(group.getId()).build(), request);
	}

	public GroupEntity deleteById(Long id, HttpServletRequest request) {
		GroupEntity group = this.getById(id);
		groupRepository.save(group.toBuilder().status(StatusEnum.DELETED.status()).build());
		FeatureEnum feature = FeatureEnum.Group;
		ActionEnum action = ActionEnum.Delete;
		String details = String.format("%s [%s] is %s", feature, group.getId(), action.getName());
		auditTrailService.audit(request, feature, action, details);
		return group;
	}

	public Page<?> pagination(PaginationRequestDto ulrd) {
		return groupCriteriaService.pagination(ulrd);
	}
}
