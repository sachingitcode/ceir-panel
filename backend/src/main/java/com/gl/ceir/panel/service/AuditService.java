package com.gl.ceir.panel.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.gl.ceir.common.NotificationRepository;
import com.gl.ceir.panel.dto.AuditTrailDto;

import lombok.RequiredArgsConstructor;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class AuditService {
	private final NotificationRepository notificationRepository;
	
	public void audit(HttpServletRequest request, AuditTrailDto audit) {
		
	}
}
