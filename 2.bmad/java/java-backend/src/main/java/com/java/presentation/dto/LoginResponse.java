package com.java.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 * 
 * @author Jxin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * 访问令牌（简化版，实际项目中使用Session）
     */
    private String token;
    
    /**
     * 用户信息
     */
    private UserDTO user;
}
