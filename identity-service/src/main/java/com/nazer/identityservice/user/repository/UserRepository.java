package com.nazer.identityservice.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nazer.identityservice.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

}
