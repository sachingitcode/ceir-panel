package com.gl.ceir.panel.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.gl.ceir.common.OtpUtil;
import com.gl.ceir.panel.controller.OtpController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@SuppressWarnings("unused")
@Slf4j
@AllArgsConstructor
@Service
public class OtpService {
	private final OtpUtil otpUtil;
}
