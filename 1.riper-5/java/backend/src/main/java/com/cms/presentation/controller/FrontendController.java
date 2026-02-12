package com.cms.presentation.controller;

import com.cms.application.dto.ContentDTO;
import com.cms.application.service.ContentApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台查询控制器
 * 提供前台展示所需的API接口
 * 
 * @author jxin
 * @date 2026-02-11
 */
@RestController
@RequestMapping("/api/frontend")
public class FrontendController {
    
    private final ContentApplicationService contentApplicationService;
    
    public FrontendController(ContentApplicationService contentApplicationService) {
        this.contentApplicationService = contentApplicationService;
    }
    
    /**
     * 查询已发布内容列表
     * 按发布时间降序排序
     */
    @GetMapping("/contents")
    public ResponseEntity<List<ContentDTO>> getPublishedContents() {
        List<ContentDTO> contents = contentApplicationService.getPublishedContents();
        return ResponseEntity.ok(contents);
    }
    
    /**
     * 查询已发布内容详情
     */
    @GetMapping("/contents/{id}")
    public ResponseEntity<ContentDTO> getPublishedContent(@PathVariable Long id) {
        try {
            ContentDTO content = contentApplicationService.getContent(id);
            // 验证是否已发布
            if (!"PUBLISHED".equals(content.getStatus())) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(content);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
