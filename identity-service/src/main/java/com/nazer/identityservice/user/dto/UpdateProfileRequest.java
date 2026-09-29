package com.nazer.identityservice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
		@Size(min = 1, max = 100) String firstName,
		@Size(min = 1, max = 100) String lastName,
		@Email @Size(min = 1, max = 255) String email,
		@Size(max = 30) String phone) {
}
