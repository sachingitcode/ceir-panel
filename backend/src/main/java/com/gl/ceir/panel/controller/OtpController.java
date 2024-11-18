package com.gl.ceir.panel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gl.ceir.panel.dto.request.OtpDto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("otp")
public class OtpController {
	@GetMapping("send/{mobileNumber}")
	public ResponseEntity<?> save(@PathVariable String mobileNumber) {
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("verify")
	public ResponseEntity<?> verify(@RequestBody OtpDto otpDto) {
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
