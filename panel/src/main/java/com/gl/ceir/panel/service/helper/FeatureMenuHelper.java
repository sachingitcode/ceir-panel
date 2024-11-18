package com.gl.ceir.panel.service.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.gl.ceir.panel.constant.StatusEnum;
import com.gl.ceir.panel.dto.response.FeautreMenuDto;
import com.gl.ceir.panel.entity.GroupFeatureEntity;
import com.gl.ceir.panel.entity.LinkEntity;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.entity.UserGroupEntity;
import com.gl.ceir.panel.repository.GroupFeatureRepository;
import com.gl.ceir.panel.repository.LinkRepository;
import com.gl.ceir.panel.repository.UserGroupRepository;
import com.gl.ceir.panel.repository.UserRepository;
import com.gl.ceir.panel.security.jwt.service.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor()
public class FeatureMenuHelper {
	private final UserRepository userRepository;
	private final LinkRepository linkRepository;
	private final GroupFeatureRepository groupFeatureRepository;
	private final UserGroupRepository userGroupRepository;

	public Set<FeautreMenuDto> menu() {
		try {
			UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			UserEntity userEntity = userRepository.findByUserName(user.getUsername()).get();
			List<UserGroupEntity> groups = userGroupRepository.findByIdUserIdAndStatus(userEntity.getId(),
					StatusEnum.ACTIVE.status);
			log.info("Groups: {}", groups.stream().map(g -> g.getId().getGroupId()).collect(Collectors.toList()));
			List<GroupFeatureEntity> features = groupFeatureRepository.findByIdGroupIdInAndStatusOrderByDisplayOrder(
					groups.stream().map(g -> g.getId().getGroupId()).collect(Collectors.toList()),
					StatusEnum.ACTIVE.status);
			return process(features);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Invalid user's token id:{}", e.getMessage());
		}
		return new HashSet<>();
	}

	private Set<FeautreMenuDto> process(List<GroupFeatureEntity> features) {
		Set<FeautreMenuDto> menu = new HashSet<FeautreMenuDto>();
		Map<String, LinkEntity> links = links();
		features.forEach(f -> {
			log.info("feature:{}", f.getFeature().getFeatureName());
			menu.add(FeautreMenuDto.builder().name(f.getFeature().getFeatureName())
					.link(links.get(f.getFeature().getLink()).getUrl())
					.icon(ObjectUtils.isNotEmpty(f.getFeature().getLogo()) ? f.getFeature().getLogo()
							: links.get(f.getFeature().getLink()).getIcon())
					.key(f.getFeature().getLink()).displayOrder(f.getDisplayOrder()).build());
		});
		return menu;
	}

	private Map<String, LinkEntity> links() {
		Map<String, LinkEntity> map = new HashMap<>();
		linkRepository.findAll().forEach(link -> map.put(link.getLinkName(), link));
		return map;
	}
}
