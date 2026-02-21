package com.java.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.java.domain.entity.User;
import com.java.domain.repository.UserRepository;

/**
 * 用户仓储的内存实现
 * 
 * 使用ConcurrentHashMap存储用户数据。
 * 应用重启后数据会丢失。
 * 
 * @author Jxin
 */
@Repository
public class InMemoryUserRepository implements UserRepository {
    
    private final ConcurrentHashMap<Long, User> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return store.values().stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst();
    }
    
    @Override
    public List<User> findAll() {
        return List.copyOf(store.values());
    }
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            // 新用户，生成ID
            Long id = idGenerator.getAndIncrement();
            user.setId(id);
        }
        store.put(user.getId(), user);
        return user;
    }
    
    @Override
    public void deleteById(Long id) {
        if (id != null) {
            store.remove(id);
        }
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }
    
    @Override
    public long count() {
        return store.size();
    }
}
