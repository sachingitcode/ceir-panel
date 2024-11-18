package com.gl.ceir.panel.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import com.gl.ceir.panel.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailValidator implements ConstraintValidator<EmailConstraint, String> {

	@Override
	public void initialize(EmailConstraint email) {

	}

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		return UserService.getUserService().emailExist(email);
	}

}
