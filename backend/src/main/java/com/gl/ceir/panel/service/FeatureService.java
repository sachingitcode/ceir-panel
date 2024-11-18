package com.gl.ceir.panel.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.gl.ceir.panel.constant.AccessEnum;
import com.gl.ceir.panel.constant.ActionEnum;
import com.gl.ceir.panel.constant.FeatureEnum;
import com.gl.ceir.panel.constant.LogicalDirectoryEnum;
import com.gl.ceir.panel.constant.StatusEnum;
import com.gl.ceir.panel.dto.FeatureDto;
import com.gl.ceir.panel.dto.FeatureModuleDto;
import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.dto.response.FeatureModuleViewDto;
import com.gl.ceir.panel.dto.response.FeautreMenuDto;
import com.gl.ceir.panel.entity.FeatureEntity;
import com.gl.ceir.panel.entity.FeatureModuleEntity;
import com.gl.ceir.panel.entity.FeatureModuleId;
import com.gl.ceir.panel.entity.GroupEntity;
import com.gl.ceir.panel.entity.GroupFeatureEntity;
import com.gl.ceir.panel.entity.ModuleEntity;
import com.gl.ceir.panel.entity.ModuleTagEntity;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.repository.FeatureModuleRepository;
import com.gl.ceir.panel.repository.FeatureRepository;
import com.gl.ceir.panel.repository.GroupFeatureRepository;
import com.gl.ceir.panel.repository.ModuleRepository;
import com.gl.ceir.panel.repository.TagRepository;
import com.gl.ceir.panel.repository.UserRepository;
import com.gl.ceir.panel.security.jwt.service.UserDetailsImpl;
import com.gl.ceir.panel.service.criteria.FeatureCriteriaService;
import com.gl.ceir.panel.service.helper.FeatureMenuHelper;
import com.gl.ceir.panel.util.DirectoryUtil;
import com.gl.ceir.panel.util.FileUploadUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Service
@Slf4j
@RequiredArgsConstructor()
public class FeatureService {
	@Value("${eirs.panel.source.path:}")
	private String basepath;
	private final FeatureRepository featureRepository;
	private final ModuleRepository moduleRepository;
	private final FeatureCriteriaService featureCriteriaService;
	private final UserRepository userRepository;
	private final TagRepository tagRepository;
	private final UserPermissionService permissionService;
	private final FeatureMenuHelper featureMenuHelper;
	private final DirectoryUtil directoryUtil;
	private final FileUploadUtil fileUploadUtil;
	private final FeatureModuleService featureModuleService;
	private final FeatureModuleRepository featureModuleRepository;
	private final AuditTrailService auditTrailService;
	private final GroupFeatureRepository groupFeatureRepository;

	public FeatureEntity save(FeatureDto featureDto, HttpServletRequest request) {
		FeatureEntity featureEntity = null;
		FeatureEntity existing = null;
		try {
			List<FeatureModuleEntity> list = featureModuleRepository.findByIdFeatureId(featureDto.getId());
			if (ObjectUtils.isNotEmpty(featureDto.getId()))
				existing = this.getById(featureDto.getId());
			UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			UserEntity userEntity = userRepository.findByUserName(user.getUsername()).get();
			Optional<ModuleEntity> odme = moduleRepository.findById(featureDto.getDefaultModuleId());
			String logicalpath = fileUploadUtil.upload(featureDto.getFile(), basepath,
					LogicalDirectoryEnum.feature.name());
			log.info("Logical path: {}", logicalpath);
			featureEntity = FeatureEntity.builder().id(featureDto.getId()).featureName(featureDto.getFeatureName())
					.description(featureDto.getDescription()).defaultModule(odme.get())
					.category(featureDto.getCategory())
					.logo(ObjectUtils.isEmpty(logicalpath) && ObjectUtils.isNotEmpty(existing) ? existing.getLogo()
							: logicalpath)
					.createdBy(userEntity.getId()).modifiedBy(userEntity.getId()).link(featureDto.getLink()).build();
			featureEntity = featureEntity.toBuilder()
					.status(ObjectUtils.isNotEmpty(existing) && ObjectUtils.isNotEmpty(existing.getStatus())
							? existing.getStatus()
							: StatusEnum.INACTIVE.status)
					.build();

			if (org.apache.commons.lang3.ObjectUtils.isNotEmpty(featureDto.getModuleTagId())) {
				Optional<ModuleTagEntity> otag = tagRepository.findById(featureDto.getModuleTagId());
				if (otag.isPresent())
					featureEntity = featureEntity.toBuilder().moduleTag(otag.get()).build();
			}
			featureEntity = featureRepository.save(featureEntity.toBuilder().modules(list.stream()
					.map(l -> ModuleEntity.builder().id(l.getId().getModuleId()).build()).collect(Collectors.toList()))
					.build());
			list = list.stream().map(l -> FeatureModuleEntity.builder().createdBy(l.getCreatedBy()).modifiedBy(userEntity.getId()).status(l.getStatus()).id(
					FeatureModuleId.builder().featureId(featureDto.getId()).moduleId(l.getId().getModuleId()).build())
					.createdOn(l.getCreatedOn()).updatedOn(l.getUpdatedOn()).build()).collect(Collectors.toList());
			
			FeatureEnum feature = FeatureEnum.Feature;
			ActionEnum action = ObjectUtils.isEmpty(featureDto.getId()) ? ActionEnum.Add : ActionEnum.Update;
			String details = String.format("%s [%s] is %s", feature, featureEntity.getId(), action.getName());
			auditTrailService.audit(request, feature, action, details);
			
			featureModuleRepository.saveAll(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return featureEntity;
	}

	public List<FeatureEntity> getFeatures() {
		List<GroupEntity> groups = permissionService.getUserGroups();
		List<GroupFeatureEntity> gfeatures = groupFeatureRepository.findByIdGroupIdInAndStatusOrderByDisplayOrder(
				groups.stream().map(m -> m.getId()).collect(Collectors.toList()), StatusEnum.ACTIVE.status);
		return gfeatures.stream().filter(f -> f.getFeature().getStatus().equals(StatusEnum.ACTIVE.status))
				.map(f -> f.getFeature()).collect(Collectors.toList());
	}

	public FeatureEntity getById(Long id) {
		Optional<FeatureEntity> optional = featureRepository.findById(id);
		return optional.isPresent() ? optional.get() : FeatureEntity.builder().build();
	}

	public FeatureEntity update(FeatureDto featureDto, Long id, HttpServletRequest request) {
		return this.save(featureDto.toBuilder().id(id).build(),request);
	}

	public FeatureEntity deleteById(Long id) {
		FeatureEntity group = this.getById(id);
		featureRepository.deleteById(id);
		return group;
	}

	public Page<?> pagination(PaginationRequestDto ulrd) {
		return featureCriteriaService.pagination(ulrd);
	}

	public Set<FeautreMenuDto> menu() {
		return featureMenuHelper.menu();
	}

	public boolean delete(List<Long> ids) {
		List<FeatureEntity> list = featureRepository.findByIdIn(ids);
		featureRepository.saveAll(list.stream().map(l -> l.toBuilder().status(StatusEnum.DELETED.status).build())
				.collect(Collectors.toList()));
		return true;
	}

	public boolean active(List<Long> ids) {
		List<FeatureEntity> list = featureRepository.findByIdIn(ids);
		featureRepository.saveAll(list.stream().map(l -> l.toBuilder().status(StatusEnum.ACTIVE.status).build())
				.collect(Collectors.toList()));
		return true;
	}
}
