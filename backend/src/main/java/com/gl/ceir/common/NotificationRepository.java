package com.gl.ceir.common;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gl.ceir.panel.dto.AuditTrailDto;
import com.gl.ceir.panel.dto.NotificationDto;

@Service
@FeignClient(url = "${notification.feign.client.path}", value = "notificationRepository")
public interface NotificationRepository {
	@RequestMapping(value = "/addNotifications", method = RequestMethod.POST)
	public Object sendemail(@RequestBody NotificationDto notification);
	
	@RequestMapping(value = "/auditTrailServiceInitialization", method = RequestMethod.POST)
	public Object audit(@RequestBody AuditTrailDto auditTrail);
}
