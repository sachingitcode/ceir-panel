package com.gl.ceir.panel.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@Table(name = "user_profile", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
public class UserProfileEntity extends AbstractTimestampEntity implements Serializable {
	private static final long serialVersionUID = 2388074391392948453L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne(mappedBy = "profile")
	@JsonBackReference
	private UserEntity user;

	@Column(length = 255)
	private String authorityEmail;

	@Column(length = 100)
	private String authorityName;

	@Column(length = 20)
	private String authorityPhoneNo;

	@Column(length = 50)
	private String commune;

	@Column(length = 255)
	private String companyName;

	@Column(length = 100)
	private String country;

	@Column(length = 255)
	private String designation;

	@Column(length = 50)
	private String displayName;

	@Column(length = 50)
	private String district;

	@Column(length = 255, unique = true)
	private String email;

	@Column(length = 10)
	private String emailOtp;

	@Column(length = 20)
	private String employeeId;

	private LocalDateTime expiryDate;

	@Column(length = 50)
	private String firstName;

	@Column(length = 100, name="id_card_filename")
	private String idCardFileName;

	@Column(length = 50)
	private String lastName;

	@Column(length = 255)
	private String locality;

	@Column(length = 50)
	private String middleName;

	private Long natureOfEmployment;
	@Column(length = 50)
	private String nationalId;
	@Column(length = 100)
	private String nidFileName;

	private Long operatorTypeId;

	@Column(length = 20)
	private String operatorTypeName;

	@Column(length = 20)
	private String passportNo;

	@Column(length = 20, unique = true)
	private String phoneNo;

	@Column(length = 10)
	private String phoneOtp;

	@Column(length = 100)
	private String photoFileName;

	@Column(length = 10)
	private String postalCode;

	@Column(length = 255)
	private String propertyLocation;

	@Column(length = 255)
	private String province;

	@Column(length = 255)
	private String source;

	@Column(length = 50)
	private String sourceUserName;

	@Column(length = 255)
	private String street;

	private Long type;

	@Column(length = 100)
	private String vatFileName;

	@Column(length = 20)
	private String vatNo;

	private int vatStatus;

	@Column(length = 50)
	private String village;
	
	@Column(length = 256)
	private String address;
}
