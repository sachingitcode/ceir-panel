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

import com.gl.ceir.panel.constant.ColumnEnum;
import com.gl.ceir.panel.constant.ColumnPathEnum;
import com.gl.ceir.panel.dto.FilterDto;
import com.gl.ceir.panel.dto.PaginationRequestDto;
import com.gl.ceir.panel.dto.UserGroupPermissionDto;
import com.gl.ceir.panel.entity.UserGroupEntity;
import com.gl.ceir.panel.repository.UserGroupRepository;
import com.gl.ceir.panel.service.UserPermissionService;
import com.gl.ceir.panel.util.DateUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor()
@Slf4j
public class UserGroupCriteriaService extends AbstractCriteria {
	private final UserGroupRepository userGroupRepository;
	private final DateUtil dateUtil;
	private final UserPermissionService userPermissionService;
	public Page<?> pagination(PaginationRequestDto ulrd) {
		UserGroupPermissionDto ugpd = userPermissionService.permissions();
		return userGroupRepository.findAll(new Specification<UserGroupEntity>() {
            private static final long serialVersionUID = 6411748625986892210L;
			@Override
            public Predicate toPredicate(Root<UserGroupEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                forPermissions(root, criteriaBuilder, predicates, ugpd);
                if(CollectionUtils.isNotEmpty(ulrd.getFilters())) {
                	for(FilterDto filter: ulrd.getFilters()) {
                		forUserName(root,criteriaBuilder, predicates, filter)
                		.forGroupName(root,criteriaBuilder, predicates, filter)
                		.forGroupId(root,criteriaBuilder, predicates, filter)
								.forStartDate(root, criteriaBuilder, predicates, filter)
								.forEndDate(root, criteriaBuilder, predicates, filter);
                	}
                }
                sortBy(ulrd, root, query, criteriaBuilder, predicates);
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, PageRequest.of(ulrd.getPage().getCurrent() - 1, ulrd.getPage().getSize()));
	}
	private UserGroupCriteriaService sortBy(PaginationRequestDto ulrd, Root<UserGroupEntity> root, CriteriaQuery<?> query,
			CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
		Path<Object> path = null;
		if(ObjectUtils.isEmpty(ulrd.getSort())) {
			path = root.get(ColumnEnum.UPDATED_ON.column);
		} else {
			if(ulrd.getSort().getBy().equals(ColumnEnum.UPDATED_ON.column)) {
				path = root.get(ulrd.getSort().getBy());
			} else if(ulrd.getSort().getBy().equals(ColumnEnum.CREATED_ON.column)) {
				path = root.get(ulrd.getSort().getBy());	
			}else if(ulrd.getSort().getBy().equals(ColumnEnum.STATUS.column)) {
				path = root.get(ulrd.getSort().getBy());	
			}else if(ulrd.getSort().getBy().equals(ColumnEnum.USER.column)) {
				path = root.get(ColumnPathEnum.USER.path).get(ulrd.getSort().getBy());	
			}else if(ulrd.getSort().getBy().equals(ColumnEnum.GROUP.column)) {
				path = root.get(ColumnPathEnum.GROUP.path).get(ulrd.getSort().getBy());	
			}
		}
		if(ObjectUtils.isEmpty(path)) return this;
		if(ObjectUtils.isNotEmpty(ulrd.getSort()) && ulrd.getSort().isReverse()) query.orderBy(criteriaBuilder.asc(path));
		else query.orderBy(criteriaBuilder.desc(path));
		return this;
	}
	private UserGroupCriteriaService forUserName(Root<UserGroupEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, FilterDto filter) {
		if(filter.getProperty().equals(ColumnEnum.USER.column) && ObjectUtils.isNotEmpty(filter.getValue())) {
			log.info("path:{},column:{},value:{}", ColumnPathEnum.USER.path, ColumnEnum.USER.column, filter.getValue());
			predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get(ColumnPathEnum.USER.path).get(ColumnEnum.USER.column), "%"+filter.getValue()+"%")));
		}
		return this;
	}
	private UserGroupCriteriaService forGroupName(Root<UserGroupEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, FilterDto filter) {
		if(filter.getProperty().equals(ColumnEnum.GROUP.column) && ObjectUtils.isNotEmpty(filter.getValue())) {
			predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get(ColumnPathEnum.GROUP.path).get(ColumnEnum.GROUP.column), "%"+filter.getValue()+"%")));
		}
		return this;
	}
	private UserGroupCriteriaService forGroupId(Root<UserGroupEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, FilterDto filter) {
		if(filter.getProperty().equals(ColumnEnum.GROUP_ID.column) && ObjectUtils.isNotEmpty(filter.getValue())) {
			log.info("key: {}, value:{}", filter.getProperty(), filter.getValue());
			predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get(ColumnPathEnum.GROUP.path).get(ColumnEnum.ID.column), filter.getValue())));
		}
		return this;
	}
	private UserGroupCriteriaService forStartDate(Root<UserGroupEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, FilterDto filter) {
		if(filter.getProperty().equals(ColumnEnum.STARTDATE.column) && ObjectUtils.isNotEmpty(filter.getValue())) {
			LocalDateTime startDate = dateUtil.toLocalDateTime(filter.getValue(), DateUtil._fronendFormat);
			predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get(ColumnEnum.CREATED_ON.column), startDate)));
		}
		return this;
	}
	
	private UserGroupCriteriaService forEndDate(Root<UserGroupEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, FilterDto filter) {
		if(filter.getProperty().equals(ColumnEnum.ENDDATE.column) && ObjectUtils.isNotEmpty(filter.getValue())) {
			LocalDateTime endDate = dateUtil.toLocalDateTime(filter.getValue(), DateUtil._fronendFormat);
			predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get(ColumnEnum.UPDATED_ON.column), endDate)));
		}
		return this;
	}
}
