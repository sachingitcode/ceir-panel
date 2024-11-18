package com.gl.ceir.panel.service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gl.ceir.panel.dto.ChangePasswordDto;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.entity.UserPasswordHistoryEntity;
import com.gl.ceir.panel.repository.UserPasswordHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class UserPasswordService {
	private final UserPasswordHistoryRepository userPasswordHistoryRepository;
	private final PasswordEncoder encoder;
	private final AuthenticationManager authenticationManager;
	
	public UserPasswordHistoryEntity save(UserEntity user) {
		UserPasswordHistoryEntity password = UserPasswordHistoryEntity.builder().password(user.getPassword()).user(user).build();
		List<UserPasswordHistoryEntity> passwords = userPasswordHistoryRepository.findByUserId(user.getId());
		if (passwords.size() > 2) {
			password = passwords.stream()
					.max(Comparator.comparing(UserPasswordHistoryEntity::getId)).get();
			password.setPassword(user.getPassword());
		} 
		return userPasswordHistoryRepository.save(password);
	}
	public boolean isMatchedWithHistoryPassword(UserEntity user, ChangePasswordDto cpd) {
		boolean matched = false;
		Predicate<UserPasswordHistoryEntity> equal = e -> encoder.matches(cpd.getNewPassword(), e.getPassword());
		List<UserPasswordHistoryEntity> passwords = userPasswordHistoryRepository.findByUserId(user.getId());
		matched = passwords.stream().anyMatch(equal);
		log.info("Is password matched: {} with old password", matched);
		return matched;
	}
	public boolean isItTemparoryPassword(UserEntity user) {
		return ObjectUtils.isNotEmpty(user) ? userPasswordHistoryRepository.findByUserId(user.getId()).size() == 1: false;
	}
}
