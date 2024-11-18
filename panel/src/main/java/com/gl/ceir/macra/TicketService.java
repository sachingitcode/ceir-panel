package com.gl.ceir.macra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gl.ceir.common.NotificationHelper;
import com.gl.ceir.common.OtpUtil;
import com.gl.ceir.common.RedmineRemoteRepository;
import com.gl.ceir.panel.constant.OtpChannelTypeEnum;
import com.gl.ceir.panel.dto.NotificationDto;
import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.dto.TicketDto;
import com.gl.ceir.panel.dto.TicketNoteDto;
import com.gl.ceir.panel.dto.TicketRateDto;
import com.gl.ceir.panel.dto.response.ApiStatusDto;
import com.gl.ceir.panel.dto.response.PaginationDto;
import com.gl.ceir.panel.dto.response.RedminUploadDto;
import com.gl.ceir.panel.dto.response.TicketResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketService {
	private final TicketHelper ticketHelper;
	private final RedmineRemoteRepository redmineRemoteRepository;
	private final OtpUtil otpUtil;
	private final NotificationHelper notificationHelper;
	private final PassiveExpiringMap<String, String> otpMap;
	@Value("${otp.message:Please use this otp: %s to access the tickets}")
	private String otpMessage;
	private static final String otpChannelType = "SMS";
	
	@Value("${ticket.create.sms:Your Ticket registered successfully with the Ticket id %s}")
	private String ticketCreateSms;
	@Value("${ticket.create.email:Your Ticket registered successfully with the Ticket id %s}")
	private String ticketCreateEmail;
	@Value("${ticket.create.email.subject:Ticekt Successfully Registered}")
	private String ticketCreateEmailSubject;
	
	public TicketResponseDto save(TicketDto ticketDto) {
		TicketResponseDto ticketResponse = TicketResponseDto.builder().build();
		try {
			if (ObjectUtils.isNotEmpty(ticketDto.getDocuments())) {
				List<RedminUploadDto> attachments = new ArrayList<RedminUploadDto>();
				for(MultipartFile multipart: ticketDto.getDocuments()) {
					Map<String, RedminUploadDto> upload = redmineRemoteRepository
							.upload(Arrays.asList(multipart), ticketHelper.toRedmineHeader(ticketDto));
					log.info("File:{},Content type:{}, response:{}", multipart.getOriginalFilename(), multipart.getContentType(), upload);
					attachments.add(RedminUploadDto.builder().token(upload.get("upload").getToken())
							.filename(multipart.getOriginalFilename()).contenttype(multipart.getContentType()).build());
				}
				ticketDto.setDocuments(null);
				ticketDto.setAttachments(attachments);
			}
			ticketDto.setPrivate(ticketDto.isResolved());
			log.info("Request: {}", ticketDto);
			ticketDto = ticketHelper.toRedmineDto(ticketDto);
			log.info("Payload: {}", ticketDto);
			ticketResponse = redmineRemoteRepository.createTicket(ticketDto, ticketHelper.toRedmineHeader(ticketDto));
			if (ticketDto.isResolved()) {
				log.info("private note:{}", ticketDto.isPrivateNotes());
				redmineRemoteRepository.rosolve("RESOLVED", ticketResponse.getTicketId(), ticketHelper.toRedmineHeader(
						TicketDto.builder().mobileNumber(ticketDto.getUserId()).userType(ticketDto.getUserType()).build()));
			}
			log.info("Redmin response:{}", ticketDto);
			NotificationDto smsrequest = NotificationDto.builder().channelType(OtpChannelTypeEnum.SMS.name())
					.message(String.format(ticketCreateSms, ticketResponse.getTicketId()))
					.msisdn(ticketDto.getMobileNumber()).build();
			NotificationDto emailrequest = NotificationDto.builder().channelType(OtpChannelTypeEnum.EMAIL.name())
					.message(String.format(ticketCreateEmail, ticketResponse.getTicketId()))
					.msisdn(ticketDto.getMobileNumber()).email(ticketDto.getEmailAddress()).build();
			notificationHelper.send(smsrequest);
			if(ObjectUtils.isNotEmpty(ticketDto.getEmailAddress())) notificationHelper.send(emailrequest);
		}catch(Exception e) {
			log.error("Error while save:{} ticket", e.getMessage());
		}
		return ticketResponse;
	}
	public TicketResponseDto saveNote(TicketNoteDto note) {
		log.info("ticket note: {}", note);
		TicketResponseDto ticket = redmineRemoteRepository.view(note.getTicketId(),
				ticketHelper.toRedmineHeader(TicketDto.builder().build()));
		HttpHeaders headers = ticketHelper.toRedmineHeader(
				TicketDto.builder().mobileNumber(ticket.getUserId()).userType(ticket.getUserType()).build());
		headers.remove("X-Client-Id");
		headers.add("X-Client-Id", ticket.getUserId());
		log.info("Ticket:{},headers:{}",ticket.getTicketId(), headers);
		
		if (ObjectUtils.isNotEmpty(note.getAttachments())) {
			redmineRemoteRepository.upload(Arrays.asList(note.getAttachments()),headers);
		}
		note.setAttachments(null);
		redmineRemoteRepository.note(note.getTicketId(), note, headers);
		return redmineRemoteRepository.view(note.getTicketId(),headers);
	}
	public TicketResponseDto resolve(String ticketId) {
		TicketResponseDto ticket = redmineRemoteRepository.view(ticketId,
				ticketHelper.toRedmineHeader(TicketDto.builder().build()));
		HttpHeaders headers = ticketHelper.toRedmineHeader(
				TicketDto.builder().mobileNumber(ticket.getUserId()).userType(ticket.getUserType()).build());
		headers.remove("X-Client-Id");
		headers.add("X-Client-Id", ticket.getUserId());
		log.info("Ticket:{},headers:{}",ticket.getTicketId(), headers);
		
		redmineRemoteRepository.rosolve("RESOLVED", ticketId, headers);
		return redmineRemoteRepository.view(ticketId,headers);
	}

	public String saveRate(TicketRateDto rate) {
		TicketResponseDto ticket = redmineRemoteRepository.view(rate.getTicketId(),
				ticketHelper.toRedmineHeader(TicketDto.builder().build()));
		HttpHeaders headers = ticketHelper.toRedmineHeader(
				TicketDto.builder().mobileNumber(ticket.getUserId()).userType(ticket.getUserType()).build());
		headers.remove("X-Client-Id");
		headers.add("X-Client-Id", ticket.getUserId());
		log.info("Ticket:{},headers:{}",ticket.getTicketId(), headers);
		
		String result = redmineRemoteRepository.rate(rate.getTicketId(), rate, headers);
		log.info("ticket rate: {}, result:{}", rate, result);
		return result;
	}
	public PaginationDto pagination(PaginationRequestDto ulrd) {
		log.info("Request: {}", ulrd);
		Map<String, String> params = ticketHelper.toPaginationDto(ulrd);
		log.info("Params: {}", params);
		HttpHeaders headers = ticketHelper.toRedmineHeader(TicketDto.builder().build());
		headers.add("loggedInUser", params.get("loggedInUser"));
		return redmineRemoteRepository.pagination(params.get("startDate"), params.get("endDate"),
				params.get("ticketId"), params.get("mobileNumber"), params.get("ticketStatus"),
				params.get("clientType"), params.get("raisedBy"), params.get("loggedInUser"), ulrd.getPage().getCurrent() - 1, ulrd.getPage().getSize(),
				headers);
	}
	public PaginationDto paginationByMsisdn(PaginationRequestDto ulrd, String msisdn) {
		log.info("Request: {}", ulrd);
		Map<String, String> params = ticketHelper.toPaginationDto(ulrd);
		log.info("Params: {}", params);
		HttpHeaders headers = ticketHelper.toRedmineHeader(TicketDto.builder().build());
		return redmineRemoteRepository.ticketByMsisdn(msisdn, ulrd.getPage().getCurrent() - 1, ulrd.getPage().getSize(),
				headers);
	}
	public ApiStatusDto getById(String id) {
		log.info("ticket id: {}", id);
		TicketResponseDto ticketDto = null;
		try {
			ticketDto = redmineRemoteRepository.view(id,ticketHelper.toRedmineHeader(TicketDto.builder().build()));
			log.info("ticket dto: {}", ticketDto);
		} catch (Exception e) {
			log.error("Ticket id:{}, not exist:{}", id, e.getMessage());
		}
		return ApiStatusDto.builder().message(ObjectUtils.isEmpty(ticketDto) ? "notValidTicketId" : "").data(ticketDto)
				.build();
	}
	public TicketResponseDto forgotTicket(String msisdn) {
		return null;
	}
	public ApiStatusDto sendotp(String msisdn) {
		try {
			String otp = otpUtil.phoneOtp(msisdn);
			log.info("Generated otp:{}, for msisdn:{}", otp, msisdn);
			notificationHelper.send(NotificationDto.builder().channelType(otpChannelType)
								.message(String.format(otpMessage, otp)).msisdn(msisdn).build());
			otpMap.put(msisdn, otp);
			log.info("otp map:{}", otpMap);
			return ApiStatusDto.builder().message("sendOtpSuccess").build();
		}catch(Exception e) {
			e.printStackTrace();
			return ApiStatusDto.builder().message("sendOtpFailed").build();
		}
	}

	public ApiStatusDto verifyOtp(String msisdn, String otp) {
		PaginationDto tickes = redmineRemoteRepository.ticketByMsisdn(msisdn,0,10,
				ticketHelper.toRedmineHeader(TicketDto.builder().mobileNumber("DUMMY_AGENT").build()));
		String sentotp = otpMap.get(msisdn);
		log.info("Recieved otp:{}, for msisdn:{},size:{},cache otp:{}", otp, msisdn, tickes.getContent().size(),sentotp);
		return ObjectUtils.isNotEmpty(sentotp) && otp.equals(sentotp)
				? ApiStatusDto.builder().message("verifyOtpSuccess").size(tickes.getContent().size())
						.id(CollectionUtils.isNotEmpty(tickes.getContent()) ? tickes.getContent().get(0).getTicketId() : "").build()
				: ApiStatusDto.builder().message("verifyOtpFailed").size(tickes.getContent().size())
						.id(CollectionUtils.isNotEmpty(tickes.getContent()) ? tickes.getContent().get(0).getTicketId() : "").build();
	}
}
