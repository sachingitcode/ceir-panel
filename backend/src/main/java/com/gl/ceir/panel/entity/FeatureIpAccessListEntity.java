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

@Entity(name = "feature_ip_access_list")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FeatureIpAccessListEntity extends AbstractTimestampEntity implements Serializable {
	private static final long serialVersionUID = 7944319811942160951L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Long featureIpListId;
	private Long featureId;
	@Column(length = 500)
	private String ipAddres;
	private Long listType;
	private Long typeOfCheck;
	private Long createdBy;
	private Long modifiedBy;
}
