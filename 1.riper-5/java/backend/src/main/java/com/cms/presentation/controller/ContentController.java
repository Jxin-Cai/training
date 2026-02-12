package com.cms.presentation.controller;

import com.cms.application.dto.ContentDTO;
import com.cms.application.service.ContentApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内容管理控制器
 * 
 * @author jxin
 * @date 2026-02-11
 */
@RestController
@RequestMapping("/api/contents")
public class ContentController {
    
    private final ContentApplicationService contentApplicationService;
    
    public ContentController(ContentApplicationService contentApplicationService) {
        this.contentApplicationService = contentApplicationService;
    }
    
    /**
     * 创建内容
     */
    @PostMapping
    public ResponseEntity<ContentDTO> createContent(@RequestBody ContentDTO dto) {
        try {
            ContentDTO created = contentApplicationService.createContent(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 查询内容列表
     * 支持按状态筛选
     */
    @GetMapping
    public ResponseEntity<List<ContentDTO>> getAllContents(
            @RequestParam(required = false) String status) {
        try {
            List<ContentDTO> contents;
            if (status != null && !status.trim().isEmpty()) {
                contents = contentApplicationService.getContentsByStatus(status);
            } else {
                contents = contentApplicationService.getAllContents();
            }
            return ResponseEntity.ok(contents);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 查询单个内容
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContentDTO> getContent(@PathVariable Long id) {
        try {
            ContentDTO content = contentApplicationService.getContent(id);
            return ResponseEntity.ok(content);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 更新内容
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContentDTO> updateContent(@PathVariable Long id, @RequestBody ContentDTO dto) {
        try {
            ContentDTO updated = contentApplicationService.updateContent(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除内容
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        try {
            contentApplicationService.deleteContent(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 发布内容
     */
    @PutMapping("/{id}/publish")
    public ResponseEntity<ContentDTO> publishContent(@PathVariable Long id) {
        try {
            ContentDTO published = contentApplicationService.publishContent(id);
            return ResponseEntity.ok(published);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 取消发布内容
     */
    @PutMapping("/{id}/unpublish")
    public ResponseEntity<ContentDTO> unpublishContent(@PathVariable Long id) {
        try {
            ContentDTO unpublished = contentApplicationService.unpublishContent(id);
            return ResponseEntity.ok(unpublished);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
