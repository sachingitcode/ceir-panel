package com.gl.ceir.panel.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity(name="user_role")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class UserRoleEntity extends AbstractTimestampEntity implements Serializable{
	private static final long serialVersionUID = 6557553369596485058L;
	@EmbeddedId
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UserRoleId id;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns({ @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable=false, updatable=false) })
	private UserEntity user;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns({ @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false, insertable=false, updatable=false) })
	private RoleEntity role;
	
	@Column(name = "created_by", updatable=false)
	private Long createdBy;
	private Long modifiedBy;
	@Column(length=2,columnDefinition = "varchar(2) default '0'")
	private String status;
}