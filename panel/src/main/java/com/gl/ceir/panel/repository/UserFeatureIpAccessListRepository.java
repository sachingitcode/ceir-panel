package com.gl.ceir.panel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gl.ceir.panel.entity.UserFeatureIpAccessListEntity;

@Repository
public interface UserFeatureIpAccessListRepository extends CrudRepository<UserFeatureIpAccessListEntity, Long>,
		JpaSpecificationExecutor<UserFeatureIpAccessListEntity> {
	public List<UserFeatureIpAccessListEntity> findByUserId(Long userId);
}
