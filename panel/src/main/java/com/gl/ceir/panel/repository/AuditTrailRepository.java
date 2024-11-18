package com.gl.ceir.panel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.gl.ceir.panel.entity.AuditTrailEntity;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrailEntity, Long>, JpaSpecificationExecutor<AuditTrailEntity>{

}
