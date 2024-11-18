package com.gl.ceir.panel.service.criteria;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.gl.ceir.panel.dto.UserGroupPermissionDto;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Slf4j
public abstract class AbstractCriteria {
	protected AbstractCriteria forPermissions(Root<?> root, CriteriaBuilder criteriaBuilder, 
			List<Predicate> predicates, UserGroupPermissionDto ugpd) {
		predicates.add(criteriaBuilder.and(root.get("createdBy").in(ugpd.getUserIds())));
		return this;
	}
	protected AbstractCriteria forRelationPermissions(Root<?> root, CriteriaBuilder criteriaBuilder, 
			List<Predicate> predicates, UserGroupPermissionDto ugpd) {
		predicates.add(criteriaBuilder.and(root.get("group").get("modifiedBy").in(ugpd.getUserIds())));
		return this;
	}
}
