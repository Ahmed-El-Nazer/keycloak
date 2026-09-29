package com.nazer.identityservice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Size(min = 3, max = 50) String username,
		@NotBlank @Email @Size(max = 255) String email,
		@NotBlank @Size(min = 8, max = 100) String password,
		@NotBlank @Size(max = 100) String firstName,
		@NotBlank @Size(max = 100) String lastName,
		@Size(max = 30) String phone) {
}
