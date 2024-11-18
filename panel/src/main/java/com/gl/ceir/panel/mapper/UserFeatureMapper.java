package com.gl.ceir.panel.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.gl.ceir.panel.dto.response.UserFeaturePaginationDto;
import com.gl.ceir.panel.dto.response.UserFeatureViewDto;
import com.gl.ceir.panel.entity.UserEntity;
import com.gl.ceir.panel.entity.UserFeatureEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserFeatureMapper {
	UserFeaturePaginationDto userEntityToUserFeatureDto(UserEntity ue);
	
	List<UserFeatureViewDto> entityToDto(List<UserFeatureEntity> ge);
}
