package com.cms.application;

import com.cms.domain.category.Category;
import com.cms.domain.category.CategoryRepository;
import com.cms.domain.category.CategoryTreeService;
import com.cms.domain.content.Content;
import com.cms.domain.content.ContentRepository;
import com.cms.domain.content.ContentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryTreeService categoryTreeService;

    public List<Content> getAllContent() {
        return contentRepository.findAll();
    }

    public Content getContentById(Long id) {
        return contentRepository.findById(id).orElse(null);
    }

    public List<Content> getContentByCategory(Long categoryId, boolean includeDescendants, ContentStatus status) {
        if (!includeDescendants) {
            if (status != null) {
                return contentRepository.findByCategoryIdAndStatus(categoryId, status);
            }
            return contentRepository.findByCategoryId(categoryId);
        }

        List<Category> categories = categoryTreeService.getAllDescendants(categoryId);
        List<Long> categoryIds = categories.stream()
            .map(Category::getId)
            .collect(Collectors.toList());

        if (status != null) {
            return contentRepository.findByCategoryIdIn(categoryIds).stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
        }

        return contentRepository.findByCategoryIdIn(categoryIds);
    }

    public Content createContent(String title, String body, Long categoryId, ContentStatus status) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Content content = new Content(title, body, category);
        if (status != null) {
            content.setStatus(status);
            if (status == ContentStatus.PUBLISHED) {
                content.publish();
            }
        }

        return contentRepository.save(content);
    }

    public Content updateContent(Long id, String title, String body, Long categoryId) {
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        if (title != null) content.setTitle(title);
        if (body != null) content.setBody(body);
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            content.setCategory(category);
        }

        return contentRepository.save(content);
    }

    public void deleteContent(Long id) {
        contentRepository.deleteById(id);
    }

    public Content publishContent(Long id) {
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Content not found"));
        content.publish();
        return contentRepository.save(content);
    }

    public Content unpublishContent(Long id) {
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Content not found"));
        content.unpublish();
        return contentRepository.save(content);
    }
}
