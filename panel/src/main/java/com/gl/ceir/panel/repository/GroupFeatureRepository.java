package com.gl.ceir.panel.repository;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.gl.ceir.panel.entity.GroupEntity;
import com.gl.ceir.panel.entity.GroupFeatureEntity;
import com.gl.ceir.panel.entity.GroupFeatureId;

@Repository
public interface GroupFeatureRepository extends PagingAndSortingRepository<GroupFeatureEntity, GroupFeatureId>,
		JpaSpecificationExecutor<GroupFeatureEntity>, CrudRepository<GroupFeatureEntity, GroupFeatureId> {
	public GroupFeatureEntity findByGroup(GroupEntity group);

	public Set<GroupFeatureEntity> findByGroupIn(Set<GroupEntity> groups);

	public List<GroupFeatureEntity> findByIdGroupId(Long groupId);

	public List<GroupFeatureEntity> findByIdGroupIdAndStatus(Long groupId, String status);

	public List<GroupFeatureEntity> findByIdIn(List<GroupFeatureId> ids);

	public List<GroupFeatureEntity> findByIdGroupIdInAndStatusOrderByDisplayOrder(List<Long> groupId, String status);

	@Modifying
	@Transactional
	public int deleteByIdGroupId(Long groupId);
}
