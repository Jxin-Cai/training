package com.java.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.domain.entity.User;
import com.java.domain.repository.UserRepository;
import com.java.domain.valueobject.UserRole;
import com.java.presentation.dto.UserDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户服务
 * 
 * @author Jxin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }
    
    public UserDTO createUser(String username, String password, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        User user = new User(username, passwordEncoder.encode(password), role);
        User saved = userRepository.save(user);
        
        log.info("用户创建成功: {}", saved.getId());
        return convertToDTO(saved);
    }
    
    public UserDTO updateUserRole(Long id, UserRole role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        user.changeRole(role);
        User updated = userRepository.save(user);
        
        log.info("用户角色更新成功: {}", id);
        return convertToDTO(updated);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("用户删除成功: {}", id);
    }
    
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
