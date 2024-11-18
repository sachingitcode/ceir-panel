package com.gl.ceir.panel.service.criteria;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.gl.ceir.panel.dto.FilterDto;
import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.dto.UserGroupPermissionDto;
import com.gl.ceir.panel.entity.GroupEntity;
import com.gl.ceir.panel.repository.GroupRepository;
import com.gl.ceir.panel.service.UserPermissionService;
import com.gl.ceir.panel.util.DateUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor()
@Slf4j
@SuppressWarnings("unused")
public class GroupCriteriaService extends AbstractCriteria {
	private final GroupRepository groupRepository;
	private final DateUtil dateUtil;
	private final UserPermissionService userPermissionService;
	public Page<?> pagination(PaginationRequestDto ulrd) {
		UserGroupPermissionDto ugpd = userPermissionService.permissions();
		return groupRepository.findAll(new Specification<GroupEntity>() {
            private static final long serialVersionUID = 6411748625986892210L;
			@Override
            public Predicate toPredicate(Root<GroupEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                forPermissions(root, criteriaBuilder, predicates, ugpd);
                if(CollectionUtils.isNotEmpty(ulrd.getFilters())) {
                	for(FilterDto filter: ulrd.getFilters()) {
                		forGroup(root, criteriaBuilder, predicates, filter).
                		forParentGroupName(root,criteriaBuilder, predicates, filter).
                		forGroupName(root,criteriaBuilder, predicates, filter)
								.forStartDate(root, criteriaBuilder, predicates, filter)
								.forEndDate(root, criteriaBuilder, predicates, filter);
                	}
                }
                sortBy(ulrd, root, query, criteriaBuilder, predicates);
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, PageRequest.of(ulrd.getPage().getCurrent() - 1, ulrd.getPage().getSize()));
	}
	
	private GroupCriteriaService sortBy(PaginationRequestDto ulrd, Root<GroupEntity> root, CriteriaQuery<?> query,
			CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
		Path<Object> path = null;
		if(ObjectUtils.isEmpty(ulrd.getSort())) {
			path = root.get("updatedOn");
		} else {
			if(ulrd.getSort().getBy().equals("groupName") || ulrd.getSort().getBy().equals("groupName")) {
				path = root.get("profile").get(ulrd.getSort().getBy());
			} else if(ulrd.getSort().getBy().equals("createdOn")) {
				path = root.get(ulrd.getSort().getBy());	
			}
		}
		if(ObjectUtils.isEmpty(path)) return this;
		if(ObjectUtils.isNotEmpty(ulrd.getSort()) && ulrd.getSort().isReverse()) query.orderBy(criteriaBuilder.asc(path));
		else query.orderBy(criteriaBuilder.desc(path));
		
		return this;
	}
	private GroupCriteriaService forParentGroupName(Root<GroupEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, FilterDto filter) {
		if(filter.getProperty().equals("parentGroupName") && ObjectUtils.isNotEmpty(filter.getValue())) {
			log.info("parent group name: {}", filter.getValue());
			predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("parent").get("groupName"), "%"+filter.getValue()+"%")));
		}
		return this;
	}
	private GroupCriteriaService forGroupName(Root<GroupEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, FilterDto filter) {
		if(filter.getProperty().equals("groupName") && ObjectUtils.isNotEmpty(filter.getValue())) {
			log.info("group name: {}", filter.getValue());
			predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("groupName"), "%"+filter.getValue()+"%")));
		}
		return this;
	}
	private GroupCriteriaService forStartDate(Root<GroupEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, FilterDto filter) {
		if(filter.getProperty().equals("startDate") && ObjectUtils.isNotEmpty(filter.getValue())) {
			LocalDateTime startDate = dateUtil.toLocalDateTime(filter.getValue(), DateUtil._fronendFormat);
			log.info("start date: {}, formatted date:{}", filter.getValue(), startDate);
			predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("createdOn"), startDate)));
		}
		return this;
	}
	
	private GroupCriteriaService forEndDate(Root<GroupEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, FilterDto filter) {
		if(filter.getProperty().equals("endDate") && ObjectUtils.isNotEmpty(filter.getValue())) {
			LocalDateTime endDate = dateUtil.toLocalDateTime(filter.getValue(), DateUtil._fronendFormat);
			log.info("end date: {}, formatted date:{}", filter.getValue(), endDate);
			predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("createdOn"), endDate)));
		}
		return this;
	}
	private GroupCriteriaService forGroup(Root<GroupEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, FilterDto filter) {
		if(filter.getProperty().equals("group") && ObjectUtils.isNotEmpty(filter.getValue())) {
			log.info("group id: {}", filter.getValue());
			predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), filter.getValue())));
		}
		return this;
	}
}
