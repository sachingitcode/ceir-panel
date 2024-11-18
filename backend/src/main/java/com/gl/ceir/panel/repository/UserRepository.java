package com.gl.ceir.panel.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gl.ceir.panel.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
	public List<UserEntity> findAll();

	public Page<?> findAll(Pageable pageable);

	public Page<?> findAllByProfileFirstNameContaining(String firstName, Pageable pageable);

	public Optional<UserEntity> findByUserName(String userName);

	Boolean existsByUserName(String userName);

	Boolean existsByProfileEmail(String email);

	public List<UserEntity> findByIdIn(List<Long> ids);

	List<UserEntity> findByCreatedByInAndCurrentStatus(Set<Long> ids, String status);

	@Query("select u.userName from UserEntity u where id in(:ids)")
	List<Object> findByIdIn(@Param("ids") Set<Long> ids);
	
	public List<UserEntity> findByCreatedByIn(Set<Long> userIds);
}