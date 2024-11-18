package com.gl.ceir.panel.controller;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gl.ceir.panel.constant.AccessEnum;
import com.gl.ceir.panel.constant.CategoryEnum;
import com.gl.ceir.panel.repository.CountryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@RestController
@Slf4j
@RequestMapping("country")
@RequiredArgsConstructor
public class CountryController {
	private final CountryRepository countryRepository;

	@GetMapping("list")
	public @ResponseBody ResponseEntity<?> accessList() {
		return new ResponseEntity<>(countryRepository.findAll(), HttpStatus.OK);
	}
}
