package com.gl.ceir.panel;

import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@SpringBootApplication
@EnableFeignClients({"com.gl.ceir.common","com.gl.ceir.panel.repository.remote"})
@ComponentScan({"com.gl.ceir.common","com.gl.ceir.panel"})
@EnableWebSecurity
public class UserApplication {
	@Value("${otp.expirty.time:15}")
	private int otpExpiryTime;
	
	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}
	@Bean
	public PassiveExpiringMap<String, String> otpMap() {
		PassiveExpiringMap<String, String> otpMap = new PassiveExpiringMap<>(otpExpiryTime, TimeUnit.SECONDS);
		return otpMap;
	}
}
