package com.gl.ceir.panel.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.gl.ceir.panel.validation.EmailConstraint;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserCreateDto implements Serializable {
	private static final long serialVersionUID = 7887191827671006846L;
	private Long id;
	@NotBlank(message = "First name is mandatory")
	private String firstName;
	private String lastName;
	@NotBlank(message = "Address is mandatory")
	private String address;
	@NotBlank(message = "Country is mandatory")
	private String country;
	private String stateName;
	@NotBlank(message = "District is mandatory")
	private String district;
	private String commune;
	private String village;
	private String street;
	private String locality;
	private String province;
	@NotNull(message = "Postal code is mandatory")
	private String postalCode;
	private String companyName;
	private String designation;
	@NotBlank(message = "National ID is mandatory")
	private String nationalId;
	private String employeeId;
	private String idCardFileName;
	private String authorityName;
	private String authorityEmail;
	private String authorityPhoneNo;
	@NotBlank(message = "Password is mandatory")
	private String password;
	@EmailConstraint(message = "emailAlreadyRegistered")
	private String email;
	private String userName;
	@NotBlank(message = "Phone no is mandatory")
	private String phoneNo;
	@NotBlank(message = "Question1 is mandatory")
	private String question1;
	@NotBlank(message = "Question2 is mandatory")
	private String question2;
	@NotBlank(message = "Question3 is mandatory")
	private String question3;
	@NotBlank(message = "Answer1 is mandatory")
	private String answer1;
	@NotBlank(message = "Answer2 is mandatory")
	private String answer2;
	@NotBlank(message = "Answer3 is mandatory")
	private String answer3;
	private MultipartFile nidFile;
	private MultipartFile idCardFile;
	private MultipartFile photoFile;
}
