package com.cms.infrastructure.repository;

import com.cms.domain.model.Category;
import com.cms.domain.repository.CategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCategoryRepository implements CategoryRepository {
    
    private final Map<String, Category> store = new ConcurrentHashMap<>();
    
    @Override
    public Category save(Category category) {
        store.put(category.getId(), category);
        return category;
    }
    
    @Override
    public Optional<Category> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
    
    @Override
    public List<Category> findAll() {
        return new ArrayList<>(store.values());
    }
    
    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
    
    @Override
    public boolean existsById(String id) {
        return store.containsKey(id);
    }
}
