package com.gl.ceir.panel.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.gl.ceir.panel.dto.DeviceRequest;
import com.gl.ceir.panel.dto.FeatureDto;
import com.gl.ceir.panel.dto.GroupDto;
import com.gl.ceir.panel.dto.ModuleDto;
import com.gl.ceir.panel.dto.ModuleTagDto;
import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.entity.FeatureEntity;
import com.gl.ceir.panel.entity.GroupEntity;
import com.gl.ceir.panel.entity.ModuleEntity;
import com.gl.ceir.panel.entity.ModuleTagEntity;
import com.gl.ceir.panel.service.FeatureService;
import com.gl.ceir.panel.service.GroupService;
import com.gl.ceir.panel.service.ModuleService;
import com.gl.ceir.panel.service.TagService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@RestController
@Slf4j
@RequestMapping("module")
@AllArgsConstructor
public class ModuleController {
	private final ModuleService moduleService;
	
	@PostMapping("save")
	public ResponseEntity<?> save(@RequestBody ModuleDto moduleDto, HttpServletRequest request) {
		return new ResponseEntity<>(moduleService.save(moduleDto,request), HttpStatus.OK);
	}
	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id){
		return new ResponseEntity<>(moduleService.getById(id), HttpStatus.OK);
	}
	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(@RequestBody ModuleDto moduleDto, @PathVariable Long id, HttpServletRequest request) {
		log.info("Feature:{},update request:{}", id, moduleDto);
		return new ResponseEntity<>(moduleService.update(moduleDto, id,request), HttpStatus.OK);
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request) {
		return new ResponseEntity<>(moduleService.deleteById(id,request), HttpStatus.OK);
	}
	@PostMapping("pagination")
	public ResponseEntity<?> pagination(@RequestBody PaginationRequestDto ulrd) {
		return new ResponseEntity<>(moduleService.pagination(ulrd), HttpStatus.OK);
	}
	@GetMapping("list")
	public ResponseEntity<?> findModules() {
		return new ResponseEntity<>(moduleService.getModules(), HttpStatus.OK);
	}
	@PostMapping("delete")
	public ResponseEntity<?> delete(@RequestBody List<Long> ids) {
		return new ResponseEntity<>(moduleService.delete(ids), HttpStatus.OK);
	}
	@PostMapping("active")
	public ResponseEntity<?> active(@RequestBody List<Long> ids) {
		return new ResponseEntity<>(moduleService.active(ids), HttpStatus.OK);
	}
}
