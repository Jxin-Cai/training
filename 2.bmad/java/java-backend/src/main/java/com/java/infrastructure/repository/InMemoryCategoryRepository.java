package com.java.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.java.domain.entity.Category;
import com.java.domain.repository.CategoryRepository;

/**
 * 分类仓储的内存实现
 * 
 * 使用ConcurrentHashMap存储分类数据。
 * 应用重启后数据会丢失。
 * 
 * @author Jxin
 */
@Repository
public class InMemoryCategoryRepository implements CategoryRepository {
    
    private final ConcurrentHashMap<Long, Category> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public Optional<Category> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }
    
    @Override
    public Optional<Category> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return store.values().stream()
                .filter(category -> name.equals(category.getName()))
                .findFirst();
    }
    
    @Override
    public List<Category> findAll() {
        return List.copyOf(store.values());
    }
    
    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            // 新分类，生成ID
            Long id = idGenerator.getAndIncrement();
            category.setId(id);
        }
        store.put(category.getId(), category);
        return category;
    }
    
    @Override
    public void deleteById(Long id) {
        if (id != null) {
            store.remove(id);
        }
    }
    
    @Override
    public boolean existsByName(String name) {
        return findByName(name).isPresent();
    }
    
    @Override
    public long count() {
        return store.size();
    }
}
