package com.gl.ceir.panel.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gl.ceir.panel.entity.LinkEntity;
import com.gl.ceir.panel.repository.LinkRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Service
@Slf4j
@RequiredArgsConstructor()
public class LinkService {
	private final LinkRepository linkRepository;
	

	public List<LinkEntity> links() {
		return linkRepository.findAll();
	}
	public LinkEntity save(LinkEntity linkEntity) {
		return linkRepository.save(linkEntity);
	}
}
