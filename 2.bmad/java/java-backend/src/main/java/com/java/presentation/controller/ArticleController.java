package com.java.presentation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.application.service.ArticleService;
import com.java.domain.repository.UserRepository;
import com.java.presentation.dto.ApiResponse;
import com.java.presentation.dto.ArticleDTO;
import com.java.presentation.dto.ArticleRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 文章控制器
 * 
 * @author Jxin
 */
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Validated
public class ArticleController {
    
    private final ArticleService articleService;
    private final UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ArticleDTO>>> getAllArticles(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") boolean all) {
        
        List<ArticleDTO> articles;
        
        // all=true 时返回所有文章（用于后台管理）
        if (all) {
            articles = articleService.getAllArticles();
        } else if (search != null && !search.trim().isEmpty()) {
            articles = articleService.searchPublishedArticles(search);
        } else if (categoryId != null) {
            articles = articleService.getArticlesByCategory(categoryId);
        } else {
            articles = articleService.getPublishedArticles();
        }
        
        return ResponseEntity.ok(ApiResponse.success(articles));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDTO>> getArticleById(@PathVariable Long id) {
        return articleService.getArticleById(id)
                .map(article -> ResponseEntity.ok(ApiResponse.success(article)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ArticleDTO>> createArticle(
            @Valid @RequestBody ArticleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        ArticleDTO article = articleService.createArticle(request, userId);
        return ResponseEntity.ok(ApiResponse.success("文章创建成功", article));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDTO>> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        ArticleDTO article = articleService.updateArticle(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("文章更新成功", article));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        articleService.deleteArticle(id, userId);
        return ResponseEntity.ok(ApiResponse.success("文章删除成功", null));
    }
    
    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<Void>> publishArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        articleService.publishArticle(id, userId);
        return ResponseEntity.ok(ApiResponse.success("文章发布成功", null));
    }
    
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<ApiResponse<Void>> unpublishArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        articleService.unpublishArticle(id, userId);
        return ResponseEntity.ok(ApiResponse.success("文章已转为草稿", null));
    }
    
    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"))
                .getId();
    }
}
