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
import com.gl.ceir.panel.dto.GroupDto;
import com.gl.ceir.panel.dto.ModuleTagDto;
import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.entity.GroupEntity;
import com.gl.ceir.panel.entity.ModuleTagEntity;
import com.gl.ceir.panel.service.GroupService;
import com.gl.ceir.panel.service.TagService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@RestController
@Slf4j
@RequestMapping("tag")
@AllArgsConstructor
public class TagController {
	@Autowired
	private final TagService tagService;
	
	@PostMapping("save")
	public @ResponseBody ModuleTagEntity save(@RequestBody ModuleTagDto tagDto, HttpServletRequest request) {
		return tagService.save(tagDto,request);
	}
	@GetMapping("/{id}")
	public @ResponseBody ModuleTagEntity getById(@PathVariable Long id){
		return tagService.getById(id);
	}
	@PutMapping("/update/{id}")
	public @ResponseBody ModuleTagEntity update(@RequestBody ModuleTagDto tagDto, @PathVariable Long id, HttpServletRequest request) {
		log.info("Tag:{},update request:{}", id, tagDto);
		return tagService.update(tagDto, id,request);
	}
	@DeleteMapping("/{id}")
	public @ResponseBody ModuleTagEntity delete(@PathVariable Long id, HttpServletRequest request) {
		return tagService.deleteById(id,request);
	}
	@PostMapping("pagination")
	public ResponseEntity<?> pagination(@RequestBody PaginationRequestDto ulrd) {
		return new ResponseEntity<>(tagService.pagination(ulrd), HttpStatus.OK);
	}
	@GetMapping("list")
	public ResponseEntity<?> findUsers() {
		return new ResponseEntity<>(tagService.getTags(), HttpStatus.OK);
	}
	@PostMapping("delete")
	public ResponseEntity<?> delete(@RequestBody List<Long> ids) {
		return new ResponseEntity<>(tagService.delete(ids), HttpStatus.OK);
	}
	@PostMapping("active")
	public ResponseEntity<?> active(@RequestBody List<Long> ids) {
		return new ResponseEntity<>(tagService.active(ids), HttpStatus.OK);
	}
}
