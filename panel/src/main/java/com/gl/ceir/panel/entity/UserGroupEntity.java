package com.gl.ceir.panel.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
@Table(name = "user_group_membership")
@JsonInclude(Include.NON_NULL)
public class UserGroupEntity implements Serializable {
	private static final long serialVersionUID = 893530433194421863L;
	@EmbeddedId
	private UserGroupId id;
	@OneToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumns({
			@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true, insertable = false, updatable = false) })
	private UserEntity user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false) })
	private GroupEntity group;

	@Column(name = "created_by", updatable = false)
	private Long createdBy;
	private Long modifiedBy;
	@Column(length = 2, columnDefinition = "varchar(2) default '0'")
	private String status;
	
	@Column(name = "created_on", updatable=true, insertable = true)
    private LocalDateTime createdOn;

	@UpdateTimestamp
    @Column(name = "modifiedOn")
    private LocalDateTime updatedOn;
}
