package com.gl.ceir.common;

import org.springframework.stereotype.Component;

import com.gl.ceir.panel.dto.NotificationDto;
import com.gl.ceir.panel.dto.response.TicketResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationHelper {
	private final NotificationRepository notificationRepository;
	public void send(NotificationDto notification) {
		try {
			Object response = notificationRepository.sendemail(notification);
			log.info("Notification response:{}", response);
		}catch(Exception e) {
			log.error("Error while send notification:{}", e.getMessage());
		}
	}
}
