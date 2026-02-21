package com.java.infrastructure.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.java.domain.entity.User;
import com.java.domain.repository.UserRepository;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Spring Security 配置
 * 
 * 配置安全策略：
 * - 前台阅读页面无需认证
 * - 后台管理需要认证
 * - Token-based 认证
 * - 密码 BCrypt 加密
 * 
 * @author Jxin
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // 简单的token存储（生产环境应使用Redis或JWT）
    private static final Map<String, Long> tokenStore = new ConcurrentHashMap<>();
    
    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Token存储（供AuthService使用）
     */
    public static void storeToken(String token, Long userId) {
        tokenStore.put(token, userId);
    }
    
    public static Optional<Long> getUserIdByToken(String token) {
        return Optional.ofNullable(tokenStore.get(token));
    }
    
    public static void removeToken(String token) {
        tokenStore.remove(token);
    }
    
    /**
     * 安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserRepository userRepository) throws Exception {
        http
            // CORS 配置
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 禁用CSRF（使用Token认证）
            .csrf(csrf -> csrf.disable())
            
            // Token认证过滤器
            .addFilterBefore(new TokenAuthenticationFilter(userRepository), UsernamePasswordAuthenticationFilter.class)
            
            // 授权配置
            .authorizeHttpRequests(auth -> auth
                // 前台阅读API无需认证
                .requestMatchers("/api/articles").permitAll()
                .requestMatchers("/api/articles/*").permitAll()
                .requestMatchers("/api/categories").permitAll()
                .requestMatchers("/api/categories/*").permitAll()
                .requestMatchers("/api/search/**").permitAll()
                
                // 认证API
                .requestMatchers("/api/auth/**").permitAll()
                
                // 静态资源
                .requestMatchers("/", "/index.html", "/assets/**", "/*.js", "/*.css").permitAll()
                
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
    
    /**
     * CORS 配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
    
    /**
     * Token认证过滤器
     */
    public static class TokenAuthenticationFilter implements Filter {
        
        private final UserRepository userRepository;
        
        public TokenAuthenticationFilter(UserRepository userRepository) {
            this.userRepository = userRepository;
        }
        
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String authHeader = httpRequest.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                getUserIdByToken(token).ifPresent(userId -> {
                    userRepository.findById(userId).ifPresent(user -> {
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(user, null, null);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    });
                });
            }
            
            chain.doFilter(request, response);
        }
    }
}
