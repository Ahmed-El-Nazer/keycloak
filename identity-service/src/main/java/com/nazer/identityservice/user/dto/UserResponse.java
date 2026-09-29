package com.nazer.identityservice.user.dto;

public record UserResponse(
		Long id,
		String username,
		String email,
		String firstName,
		String lastName,
		String phone) {
}
