package com.gl.ceir.macra;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.AllArgsConstructor;

@Configuration
@EnableMethodSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class WebSecurityConfig {
	@Value("${white.list.ip.list:http://localhost:4200}")
	private List<String> whiteListIpList;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		String[] resources = new String[] { "/**" };
		http.cors().configurationSource(corsConfigurationSource()).and().csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers(new RegexRequestMatcher("/api/auth/signup", "POST"))
						.permitAll().antMatchers(resources).permitAll().antMatchers("/public/**").permitAll()
						.requestMatchers(new RegexRequestMatcher("/link/save", "POST")).permitAll()
						.requestMatchers(new RegexRequestMatcher("/api/auth/signin", "POST")).permitAll()
						.requestMatchers(new RegexRequestMatcher("/ws", "GET")).permitAll()
						.requestMatchers(new RegexRequestMatcher("/api/test/*", "POST")).permitAll().anyRequest()
						.authenticated());

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.setAllowedOrigins(whiteListIpList);
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
