package com.gl.ceir.panel.service;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.gl.ceir.panel.constant.ActionEnum;
import com.gl.ceir.panel.constant.FeatureEnum;
import com.gl.ceir.panel.constant.StatusEnum;
import com.gl.ceir.panel.dto.AclTreeDto;
import com.gl.ceir.panel.dto.CheckCountryDto;
import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.dto.response.CheckCountryResponseDto;
import com.gl.ceir.panel.entity.FeatureEntity;
import com.gl.ceir.panel.entity.FeatureModuleEntity;
import com.gl.ceir.panel.entity.ModuleEntity;
import com.gl.ceir.panel.entity.RoleFeatureModuleAccessEntity;
import com.gl.ceir.panel.entity.RoleFeatureModuleAccessId;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.repository.AclRepository;
import com.gl.ceir.panel.repository.FeatureModuleRepository;
import com.gl.ceir.panel.repository.FeatureRepository;
import com.gl.ceir.panel.repository.RoleRepository;
import com.gl.ceir.panel.repository.UserRepository;
import com.gl.ceir.panel.repository.remote.CheckIpCountryRemoteRepostiory;
import com.gl.ceir.panel.security.jwt.service.UserDetailsImpl;
import com.gl.ceir.panel.service.criteria.AclCriteriaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Service
@Slf4j
@RequiredArgsConstructor()
public class AclService {
	private final AclRepository aclRepository;
	private final AclCriteriaService aclCriteriaService;
	private final RoleRepository roleRepository;
	private final FeatureRepository featureRepository;
	private final UserRepository userRepository;
	private final FeatureModuleRepository featureModuleRepository;
	private final FeatureService featureService;
	private final CheckIpCountryRemoteRepostiory checkIpCountryRemoteRepostiory;
	@Value("${eirs.allowed.countries.to.access.public.portal:}")
	private List<String> allowedCountries;
	private final AuditTrailService auditTrailService;
	
	public List<RoleFeatureModuleAccessEntity> save(AclTreeDto aclDto, HttpServletRequest request) {
		List<RoleFeatureModuleAccessEntity> acls = new ArrayList<>();
		try {
			UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			UserEntity userEntity = userRepository.findByUserName(user.getUsername()).get();
			
			List<RoleFeatureModuleAccessEntity> acllist = aclRepository.findByIdRoleId(aclDto.getId());
			Map<RoleFeatureModuleAccessId, RoleFeatureModuleAccessEntity> newlist = new HashMap<>();
			aclDto.getChilds().forEach(f -> {
				f.getChilds().forEach(m -> {
					if(m.isSelected()) {
						RoleFeatureModuleAccessId id= RoleFeatureModuleAccessId.builder()
						.roleId(aclDto.getId()).featureId(f.getId()).moduleId(m.getId()).build();
						RoleFeatureModuleAccessEntity acl = RoleFeatureModuleAccessEntity.builder().id(id)
								.modifiedBy(userEntity.getId()).createdBy(userEntity.getId()).status(StatusEnum.ACTIVE.status).build();
						newlist.put(id, acl);
					}
				});
			});
			
			List<RoleFeatureModuleAccessEntity> deletedlist = new ArrayList<>();
			if(CollectionUtils.isNotEmpty(acllist)) {
				Map<RoleFeatureModuleAccessId, RoleFeatureModuleAccessEntity> existing = new HashMap<>();
				acllist.forEach(acl -> {
					if(newlist.containsKey(acl.getId()) == false) {
						deletedlist.add(acl.toBuilder().status(StatusEnum.DELETED.status).build());
					} 
				});
			}
			acls = new ArrayList<RoleFeatureModuleAccessEntity>(newlist.values());
			acls.addAll(deletedlist);
			log.info("Acl size:{}", acls.size());
			FeatureEnum feature = FeatureEnum.Acl;
			ActionEnum action = CollectionUtils.isEmpty(acllist) ? ActionEnum.Add : ActionEnum.Update;
			String details = String.format("%s [%s] is %s", feature, aclDto.getId(), action.getName());
			auditTrailService.audit(request, feature, action, details);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return (List<RoleFeatureModuleAccessEntity>) aclRepository.saveAll(acls);
	}
	public List<RoleFeatureModuleAccessEntity> getAcls(){
		return aclRepository.findAll();
	}
	public List<RoleFeatureModuleAccessEntity> findByRoleId(Long roleId) {
		List<RoleFeatureModuleAccessEntity> aclEntity = aclRepository.findByIdRoleId(roleId);
		return aclEntity;
	}
	public Page<?> pagination(PaginationRequestDto ulrd) {
		return aclCriteriaService.pagination(ulrd);
	}
	public AclTreeDto getTreeByRoleId(Long roleId) {
		List<AclTreeDto> trees = new ArrayList<>();
		try {
			List<FeatureModuleEntity> features = featureModuleRepository.findAll();
			
			Set<Long> flist= featureService.getFeatures().stream().map(f -> f.getId()).collect(Collectors.toSet());
			
			List<RoleFeatureModuleAccessEntity> acls = aclRepository.findByIdRoleIdAndStatus(roleId, StatusEnum.ACTIVE.status);
			
			Map<FeatureEntity, Set<ModuleEntity>> fmodules = new HashMap<>();
			features.forEach(feature -> {
				if(flist.contains(feature.getId().getFeatureId())) {
					Set<ModuleEntity> modules = fmodules.getOrDefault(feature.getFeature(), new HashSet<ModuleEntity>());
					modules.add(feature.getModule());
					fmodules.put(feature.getFeature(), modules);
				}
			});
			Map<Long, Set<Long>> aclmap = new HashMap<>();
			if(CollectionUtils.isNotEmpty(acls)) {
				acls.forEach(acl -> {
					Set<Long> modules = aclmap.getOrDefault(acl.getId().getFeatureId(), new HashSet<Long>());
					modules.add(acl.getId().getModuleId());
					aclmap.put(acl.getId().getFeatureId(), modules);
				});
			}
			
			fmodules.forEach((k,v) -> {
				Set<Long> lmodules = aclmap.getOrDefault(k.getId(), new HashSet<Long>());
				List<AclTreeDto> childs = new ArrayList<>();
				v.forEach(m -> {
					childs.add(AclTreeDto.builder().name(m.getModuleName()).id(m.getId())
							.selected(lmodules.contains(m.getId())).childs(new ArrayList<>()).expanded(false).build());
				});
				trees.add(AclTreeDto.builder().name(k.getFeatureName()).id(k.getId()).selected(
						childs.stream().filter(c -> c.isSelected()).collect(Collectors.toList()).size() == v.size() ? true
						: false).expanded(true).childs(childs).build());
			});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return AclTreeDto.builder().name("Permissions").expanded(true).childs(trees).id(roleId).build();
	}
	public boolean delete(List<RoleFeatureModuleAccessId> ids) {
		log.info("Acl deleted entries:{}", ids);
		List<RoleFeatureModuleAccessEntity> acllist = aclRepository.findByIdIn(ids);
		acllist = acllist.stream().map(acl -> acl.toBuilder().status(StatusEnum.DELETED.status).build()).collect(Collectors.toList());
		aclRepository.saveAll(acllist);
		return true;
	}
	public boolean active(List<RoleFeatureModuleAccessId> ids) {
		log.info("Acl active entries:{}", ids);
		List<RoleFeatureModuleAccessEntity> acllist = aclRepository.findByIdIn(ids);
		acllist = acllist.stream().map(acl -> acl.toBuilder().status(StatusEnum.ACTIVE.status).build()).collect(Collectors.toList());
		aclRepository.saveAll(acllist);
		return true;
	}
	public boolean isAccessAllow(String ip) {
		CheckCountryResponseDto response = CheckCountryResponseDto.builder().countryCode("IN").build();
		try {
			InetAddress address = InetAddress.getByName(ip);
			CheckCountryDto ccd = CheckCountryDto.builder().ip(ip).ipType(address instanceof Inet6Address ? "ipv6": "ipv4").build();
			log.info("Request to check country:{}", ccd);
			response = checkIpCountryRemoteRepostiory.check(ccd);
			log.info("Response to check country:{}", response);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error:{} to check country for ip:{}", e.getMessage(), ip);
		}
		log.info("Ip:{} has country:{}, is allowed to access portal:{}, allowed countries from configuration:{}", ip,
				response.getCountryCode(), allowedCountries.contains(response.getCountryCode()), allowedCountries);
		return allowedCountries.contains(response.getCountryCode());
	}
}
