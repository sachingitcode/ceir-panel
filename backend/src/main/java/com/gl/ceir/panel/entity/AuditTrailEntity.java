package com.gl.ceir.panel.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity(name="audit_trail")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuditTrailEntity extends AbstractTimestampEntity implements Serializable{
	private static final long serialVersionUID = -7390461796309238430L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String browser;
	private String details;
	private Long featureId;
	private String featureName;
	private String jSessionId;
	private String publicIp;
	private String roleType;
	private String subFeature;
	private String txnId;
	private Long userId;
	private String userName;
	private String userType;
	private String userTypeId;
}
