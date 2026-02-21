package com.java.application.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.domain.entity.User;
import com.java.domain.repository.UserRepository;
import com.java.infrastructure.config.SecurityConfig;
import com.java.presentation.dto.LoginRequest;
import com.java.presentation.dto.LoginResponse;
import com.java.presentation.dto.UserDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证服务
 * 
 * 处理用户登录、登出等认证相关业务逻辑。
 * 
 * @author Jxin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    
    /**
     * 用户登录
     * 
     * @param request 登录请求
     * @return 登录响应（包含token和用户信息）
     * @throws IllegalArgumentException 如果用户名或密码错误
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录尝试: {}", request.getUsername());
        
        // 查找用户
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        
        if (userOpt.isEmpty()) {
            log.warn("登录失败: 用户不存在 - {}", request.getUsername());
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        User user = userOpt.get();
        
        // 验证密码
        if (!passwordService.matches(request.getPassword(), user.getPassword())) {
            log.warn("登录失败: 密码错误 - {}", request.getUsername());
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        // 生成简单token（实际项目应使用JWT或Session）
        String token = UUID.randomUUID().toString();
        
        // 存储token
        SecurityConfig.storeToken(token, user.getId());
        
        // 构建响应
        UserDTO userDTO = convertToDTO(user);
        
        log.info("用户登录成功: {} - {}", user.getId(), user.getUsername());
        
        return LoginResponse.builder()
                .token(token)
                .user(userDTO)
                .build();
    }
    
    /**
     * 用户登出
     */
    public void logout() {
        log.info("用户登出");
        // 简化实现，Session管理由Spring Security处理
    }
    
    /**
     * 获取当前用户信息
     * 
     * @param userId 用户ID
     * @return 用户DTO
     */
    public UserDTO getCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }
    
    /**
     * 转换为DTO
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
