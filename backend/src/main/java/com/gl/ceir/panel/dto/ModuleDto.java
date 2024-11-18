package com.gl.ceir.panel.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.gl.ceir.panel.entity.AbstractTimestampEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ModuleDto extends AbstractTimestampEntity implements Serializable {
	private static final long serialVersionUID = 7473275252882625029L;
	private Long id;
	@NotBlank(message = "Name is mandatory")
	private String moduleName;
	private String description;
	private Long moduleTagId;
}
