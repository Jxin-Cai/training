package com.java.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.application.service.AuthService;
import com.java.presentation.dto.ApiResponse;
import com.java.presentation.dto.LoginRequest;
import com.java.presentation.dto.LoginResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证控制器
 * 
 * 处理登录、登出等认证相关API。
 * 
 * @author Jxin
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 用户登录
     * 
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (IllegalArgumentException e) {
            log.warn("登录失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 用户登出
     * 
     * @return 成功响应
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout();
        return ResponseEntity.ok(ApiResponse.success("登出成功", null));
    }
    
    /**
     * 获取CSRF Token
     * 
     * @return CSRF Token
     */
    @GetMapping("/csrf-token")
    public ResponseEntity<ApiResponse<String>> getCsrfToken() {
        // Spring Security 自动处理CSRF，这里返回提示
        return ResponseEntity.ok(ApiResponse.success("CSRF token已设置在Cookie中", null));
    }
}
