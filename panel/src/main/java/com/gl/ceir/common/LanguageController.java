package com.gl.ceir.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/language")
@CrossOrigin
public class LanguageController {
	private final LanguageService languageService;
	@GetMapping("/{language}")
	public ResponseEntity<?> getById(@PathVariable String language) {
		log.info("language: {}", language);
		return new ResponseEntity<>(languageService.languagejson(language), HttpStatus.OK);
	}
}
