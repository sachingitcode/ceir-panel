package com.gl.ceir.panel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.gl.ceir.panel.entity.FeatureEntity;
import com.gl.ceir.panel.entity.FeatureModuleEntity;
import com.gl.ceir.panel.entity.FeatureModuleId;

@Repository
public interface FeatureModuleRepository extends PagingAndSortingRepository<FeatureModuleEntity, FeatureModuleId>,
		JpaSpecificationExecutor<FeatureModuleEntity>, CrudRepository<FeatureModuleEntity, FeatureModuleId> {
	public FeatureModuleEntity findByFeature(FeatureEntity feature);

	public List<FeatureModuleEntity> findAll();

	public List<FeatureModuleEntity> findByIdIn(List<FeatureModuleId> ids);

	public List<FeatureModuleEntity> findByIdInAndStatus(List<FeatureModuleId> ids, String status);

	public List<FeatureModuleEntity> findByIdFeatureIdIn(List<Long> featureIds);

	public List<FeatureModuleEntity> findByIdFeatureIdAndStatus(Long featureId, String status);

	public List<FeatureModuleEntity> findByIdFeatureIdInAndStatus(List<Long> featureIds, String status);

	public List<FeatureModuleEntity> findByIdFeatureId(Long userId);
}
