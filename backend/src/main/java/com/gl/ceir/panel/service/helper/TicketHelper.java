package com.gl.ceir.panel.service.helper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.gl.ceir.panel.constant.AccessEnum;
import com.gl.ceir.panel.constant.StatusEnum;
import com.gl.ceir.panel.constant.UserTypeEnum;
import com.gl.ceir.panel.dto.FilterDto;
import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.dto.TicketDto;
import com.gl.ceir.panel.entity.GroupRoleEntity;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.entity.UserGroupEntity;
import com.gl.ceir.panel.repository.GroupRoleRepository;
import com.gl.ceir.panel.repository.UserGroupRepository;
import com.gl.ceir.panel.service.UserService;
import com.gl.ceir.panel.util.DateUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketHelper {
	private final UserService userService;
	private final GroupRoleRepository groupRoleRepository;
	@Value("${eirs.panel.register.client.type:REGISTERED}")
	private String loggedInUserType;
	@Value("${eirs.panel.redmine.registered.user:DUMMY_AGENT}")
	private String redmineRegisteredUser;

	@Value("${eirs.panel.unregister.client.type:END_USER}")
	private String loggedOutUserType;
	
	private final DateUtil dateUtil;
	
	private final UserGroupRepository userGroupRepository;
	
	@Value("${eirs.system.admin.support.group:ticket-support}")
	private String systemSupport;
	@Value("${eirs.customer.care.group:customer_care}")
	private String customerCare;
	
	public TicketDto toRedmineDto(TicketDto ticketDto) {
		UserEntity entity = userService.getLoggedInUser();
		if(ObjectUtils.isNotEmpty(entity)) {
			if(ObjectUtils.isEmpty(ticketDto.getFirstName()) || ObjectUtils.isEmpty(ticketDto.getMobileNumber())) {
				ticketDto = ticketDto.toBuilder().firstName(entity.getProfile().getFirstName())
						.lastName(entity.getProfile().getLastName()).mobileNumber(entity.getProfile().getPhoneNo())
						.emailAddress(entity.getProfile().getEmail()).referenceId(ticketDto.getReferenceId()).build();
			}
		}
		String raisedBy = findHighAccessLevelGroupId(entity);
		log.info("Ticket raised by: {}", raisedBy);
		return ticketDto.toBuilder().firstName(ticketDto.getFirstName()).lastName(ticketDto.getLastName())
				.mobileNumber(ticketDto.getMobileNumber()).emailAddress(ticketDto.getEmailAddress()).category(ticketDto.getCategory())
				.subject(ticketDto.getSubject()).description(ticketDto.getDescription())
				.raisedBy(ObjectUtils.isEmpty(raisedBy) ? ObjectUtils.isNotEmpty(ticketDto.getEmailAddress()) ? ticketDto.getEmailAddress():ticketDto.getMobileNumber(): raisedBy).build();
	}

	public HttpHeaders toRedmineHeaderForTicketCreate(TicketDto ticketDto) {
		UserEntity entity = userService.getLoggedInUser();
		String clientId = ObjectUtils.isEmpty(entity) ? ticketDto.getMobileNumber(): redmineRegisteredUser;
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Client-Id", clientId);
		headers.add("X-Client-Type", this.getClientType().type);
		log.info("client id:{}, client type:{}", headers.get("X-Client-Id"),headers.get("X-Client-Type"));
		return headers;
	}
	
	public HttpHeaders toRedmineHeader(TicketDto ticketDto) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Client-Id", redmineRegisteredUser);
		headers.add("X-Client-Type", this.getClientType().type);
		log.info("client id:{}, client type:{}", headers.get("X-Client-Id"),headers.get("X-Client-Type"));
		return headers;
	}
	
	public HttpHeaders toRedmineHeaderForPagination(TicketDto ticketDto) {
		UserEntity entity = userService.getLoggedInUser();
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Client-Id", entity.getUserName());
		headers.add("X-Client-Type", this.getClientType().type);
		log.info("client id:{}, client type:{}", headers.get("X-Client-Id"),headers.get("X-Client-Type"));
		return headers;
	}
	
	public UserTypeEnum getClientType() {
		UserEntity entity = userService.getLoggedInUser();
		if(ObjectUtils.isNotEmpty(entity)) {
			List<UserGroupEntity> groups = userGroupRepository.findByIdUserIdAndStatus(entity.getId(), StatusEnum.ACTIVE.status);
			Set<String> names = groups.stream().map(g -> g.getGroup().getGroupName()).collect(Collectors.toSet());
			return names.contains(systemSupport) ? UserTypeEnum.TICKET_SUPPORT
					: names.contains(customerCare) ? UserTypeEnum.CUSTOMER_CARE : UserTypeEnum.PUBLIC;
		}
		return UserTypeEnum.PUBLIC;
	}

	public Map<String, String> toPaginationDto(PaginationRequestDto ulrd) {
		UserEntity entity = userService.getLoggedInUser();
		String loggedInUser = ObjectUtils.isNotEmpty(entity) ? entity.getUserName() : null;
		Map<String, String> params = new HashedMap<String, String>();
		params.put("page", String.valueOf(ulrd.getPage().getCurrent() - 1));
		params.put("size", String.valueOf(ulrd.getPage().getSize()));
		params.put("loggedInUser", loggedInUser);
		if (CollectionUtils.isNotEmpty(ulrd.getFilters())) {
			for (FilterDto filter : ulrd.getFilters()) {
				log.info("key: {}, value:{}", filter.getProperty(), filter.getValue());
				if(ObjectUtils.isNotEmpty(filter.getValue())) {
					if(filter.getProperty().equals("startDate") || filter.getProperty().equals("endDate")) {
						LocalDate startDate = dateUtil.toLocalDate(filter.getValue(), DateUtil._fronendFormat);
						params.put(filter.getProperty(), startDate.toString());
					} else {
						params.put(filter.getProperty(), filter.getValue());
					}
				}
			}
		}
		UserTypeEnum type = this.getClientType();
		if(ObjectUtils.isNotEmpty(params.get("clientType")) && params.get("clientType").equals("mine")) {
			params.put("clientType", type.type);
		} else {
			if(type!=UserTypeEnum.CUSTOMER_CARE)params.put("clientType", type.type);
		}
		return params;
	}
	public String findHighAccessLevelGroupId(UserEntity entity) {
		if(ObjectUtils.isEmpty(entity)) return null;
		List<UserGroupEntity> ugroups = userGroupRepository.findByIdUserId(entity.getId());
		List<GroupRoleEntity> groles = groupRoleRepository.findByIdGroupIdInAndStatus(
				ugroups.stream().map(g -> g.getId().getGroupId()).collect(Collectors.toSet()),
				StatusEnum.ACTIVE.status);
		return groles.stream().filter(g -> g.getRole().getAccess() == AccessEnum.all)
				.map(g -> g.getGroup().getId()).findAny()
				.orElse(groles.stream().filter(g -> g.getRole().getAccess() == AccessEnum.group)
						.map(g -> g.getGroup().getId()).findAny()
						.orElse(groles.stream().filter(g -> g.getRole().getAccess() == AccessEnum.self)
								.map(g -> g.getGroup().getId()).findAny().orElse(entity.getId()))).toString();
	}
}
