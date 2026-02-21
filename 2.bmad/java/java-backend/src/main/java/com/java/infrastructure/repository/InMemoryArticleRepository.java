package com.java.infrastructure.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.java.domain.entity.Article;
import com.java.domain.repository.ArticleRepository;
import com.java.domain.valueobject.ArticleStatus;

/**
 * 文章仓储的内存实现
 * 
 * 使用ConcurrentHashMap存储文章数据。
 * 应用重启后数据会丢失。
 * 
 * @author Jxin
 */
@Repository
public class InMemoryArticleRepository implements ArticleRepository {
    
    private final ConcurrentHashMap<Long, Article> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public Optional<Article> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }
    
    @Override
    public List<Article> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(Article::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Article> findByStatus(ArticleStatus status) {
        return store.values().stream()
                .filter(article -> article.getStatus() == status)
                .sorted(Comparator.comparing(Article::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Article> findByCategoryId(Long categoryId) {
        return store.values().stream()
                .filter(article -> categoryId.equals(article.getCategoryId()))
                .sorted(Comparator.comparing(Article::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Article> findByAuthorId(Long authorId) {
        return store.values().stream()
                .filter(article -> authorId.equals(article.getAuthorId()))
                .sorted(Comparator.comparing(Article::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Article> findByStatusAndCategoryId(ArticleStatus status, Long categoryId) {
        return store.values().stream()
                .filter(article -> article.getStatus() == status && categoryId.equals(article.getCategoryId()))
                .sorted(Comparator.comparing(Article::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Article> searchByTitle(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        String lowerKeyword = keyword.toLowerCase();
        return store.values().stream()
                .filter(article -> article.getTitle().toLowerCase().contains(lowerKeyword))
                .sorted(Comparator.comparing(Article::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Article> searchPublishedByTitle(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        String lowerKeyword = keyword.toLowerCase();
        return store.values().stream()
                .filter(article -> article.getStatus() == ArticleStatus.PUBLISHED)
                .filter(article -> article.getTitle().toLowerCase().contains(lowerKeyword))
                .sorted(Comparator.comparing(Article::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public Article save(Article article) {
        if (article.getId() == null) {
            // 新文章，生成ID
            Long id = idGenerator.getAndIncrement();
            article.setId(id);
        }
        store.put(article.getId(), article);
        return article;
    }
    
    @Override
    public void deleteById(Long id) {
        if (id != null) {
            store.remove(id);
        }
    }
    
    @Override
    public long count() {
        return store.size();
    }
    
    @Override
    public long countByStatus(ArticleStatus status) {
        return store.values().stream()
                .filter(article -> article.getStatus() == status)
                .count();
    }
    
    @Override
    public long countByCategoryId(Long categoryId) {
        return store.values().stream()
                .filter(article -> categoryId.equals(article.getCategoryId()))
                .count();
    }
}
