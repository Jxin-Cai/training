package com.cms.infrastructure.repository;

import com.cms.domain.model.Category;
import com.cms.domain.repository.CategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    
    @Override
    public List<Category> findByParentId(String parentId) {
        return store.values().stream()
                .filter(c -> {
                    if (parentId == null) {
                        return c.getParentId() == null;
                    }
                    return parentId.equals(c.getParentId());
                })
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Category> findRootCategories() {
        return findByParentId(null);
    }
    
    @Override
    public List<Category> findDescendants(String categoryId) {
        return findById(categoryId)
                .map(parent -> store.values().stream()
                        .filter(c -> c.getPath().startsWith(parent.getPath() + "/"))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
    
    @Override
    public boolean hasChildren(String categoryId) {
        return store.values().stream()
                .anyMatch(c -> categoryId.equals(c.getParentId()));
    }
}
