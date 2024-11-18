package com.gl.ceir.panel.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.dto.RoleDto;
import com.gl.ceir.panel.entity.RoleEntity;
import com.gl.ceir.panel.service.RoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("role")
@RequiredArgsConstructor
public class RoleController {
	private final RoleService roleService;

	@PostMapping("save")
	public @ResponseBody RoleEntity save(@RequestBody RoleDto roleDto, HttpServletRequest request) {
		return roleService.save(roleDto,request);
	}

	@GetMapping("list")
	public ResponseEntity<?> findRoles() {
		return new ResponseEntity<>(roleService.getRoles(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public @ResponseBody RoleEntity getById(@PathVariable Long id) {
		return roleService.getById(id);
	}

	@PutMapping("/update/{id}")
	public @ResponseBody RoleEntity update(@RequestBody RoleDto roleDto, @PathVariable Long id, HttpServletRequest request) {
		log.info("Role:{},update request:{}", id, roleDto);
		return roleService.update(roleDto, id,request);
	}

	@DeleteMapping("/{id}")
	public @ResponseBody RoleEntity delete(@PathVariable Long id, HttpServletRequest request) {
		return roleService.deleteById(id,request);
	}

	@PostMapping("pagination")
	public ResponseEntity<?> pagination(@RequestBody PaginationRequestDto ulrd) {
		return new ResponseEntity<>(roleService.pagination(ulrd), HttpStatus.OK);
	}
	@PostMapping("delete")
	public ResponseEntity<?> delete(@RequestBody List<Long> ids) {
		return new ResponseEntity<>(roleService.delete(ids), HttpStatus.OK);
	}
	@PostMapping("active")
	public ResponseEntity<?> active(@RequestBody List<Long> ids) {
		return new ResponseEntity<>(roleService.active(ids), HttpStatus.OK);
	}
}
