package com.gl.ceir.panel.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gl.ceir.panel.constant.OtpChannelTypeEnum;
import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.dto.UserCreateDto;
import com.gl.ceir.panel.dto.UserUpdateDto;
import com.gl.ceir.panel.service.UserPermissionService;
import com.gl.ceir.panel.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@Slf4j
@RequestMapping("user")
@RequiredArgsConstructor
@Api(produces = "application/json", value = "Operations pertaining to manager users in the application")
public class UserController {
	private final UserService userService;
	private final UserPermissionService userPermissionService;

	@Value("${user.for.testing:}")
	private String userForTesting;

	@ApiIgnore
	@ApiOperation(value = "Create a new user", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully created a new user"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Application failed to process the request") })
	@PostMapping("save")
	public ResponseEntity<?> save(@Valid @ModelAttribute UserCreateDto ucd, HttpServletRequest request) {
		log.info("User create request:{}", ucd);
		return userService.save(ucd, request);
	}

	@RequestMapping(path = "/update/{id}", method = { RequestMethod.PUT, RequestMethod.POST }, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> update(@Valid @ModelAttribute UserUpdateDto userDto, @PathVariable Long id,
			HttpServletRequest request) {
		return new ResponseEntity<>(userService.update(userDto, id, request), HttpStatus.OK);
	}

	@PostMapping("pagination")
	public ResponseEntity<?> pagination(@RequestBody PaginationRequestDto ulrd) {
		return new ResponseEntity<>(userService.pagination(ulrd), HttpStatus.OK);
	}

	@RequestMapping("{id}")
	public ResponseEntity<?> view(@PathVariable Long id) {
		return new ResponseEntity<>(userService.viewById(id), HttpStatus.OK);
	}
	
	@RequestMapping("updateEmailAndMsisdn/{id}/{email}/{msisdn}")
	public ResponseEntity<?> updateEmailandMsisdn(@PathVariable Long id, @PathVariable String email, @PathVariable String msisdn) {
		return new ResponseEntity<>(userService.updateEmailAndMsisdn(id, email,msisdn), HttpStatus.OK);
	}

	@GetMapping("list")
	public ResponseEntity<?> findUsers() {
		return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
	}

	@GetMapping("permissions")
	public ResponseEntity<?> permissions() {
		return new ResponseEntity<>(userPermissionService.permissions().getPermissions(), HttpStatus.OK);
	}

	@GetMapping("isAlertForPasswordExpire")
	public ResponseEntity<?> isAlertForPasswordExpire() {
		return new ResponseEntity<>(userService.isAlertForPasswordExpire(), HttpStatus.OK);
	}

	@PostMapping("delete")
	public ResponseEntity<?> delete(@RequestBody List<Long> ids, HttpServletRequest request) {
		return new ResponseEntity<>(userService.delete(ids,request), HttpStatus.OK);
	}

	@PostMapping("active")
	public ResponseEntity<?> active(@RequestBody List<Long> ids, HttpServletRequest request) {
		return new ResponseEntity<>(userService.active(ids,request), HttpStatus.OK);
	}

	@PostMapping("reset-password")
	public ResponseEntity<?> resetpassword(@RequestBody List<Long> ids, HttpServletRequest request) {
		return userService.resetpassword(ids,request);
	}

	@GetMapping("getUserType")
	public ResponseEntity<?> getUserType(HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();
		map.put("userType", userService.findUserType().type);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@GetMapping("findUsersByUserNameForTickets/{userName}")
	public ResponseEntity<?> getUserType(@PathVariable String userName) {
		userName = ObjectUtils.isNotEmpty(userForTesting) ? userForTesting : userName;
		log.info("Username:{}", userName);
		return userService.users(userName);
	}
	@GetMapping("send-otp/{otpchannel}/{emailormsisdn}")
	public ResponseEntity<?> sendotp(@PathVariable OtpChannelTypeEnum otpchannel,@PathVariable String emailormsisdn) {
		log.info("Otp request channel:{}, receiver id:{}", otpchannel, emailormsisdn);
		return new ResponseEntity<>(userService.sendotp(otpchannel,emailormsisdn), HttpStatus.OK);
	}
	@GetMapping("verify-otp/{emailormsisdn}/{otp}")
	public ResponseEntity<?> sendotp(@PathVariable String emailormsisdn, @PathVariable String otp) {
		log.info("Otp request receiver id:{}",emailormsisdn);
		return new ResponseEntity<>(userService.verifyOtp(emailormsisdn, otp), HttpStatus.OK);
	}
}
