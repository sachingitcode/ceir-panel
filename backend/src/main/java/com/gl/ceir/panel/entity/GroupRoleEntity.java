package com.gl.ceir.panel.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;

import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity(name="group_role")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class GroupRoleEntity  implements Serializable{
	private static final long serialVersionUID = 3566263158997619742L;
	@EmbeddedId
	private GroupRoleId id;
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns({ @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false, insertable=false, updatable=false) })
	private GroupEntity group;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns({ @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false, insertable=false, updatable=false) })
	private RoleEntity role;
	
	@Column(name = "created_by", updatable=true, insertable = true)
	private Long createdBy;
	private Long modifiedBy;
	@Column(length=2,columnDefinition = "varchar(2) default '0'")
	private String status;
	
	@Column(name = "created_on", updatable=true, insertable = true)
    private LocalDateTime createdOn;

	@UpdateTimestamp
    @Column(name = "modifiedOn")
    private LocalDateTime updatedOn;
}
