package com.gl.ceir.panel.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.gl.ceir.panel.dto.TicketCategoryDto;
import com.gl.ceir.panel.entity.TicketCategoryEntity;
import com.gl.ceir.panel.repository.TicketCategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Service
@Slf4j
@RequiredArgsConstructor()
public class TicketCategoryService {
	private final TicketCategoryRepository ticketCategoryRepository;
	
	public TicketCategoryEntity save(TicketCategoryDto dto) {
		TicketCategoryEntity tcd = TicketCategoryEntity.builder().build();
		BeanUtils.copyProperties(dto, tcd);
		return ticketCategoryRepository.save(tcd);
	}
	public List<TicketCategoryEntity> categories() {
		return ticketCategoryRepository.findAll();
	}
}
