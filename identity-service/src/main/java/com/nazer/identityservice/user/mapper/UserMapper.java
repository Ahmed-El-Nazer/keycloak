package com.nazer.identityservice.user.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.nazer.identityservice.user.dto.RegisterRequest;
import com.nazer.identityservice.user.dto.UpdateProfileRequest;
import com.nazer.identityservice.user.dto.UserResponse;
import com.nazer.identityservice.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "username", qualifiedByName = "trim")
	@Mapping(target = "email", qualifiedByName = "normalizeEmail")
	@Mapping(target = "firstName", qualifiedByName = "trim")
	@Mapping(target = "lastName", qualifiedByName = "trim")
	@Mapping(target = "phone", qualifiedByName = "blankToNull")
	User toEntity(RegisterRequest request);

	UserResponse toResponse(User user);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "username", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "email", qualifiedByName = "normalizeEmail")
	@Mapping(target = "firstName", qualifiedByName = "trim")
	@Mapping(target = "lastName", qualifiedByName = "trim")
	@Mapping(target = "phone", qualifiedByName = "blankToNull")
	void update(UpdateProfileRequest request, @MappingTarget User user);

	@Named("trim")
	default String trim(String value) {
		return value == null ? null : value.trim();
	}

	@Named("normalizeEmail")
	default String normalizeEmail(String email) {
		return email == null ? null : email.trim().toLowerCase();
	}

	@Named("blankToNull")
	default String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}

}
