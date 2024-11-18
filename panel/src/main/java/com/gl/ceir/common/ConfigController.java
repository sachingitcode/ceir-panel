package com.gl.ceir.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import com.gl.ceir.panel.dto.PaginationRequestDto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@RestController
@Slf4j
@RequestMapping("config")
@AllArgsConstructor
@NoArgsConstructor
public class ConfigController {
	@Value("${eirs.site.key:}")
	private String siteKey;
	@Value("${eirs.portal.link:}")
	private String eirsPortalLink;
	@Value("${email.regex:}")
	private String emailRegex;
	@Value("${mobile.regex:}")
	private String mobileRegex;
	@Value("${mdr.portal.url:}")
	private String mdrPortalUrl;
	
	@PostMapping("system/pagination")
	public ResponseEntity<?> pagination(@RequestBody PaginationRequestDto ulrd) {
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	@GetMapping("frontend")
	public ResponseEntity<?> frontend() {
		return new ResponseEntity<>(readFrontendConfig(), HttpStatus.OK);
	}
	
	private Map<String, Object> readFrontendConfig(){
		Map<String, Object> map = new HashMap<>();
		map.put("siteKey", siteKey);
		map.put("eirsPortalLink", eirsPortalLink);
		map.put("emailRegex", emailRegex);
		map.put("mobileRegex", mobileRegex);
		map.put("mdrPortalUrl", mdrPortalUrl);
		return map;
	}
}
