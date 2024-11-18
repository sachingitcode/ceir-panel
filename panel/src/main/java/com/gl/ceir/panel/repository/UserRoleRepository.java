package com.gl.ceir.panel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.entity.UserRoleEntity;
import com.gl.ceir.panel.entity.UserRoleId;

@Repository
public interface UserRoleRepository extends PagingAndSortingRepository<UserRoleEntity, UserRoleId>,
		JpaSpecificationExecutor<UserRoleEntity>, CrudRepository<UserRoleEntity, UserRoleId> {
	public UserRoleEntity findByUser(UserEntity user);

	public List<UserRoleEntity> findByIdUserId(Long userId);

	public List<UserRoleEntity> findByIdIn(List<UserRoleId> ids);

	public List<UserRoleEntity> findByIdUserIdAndStatus(Long userId, String status);
}
