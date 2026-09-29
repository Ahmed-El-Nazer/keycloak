package com.nazer.identityservice.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nazer.identityservice.business.dto.BusinessResponse;
import com.nazer.identityservice.common.response.SuccessResponse;

@RestController
@RequestMapping("/api/business")
public class BusinessController {

	@GetMapping("/public")
	public SuccessResponse<BusinessResponse> publicEndpoint() {
		return SuccessResponse.of(
				"Public business request completed",
				new BusinessResponse("Public business endpoint"));
	}

	@GetMapping("/secure")
	public SuccessResponse<BusinessResponse> secureEndpoint() {
		return SuccessResponse.of(
				"Secure business request completed",
				new BusinessResponse("Authenticated business endpoint"));
	}

}
