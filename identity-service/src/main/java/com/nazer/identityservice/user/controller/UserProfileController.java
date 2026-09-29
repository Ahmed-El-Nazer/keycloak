package com.nazer.identityservice.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nazer.identityservice.common.response.SuccessResponse;
import com.nazer.identityservice.user.dto.RegisterRequest;
import com.nazer.identityservice.user.dto.UpdateProfileRequest;
import com.nazer.identityservice.user.dto.UserResponse;
import com.nazer.identityservice.user.service.UserProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

	private final UserProfileService userProfileService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public SuccessResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
		return SuccessResponse.of("User registered successfully", userProfileService.register(request));
	}

	@PutMapping("/{id}/profile")
	public SuccessResponse<UserResponse> updateProfile(
			@PathVariable Long id,
			@Valid @RequestBody UpdateProfileRequest request) {
		return SuccessResponse.of("Profile updated successfully", userProfileService.updateProfile(id, request));
	}

}
