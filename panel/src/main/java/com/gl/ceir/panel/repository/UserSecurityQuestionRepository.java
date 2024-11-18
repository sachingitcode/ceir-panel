package com.gl.ceir.panel.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.gl.ceir.panel.entity.UserSecurityQuestionEntity;
import com.gl.ceir.panel.entity.UserSecurityQuestionId;

@Repository
public interface UserSecurityQuestionRepository
		extends PagingAndSortingRepository<UserSecurityQuestionEntity, UserSecurityQuestionId>,
		JpaSpecificationExecutor<UserSecurityQuestionEntity> {

}
