package com.gl.ceir.panel.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gl.ceir.panel.entity.SecurityQuestionEntity;
import com.gl.ceir.panel.repository.SecurityQuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor()
public class SecurityQuestionService {
	
	private final SecurityQuestionRepository securityQuestionRepository;
	public List<SecurityQuestionEntity> list(){
		return securityQuestionRepository.findAll();
	}
}
