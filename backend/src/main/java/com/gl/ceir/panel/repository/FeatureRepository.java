package com.gl.ceir.panel.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.gl.ceir.panel.entity.FeatureEntity;

@Repository
public interface FeatureRepository extends PagingAndSortingRepository<FeatureEntity, Long>,
		JpaSpecificationExecutor<FeatureEntity>, CrudRepository<FeatureEntity, Long> {
	public List<FeatureEntity> findAll();

	public List<FeatureEntity> findByCreatedByIn(Set<Long> userIds);

	public List<FeatureEntity> findByIdIn(List<Long> ids);

	public List<FeatureEntity> findByCreatedByInAndStatus(Set<Long> userIds, String status);
}
