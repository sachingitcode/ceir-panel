package com.gl.ceir.panel.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity(name = "role_feature_module_access")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RoleFeatureModuleAccessEntity extends AbstractTimestampEntity implements Serializable {
	private static final long serialVersionUID = 7944319811942160951L;
	@EmbeddedId
	private RoleFeatureModuleAccessId id;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns({ @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false, insertable=false, updatable=false) })
	@JsonManagedReference
	private RoleEntity role;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns({ @JoinColumn(name = "feature_id", referencedColumnName = "id", nullable = false, insertable=false, updatable=false) })
	@JsonManagedReference
	private FeatureEntity feature;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns({ @JoinColumn(name = "module_id", referencedColumnName = "id", nullable = false, insertable=false, updatable=false) })
	@JsonManagedReference
	private ModuleEntity module;
	
	@Column(length = 255)
	private String featureModuleOverrideLink;
	@Column(name = "created_by", updatable=false)
	private Long createdBy;
	private Long modifiedBy;
	@Column(length=2,columnDefinition = "varchar(2) default '0'")
	private String status;
}
