package com.gl.ceir.macra;

import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableFeignClients({"com.gl.ceir.common"})
@ComponentScan({"com.gl.ceir.common","com.gl.ceir.macra"})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class MacraApplication {
	@Value("${otp.expiry.time:60}")
	private int otpExpiryTime;
	
	public static void main(String[] args) {
		SpringApplication.run(MacraApplication.class, args);
	}
	@Bean
	public PassiveExpiringMap<String, String> otpMap() {
		PassiveExpiringMap<String, String> otpMap = new PassiveExpiringMap<>(otpExpiryTime, TimeUnit.SECONDS);
		return otpMap;
	}
}
