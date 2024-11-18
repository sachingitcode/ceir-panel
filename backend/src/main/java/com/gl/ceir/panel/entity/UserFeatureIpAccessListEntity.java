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

@Entity(name = "user_feature_ip_access_list")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserFeatureIpAccessListEntity extends AbstractTimestampEntity implements Serializable{
	private static final long serialVersionUID = -1555056622843202094L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Long featureIpListId;
	private Long userId;
	@Column(length = 500)
	private String ipAddress;
	@Column(name = "created_by", updatable=false)
	private Long createdBy;
	private Long modifiedBy;
}
