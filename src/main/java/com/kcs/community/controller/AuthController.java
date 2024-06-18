package com.kcs.community.controller;

import com.kcs.community.auth.CustomUserDetails;
import com.kcs.community.dto.user.SignupRequest;
import com.kcs.community.dto.user.SignupResponse;
import com.kcs.community.dto.user.UserInfoDto;
import com.kcs.community.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        try {
            SignupResponse response = userService.signup(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("signup: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<UserInfoDto> test(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserInfoDto userDto = userService.findByEmail(userDetails.getUsername());

        log.info("findUser email: {}, nickname: {}, profileUrl: {}", userDto.email(), userDto.nickname(), userDto.profileUrl());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
