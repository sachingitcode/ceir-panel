package com.gl.ceir.panel.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.gl.ceir.panel.constant.ActionEnum;
import com.gl.ceir.panel.constant.FeatureEnum;
import com.gl.ceir.panel.constant.StatusEnum;
import com.gl.ceir.panel.dto.GroupDto;
import com.gl.ceir.panel.dto.ModuleTagDto;
import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.entity.GroupEntity;
import com.gl.ceir.panel.entity.ModuleEntity;
import com.gl.ceir.panel.entity.ModuleTagEntity;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.repository.GroupRepository;
import com.gl.ceir.panel.repository.TagRepository;
import com.gl.ceir.panel.repository.UserRepository;
import com.gl.ceir.panel.security.jwt.service.UserDetailsImpl;
import com.gl.ceir.panel.service.criteria.GroupCriteriaService;
import com.gl.ceir.panel.service.criteria.TagCriteriaService;
import com.gl.ceir.panel.service.criteria.UserCriteriaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Service
@Slf4j
@RequiredArgsConstructor()
public class TagService {
	private final TagRepository tagRepository;
	private final TagCriteriaService tagCriteriaService;
	private final UserRepository userRepository;
	private final AuditTrailService auditTrailService;

	public ModuleTagEntity save(ModuleTagDto tagDto, HttpServletRequest request) {
		UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity userEntity = userRepository.findByUserName(user.getUsername()).get();
		ModuleTagEntity tag=  tagRepository.save(ModuleTagEntity.builder().id(tagDto.getId()).moduleTagName(tagDto.getModuleTagName())
				.description(tagDto.getDescription()).createdBy(userEntity.getId()).modifiedBy(userEntity.getId())
				.status(StatusEnum.ACTIVE.status).build());
		FeatureEnum feature = FeatureEnum.Tag;
		ActionEnum action = ObjectUtils.isEmpty(tagDto.getId()) ? ActionEnum.Add : ActionEnum.Update;
		String details = String.format("%s [%s] is %s", feature, tag.getId(), action.getName());
		auditTrailService.audit(request, feature, action, details);
		
		return tag;
	}

	public List<ModuleTagEntity> getTags() {
		return tagRepository.findAll();
	}

	public ModuleTagEntity getById(Long id) {
		Optional<ModuleTagEntity> optional = tagRepository.findById(id);
		return optional.isPresent() ? optional.get() : ModuleTagEntity.builder().build();
	}

	public ModuleTagEntity update(ModuleTagDto tagDto, Long id, HttpServletRequest request) {
		ModuleTagEntity group = this.getById(id);
		return this.save(tagDto.toBuilder().id(group.getId()).build(),request);
	}

	public ModuleTagEntity deleteById(Long id, HttpServletRequest request) {
		ModuleTagEntity group = this.getById(id);
		tagRepository.save(group.toBuilder().status(StatusEnum.DELETED.status()).build());
		FeatureEnum feature = FeatureEnum.Tag;
		ActionEnum action = ActionEnum.Delete;
		String details = String.format("%s [%s] is %s", feature, group.getId(), action.getName());
		auditTrailService.audit(request, feature, action, details);
		return group;
	}

	public Page<?> pagination(PaginationRequestDto ulrd) {
		return tagCriteriaService.pagination(ulrd);
	}

	public boolean delete(List<Long> ids) {
		List<ModuleTagEntity> list = tagRepository.findByIdIn(ids);
		tagRepository.saveAll(list.stream().map(l -> l.toBuilder().status(StatusEnum.DELETED.status).build())
				.collect(Collectors.toList()));
		return true;
	}

	public boolean active(List<Long> ids) {
		List<ModuleTagEntity> list = tagRepository.findByIdIn(ids);
		tagRepository.saveAll(list.stream().map(l -> l.toBuilder().status(StatusEnum.ACTIVE.status).build())
				.collect(Collectors.toList()));
		return true;
	}
}
