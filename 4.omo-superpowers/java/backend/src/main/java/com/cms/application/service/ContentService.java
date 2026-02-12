package com.cms.application.service;

import com.cms.application.dto.ContentDto;
import com.cms.application.dto.CreateContentRequest;
import com.cms.application.dto.UpdateContentRequest;
import com.cms.domain.model.Content;
import com.cms.domain.repository.CategoryRepository;
import com.cms.domain.repository.ContentRepository;
import com.cms.domain.service.MarkdownRenderer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContentService {
    
    private final ContentRepository contentRepository;
    private final CategoryRepository categoryRepository;
    private final MarkdownRenderer markdownRenderer;
    private final CategoryService categoryService;
    
    public ContentService(ContentRepository contentRepository, 
                          CategoryRepository categoryRepository,
                          MarkdownRenderer markdownRenderer,
                          CategoryService categoryService) {
        this.contentRepository = contentRepository;
        this.categoryRepository = categoryRepository;
        this.markdownRenderer = markdownRenderer;
        this.categoryService = categoryService;
    }
    
    public ContentDto create(CreateContentRequest request) {
        if (!categoryRepository.existsById(request.categoryId())) {
            throw new IllegalArgumentException("Category not found: " + request.categoryId());
        }
        
        Content content = Content.create(request.title(), request.markdownContent(), request.categoryId());
        String html = markdownRenderer.renderToHtml(request.markdownContent());
        content.setRenderedHtml(html);
        
        Content saved = contentRepository.save(content);
        return toDto(saved);
    }
    
    public Optional<ContentDto> findById(String id) {
        return contentRepository.findById(id).map(this::toDto);
    }
    
    public List<ContentDto> findAll() {
        return contentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<ContentDto> findAllPublished() {
        return contentRepository.findAllPublishedOrderByPublishedAtDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<ContentDto> findByCategoryId(String categoryId) {
        return contentRepository.findByCategoryId(categoryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<ContentDto> findByCategoryWithDescendants(String categoryId) {
        List<String> categoryIds = categoryService.getDescendantIds(categoryId);
        return contentRepository.findByCategoryIds(categoryIds).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public Optional<ContentDto> update(String id, UpdateContentRequest request) {
        return contentRepository.findById(id).map(content -> {
            if (request.categoryId() != null && !categoryRepository.existsById(request.categoryId())) {
                throw new IllegalArgumentException("Category not found: " + request.categoryId());
            }
            
            if (request.title() != null && request.markdownContent() != null) {
                content.updateContent(request.title(), request.markdownContent());
                String html = markdownRenderer.renderToHtml(request.markdownContent());
                content.setRenderedHtml(html);
            }
            
            if (request.categoryId() != null) {
                content.changeCategory(request.categoryId());
            }
            
            Content updated = contentRepository.save(content);
            return toDto(updated);
        });
    }
    
    public Optional<ContentDto> publish(String id) {
        return contentRepository.findById(id).map(content -> {
            content.publish();
            Content published = contentRepository.save(content);
            return toDto(published);
        });
    }
    
    public Optional<ContentDto> unpublish(String id) {
        return contentRepository.findById(id).map(content -> {
            content.unpublish();
            Content unpublished = contentRepository.save(content);
            return toDto(unpublished);
        });
    }
    
    public boolean delete(String id) {
        if (contentRepository.existsById(id)) {
            contentRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private ContentDto toDto(Content content) {
        String categoryName = categoryRepository.findById(content.getCategoryId())
                .map(c -> c.getName())
                .orElse(null);
        
        return new ContentDto(
                content.getId(),
                content.getTitle(),
                content.getMarkdownContent(),
                content.getHtmlContent(),
                content.getCategoryId(),
                categoryName,
                content.getStatus().name(),
                content.getCreatedAt(),
                content.getUpdatedAt(),
                content.getPublishedAt()
        );
    }
}
