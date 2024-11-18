package com.gl.ceir.panel.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gl.ceir.panel.entity.LinkEntity;
import com.gl.ceir.panel.service.LinkService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/link")
public class LinkController {
	private final LinkService linkService;

	@PostMapping("/save")
	public ResponseEntity<?> registerUser(@Valid @RequestBody LinkEntity linkEntity) {
		return ResponseEntity.ok(linkService.save(linkEntity));
	}

	@GetMapping("list")
	public ResponseEntity<?> list() {
		return new ResponseEntity<>(linkService.links(), HttpStatus.OK);
	}
}
