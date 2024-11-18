package com.gl.ceir.panel.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = "userName") })
@JsonInclude(Include.NON_NULL)
public class UserEntity extends AbstractTimestampEntity implements Serializable {
	private static final long serialVersionUID = 6609861165334961349L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private UserEntity parent;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "profile_id", referencedColumnName = "id")
	@JsonManagedReference
	private UserProfileEntity profile;
	@Column(length = 100)
	private String password;
	private LocalDateTime passwordDate;
	private int previousStatus;
	private Long referenceId;
	@Column(length = 255)
	private String remark;
	@Column(length=2,columnDefinition = "varchar(3) default 'en'")
	private String userLanguage;
	@Column(length = 50, name="username")
	private String userName;
	private LocalDateTime lastLoginDate;
	private String approvedBy;
	private LocalDateTime approvedDate;
	private int failedAttempt;
	@Column(length=2,columnDefinition = "varchar(2) default '0'")
	private String currentStatus;	
	@Column(name = "created_by", updatable=true)
	private Long createdBy;
	private String modifiedBy;
	private Long userTypeId;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	@NotFound(action = NotFoundAction.IGNORE)
	private Set<RoleEntity> roles;
	@JsonManagedReference
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumns({
        @JoinColumn(name = "user_id", referencedColumnName = "id")
    })
	private java.util.List<UserSecurityQuestionEntity> questions;
}
