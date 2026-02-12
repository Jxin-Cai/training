package com.cms.application.service;

import com.cms.application.dto.ContentDTO;
import com.cms.domain.model.category.Category;
import com.cms.domain.model.content.Content;
import com.cms.domain.model.content.ContentStatus;
import com.cms.domain.repository.CategoryRepository;
import com.cms.domain.repository.ContentRepository;
import com.cms.domain.service.MarkdownRenderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 内容应用服务
 * 协调领域对象完成业务用例
 * 
 * @author jxin
 * @date 2026-02-11
 */
@Service
@Transactional
public class ContentApplicationService {
    
    private final ContentRepository contentRepository;
    private final CategoryRepository categoryRepository;
    private final MarkdownRenderService markdownRenderService;
    
    public ContentApplicationService(ContentRepository contentRepository,
                                   CategoryRepository categoryRepository,
                                   MarkdownRenderService markdownRenderService) {
        this.contentRepository = contentRepository;
        this.categoryRepository = categoryRepository;
        this.markdownRenderService = markdownRenderService;
    }
    
    /**
     * 创建内容
     */
    public ContentDTO createContent(ContentDTO dto) {
        validateContentDTO(dto);
        
        // 验证分类是否存在
        if (!categoryRepository.existsById(dto.getCategoryId())) {
            throw new IllegalArgumentException("分类不存在: " + dto.getCategoryId());
        }
        
        // 渲染Markdown为HTML
        String renderedHtml = markdownRenderService.renderToHtml(dto.getMarkdownContent());
        
        // 创建领域对象
        Content content = Content.create(
                dto.getTitle(),
                dto.getMarkdownContent(),
                renderedHtml,
                dto.getCategoryId()
        );
        
        Content saved = contentRepository.save(content);
        return enrichWithCategoryName(ContentDTO.fromDomain(saved));
    }
    
    /**
     * 更新内容
     */
    public ContentDTO updateContent(Long id, ContentDTO dto) {
        validateContentDTO(dto);
        
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("内容不存在: " + id));
        
        // 验证分类是否存在
        if (!categoryRepository.existsById(dto.getCategoryId())) {
            throw new IllegalArgumentException("分类不存在: " + dto.getCategoryId());
        }
        
        // 渲染Markdown为HTML
        String renderedHtml = markdownRenderService.renderToHtml(dto.getMarkdownContent());
        
        // 更新领域对象
        content.updateContent(
                dto.getTitle(),
                dto.getMarkdownContent(),
                renderedHtml,
                dto.getCategoryId()
        );
        
        Content updated = contentRepository.save(content);
        return enrichWithCategoryName(ContentDTO.fromDomain(updated));
    }
    
    /**
     * 删除内容
     */
    public void deleteContent(Long id) {
        if (!contentRepository.existsById(id)) {
            throw new IllegalArgumentException("内容不存在: " + id);
        }
        contentRepository.deleteById(id);
    }
    
    /**
     * 查询单个内容
     */
    @Transactional(readOnly = true)
    public ContentDTO getContent(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("内容不存在: " + id));
        return enrichWithCategoryName(ContentDTO.fromDomain(content));
    }
    
    /**
     * 查询所有内容
     */
    @Transactional(readOnly = true)
    public List<ContentDTO> getAllContents() {
        List<Content> contents = contentRepository.findAll();
        return enrichListWithCategoryNames(contents);
    }
    
    /**
     * 根据状态查询内容
     */
    @Transactional(readOnly = true)
    public List<ContentDTO> getContentsByStatus(String statusStr) {
        ContentStatus status = ContentStatus.valueOf(statusStr.toUpperCase());
        List<Content> contents = contentRepository.findByStatus(status);
        return enrichListWithCategoryNames(contents);
    }
    
    /**
     * 发布内容
     */
    public ContentDTO publishContent(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("内容不存在: " + id));
        
        content.publish();
        Content updated = contentRepository.save(content);
        return enrichWithCategoryName(ContentDTO.fromDomain(updated));
    }
    
    /**
     * 取消发布内容
     */
    public ContentDTO unpublishContent(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("内容不存在: " + id));
        
        content.unpublish();
        Content updated = contentRepository.save(content);
        return enrichWithCategoryName(ContentDTO.fromDomain(updated));
    }
    
    /**
     * 查询已发布内容（前台使用）
     */
    @Transactional(readOnly = true)
    public List<ContentDTO> getPublishedContents() {
        List<Content> contents = contentRepository.findPublishedOrderByPublishTimeDesc();
        return enrichListWithCategoryNames(contents);
    }
    
    /**
     * 验证内容DTO
     */
    private void validateContentDTO(ContentDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        if (dto.getMarkdownContent() == null || dto.getMarkdownContent().trim().isEmpty()) {
            throw new IllegalArgumentException("内容不能为空");
        }
        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("必须选择分类");
        }
    }
    
    /**
     * 为单个DTO填充分类名称
     */
    private ContentDTO enrichWithCategoryName(ContentDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElse(null);
        if (category != null) {
            dto.setCategoryName(category.getName());
        }
        return dto;
    }
    
    /**
     * 为DTO列表批量填充分类名称
     */
    private List<ContentDTO> enrichListWithCategoryNames(List<Content> contents) {
        // 获取所有涉及的分类
        List<Long> categoryIds = contents.stream()
                .map(Content::getCategoryId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, Category> categoryMap = categoryRepository.findAll().stream()
                .filter(c -> categoryIds.contains(c.getId()))
                .collect(Collectors.toMap(Category::getId, Function.identity()));
        
        // 填充分类名称
        return contents.stream()
                .map(content -> {
                    ContentDTO dto = ContentDTO.fromDomain(content);
                    Category category = categoryMap.get(content.getCategoryId());
                    if (category != null) {
                        dto.setCategoryName(category.getName());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
