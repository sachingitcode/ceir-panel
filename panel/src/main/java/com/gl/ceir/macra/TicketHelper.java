package com.gl.ceir.macra;

import java.time.LocalDate;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.gl.ceir.panel.constant.UserTypeEnum;
import com.gl.ceir.panel.dto.FilterDto;
import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.dto.TicketDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TicketHelper {
	private final DateUtil dateUtil;
	@Value("${eirs.panel.redmine.registered.user:DUMMY_AGENT}")
	private String redmineRegisteredUser;
	public TicketDto toRedmineDto(TicketDto ticketDto) {
		return ticketDto.toBuilder().firstName(ticketDto.getFirstName()).lastName(ticketDto.getLastName())
				.mobileNumber(ticketDto.getMobileNumber()).emailAddress(ticketDto.getEmailAddress()).category(ticketDto.getCategory())
				.subject(ticketDto.getSubject()).description(ticketDto.getDescription())
				.raisedBy(ObjectUtils.isEmpty(ticketDto.getEmailAddress()) ? ticketDto.getMobileNumber(): ticketDto.getEmailAddress())
				.build();
	}
	public HttpHeaders toRedmineHeader(TicketDto ticketDto) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Client-Id", redmineRegisteredUser);
		headers.add("X-Client-Type", UserTypeEnum.PUBLIC.type);
		log.info("client id:{}, client type:{}", headers.get("X-Client-Id"),headers.get("X-Client-Type"));
		return headers;
	}
	
	public Map<String, String> toPaginationDto(PaginationRequestDto ulrd) {
		String loggedInUser = null;
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
		UserTypeEnum type = UserTypeEnum.PUBLIC;
		if(ObjectUtils.isNotEmpty(params.get("clientType")) && params.get("clientType").equals("mine")) {
			params.put("clientType", type.type);
		} else {
			if(type!=UserTypeEnum.CUSTOMER_CARE)params.put("clientType", type.type);
		}
		return params;
	}
}
