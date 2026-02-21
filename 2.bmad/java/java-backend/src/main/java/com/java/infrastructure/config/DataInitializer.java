package com.java.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.java.domain.entity.User;
import com.java.domain.repository.UserRepository;
import com.java.domain.valueobject.UserRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据初始化器
 * 
 * 在应用启动时初始化默认数据。
 * 
 * @author Jxin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        log.info("初始化默认数据...");
        
        // 创建默认管理员
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    UserRole.ADMIN
            );
            userRepository.save(admin);
            log.info("默认管理员已创建: admin / admin123");
        }
        
        // 创建默认作者
        if (!userRepository.existsByUsername("author")) {
            User author = new User(
                    "author",
                    passwordEncoder.encode("author123"),
                    UserRole.AUTHOR
            );
            userRepository.save(author);
            log.info("默认作者已创建: author / author123");
        }
        
        // 创建默认读者
        if (!userRepository.existsByUsername("reader")) {
            User reader = new User(
                    "reader",
                    passwordEncoder.encode("reader123"),
                    UserRole.READER
            );
            userRepository.save(reader);
            log.info("默认读者已创建: reader / reader123");
        }
        
        log.info("数据初始化完成");
    }
}
