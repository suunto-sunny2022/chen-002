package com.chen.system.controller;

import com.chen.common.ApiResponse;
import lombok.Data;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestBody LoginRequest request) {
        SecurityUtils.getSubject().login(
            new UsernamePasswordToken(request.getUsername(), request.getPassword()));
        return ApiResponse.ok();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        SecurityUtils.getSubject().logout();
        return ApiResponse.ok();
    }

    @GetMapping("/unauthorized")
    public ResponseEntity<ApiResponse<Void>> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("请先登录"));
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
