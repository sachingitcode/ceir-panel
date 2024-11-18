package com.gl.ceir.panel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gl.ceir.panel.entity.UserProfileEntity;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfileEntity, Long> {
	public UserProfileEntity findByEmail(String email);
	public Page <?> findAll(Pageable pageable);
	public List<UserProfileEntity> findAllByFirstName(String firstName, Pageable pageable);
	public List<UserProfileEntity> findAllByLastName(String lastName, Pageable pageable);
	public List<UserProfileEntity> findAllByCompanyName(String companyName, Pageable pageable);
	public List<UserProfileEntity> findAllByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);
	public List<UserProfileEntity> findAllByFirstNameAndLastNameAndCompanyName(String firstName, String lastName, String companyName, Pageable pageable);
	public List<UserProfileEntity> findAllByFirstNameAndCompanyName(String firstName, String companyName, Pageable pageable);
	public List<UserProfileEntity> findAllByLastNameAndCompanyName(String lastName, String companyName, Pageable pageable);
}