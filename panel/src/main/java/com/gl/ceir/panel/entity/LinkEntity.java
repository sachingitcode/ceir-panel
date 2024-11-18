package com.gl.ceir.panel.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity(name="links_entity")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class LinkEntity extends AbstractTimestampEntity implements Serializable{
	private static final long serialVersionUID = -6525112908440677714L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(length = 32)
	private String linkName;
	@Column(length = 32, unique=true)
	private String url;
	@Column(length = 32)
	private String icon;
	@Column(name = "created_by", updatable=false)
	private Long createdBy;
	private Long modifiedBy;
}
