package com.cms.infrastructure.repository;

import com.cms.domain.model.Content;
import com.cms.domain.repository.ContentRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryContentRepository implements ContentRepository {
    
    private final Map<String, Content> store = new ConcurrentHashMap<>();
    
    @Override
    public Content save(Content content) {
        store.put(content.getId(), content);
        return content;
    }
    
    @Override
    public Optional<Content> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
    
    @Override
    public List<Content> findAll() {
        return new ArrayList<>(store.values());
    }
    
    @Override
    public List<Content> findByCategoryId(String categoryId) {
        return store.values().stream()
                .filter(c -> categoryId.equals(c.getCategoryId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Content> findByCategoryIds(List<String> categoryIds) {
        return store.values().stream()
                .filter(c -> categoryIds.contains(c.getCategoryId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Content> findAllPublished() {
        return store.values().stream()
                .filter(Content::isPublished)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Content> findAllPublishedOrderByPublishedAtDesc() {
        return store.values().stream()
                .filter(Content::isPublished)
                .sorted(Comparator.comparing(Content::getPublishedAt).reversed())
                .collect(Collectors.toList());
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
