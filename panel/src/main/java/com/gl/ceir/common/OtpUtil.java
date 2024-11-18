package com.gl.ceir.common;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OtpUtil {
	@Value("${is.testing.enabled:true}")
	private boolean isTestingEnabled;
	private final static String SALTCHARS = "1234567890";
	public String phoneOtp(String mobile) {
		log.info("inside phone otp");
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 4) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String otpval = salt.toString();
		return isTestingEnabled ? "1234" : otpval;
	}
}
