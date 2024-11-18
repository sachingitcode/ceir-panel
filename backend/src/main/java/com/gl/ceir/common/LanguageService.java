package com.gl.ceir.common;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor()
public class LanguageService {
	@Value("${eirs.panel.source.path:}")
	private String sourcePath;
	private final static String _LANGUAGE = "language";
	private final ObjectMapper objectMapper;

	public Object languagejson(String language) {
		Object json = null;
		try {
			log.info("language:{},path:{}", language,
					(sourcePath + File.separator + _LANGUAGE + File.separator + language));
			json = objectMapper.readValue(new File(sourcePath + File.separator + _LANGUAGE + File.separator + language),
					Object.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
}
