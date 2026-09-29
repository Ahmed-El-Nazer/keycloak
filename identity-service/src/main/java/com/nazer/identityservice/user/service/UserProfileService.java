package com.nazer.identityservice.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nazer.identityservice.common.exception.ApiException;
import com.nazer.identityservice.user.dto.RegisterRequest;
import com.nazer.identityservice.user.dto.UpdateProfileRequest;
import com.nazer.identityservice.user.dto.UserResponse;
import com.nazer.identityservice.user.entity.User;
import com.nazer.identityservice.user.mapper.UserMapper;
import com.nazer.identityservice.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;

	@Transactional
	public UserResponse register(RegisterRequest request) {
		String username = request.username().trim();
		String email = request.email().trim().toLowerCase();

		if (userRepository.existsByUsername(username)) {
			throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
		}
		if (userRepository.existsByEmail(email)) {
			throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
		}

		User user = userMapper.toEntity(request);
		user.setPassword(passwordEncoder.encode(request.password()));
		return userMapper.toResponse(userRepository.save(user));
	}

	@Transactional
	public UserResponse updateProfile(Long id, UpdateProfileRequest request) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

		if (request.email() != null) {
			String email = request.email().trim().toLowerCase();
			if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
				throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
			}
		}

		userMapper.update(request, user);
		return userMapper.toResponse(user);
	}

}
