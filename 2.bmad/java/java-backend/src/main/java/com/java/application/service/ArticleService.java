package com.java.application.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.java.domain.entity.Article;
import com.java.domain.entity.Category;
import com.java.domain.entity.User;
import com.java.domain.repository.ArticleRepository;
import com.java.domain.repository.CategoryRepository;
import com.java.domain.repository.UserRepository;
import com.java.domain.valueobject.ArticleStatus;
import com.java.presentation.dto.ArticleDTO;
import com.java.presentation.dto.ArticleRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 文章服务
 * 
 * @author Jxin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    
    public List<ArticleDTO> getAllArticles() {
        return articleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ArticleDTO> getPublishedArticles() {
        return articleRepository.findByStatus(ArticleStatus.PUBLISHED).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ArticleDTO> getArticlesByCategory(Long categoryId) {
        return articleRepository.findByStatusAndCategoryId(ArticleStatus.PUBLISHED, categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ArticleDTO> getArticlesByAuthor(Long authorId) {
        return articleRepository.findByAuthorId(authorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ArticleDTO> searchPublishedArticles(String keyword) {
        return articleRepository.searchPublishedByTitle(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<ArticleDTO> getArticleById(Long id) {
        return articleRepository.findById(id).map(this::convertToDTO);
    }
    
    public ArticleDTO createArticle(ArticleRequest request, Long authorId) {
        validateCategory(request.getCategoryId());
        
        Article article = new Article(
                request.getTitle(),
                request.getContent(),
                request.getCategoryId(),
                authorId
        );
        
        Article saved = articleRepository.save(article);
        log.info("文章创建成功: {}", saved.getId());
        return convertToDTO(saved);
    }
    
    public ArticleDTO updateArticle(Long id, ArticleRequest request, Long userId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        
        if (!article.isOwnedBy(userId)) {
            throw new IllegalArgumentException("无权编辑此文章");
        }
        
        validateCategory(request.getCategoryId());
        article.update(request.getTitle(), request.getContent(), request.getCategoryId());
        
        Article updated = articleRepository.save(article);
        log.info("文章更新成功: {}", id);
        return convertToDTO(updated);
    }
    
    public void deleteArticle(Long id, Long userId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        
        if (!article.isOwnedBy(userId)) {
            throw new IllegalArgumentException("无权删除此文章");
        }
        
        articleRepository.deleteById(id);
        log.info("文章删除成功: {}", id);
    }
    
    public void publishArticle(Long id, Long userId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        
        if (!article.isOwnedBy(userId)) {
            throw new IllegalArgumentException("无权发布此文章");
        }
        
        article.publish();
        articleRepository.save(article);
        log.info("文章发布成功: {}", id);
    }
    
    public void unpublishArticle(Long id, Long userId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        
        if (!article.isOwnedBy(userId)) {
            throw new IllegalArgumentException("无权操作此文章");
        }
        
        article.unpublish();
        articleRepository.save(article);
        log.info("文章取消发布: {}", id);
    }
    
    private void validateCategory(Long categoryId) {
        if (!categoryRepository.findById(categoryId).isPresent()) {
            throw new IllegalArgumentException("分类不存在");
        }
    }
    
    private ArticleDTO convertToDTO(Article article) {
        String categoryName = categoryRepository.findById(article.getCategoryId())
                .map(Category::getName)
                .orElse("");
        
        String authorName = userRepository.findById(article.getAuthorId())
                .map(User::getUsername)
                .orElse("");
        
        return ArticleDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .status(article.getStatus())
                .categoryId(article.getCategoryId())
                .categoryName(categoryName)
                .authorId(article.getAuthorId())
                .authorName(authorName)
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }
}
