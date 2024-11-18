package com.gl.ceir.panel.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.gl.ceir.panel.dto.response.UserGroupPaginationDto;
import com.gl.ceir.panel.dto.response.UserGroupViewDto;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.entity.UserGroupEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserGroupMapper {
	UserGroupPaginationDto userEntityToUserGroupDto(UserEntity ue);
	
	List<UserGroupViewDto> entityToDto(List<UserGroupEntity> ge);
}
