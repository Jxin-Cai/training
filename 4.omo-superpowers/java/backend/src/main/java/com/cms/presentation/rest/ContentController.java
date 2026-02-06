package com.cms.presentation.rest;

import com.cms.application.dto.ContentDto;
import com.cms.application.dto.CreateContentRequest;
import com.cms.application.dto.UpdateContentRequest;
import com.cms.application.service.ContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contents")
@CrossOrigin(origins = "http://localhost:3000")
public class ContentController {
    
    private final ContentService contentService;
    
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }
    
    @PostMapping
    public ResponseEntity<ContentDto> create(@RequestBody CreateContentRequest request) {
        try {
            ContentDto created = contentService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ContentDto> getById(@PathVariable String id) {
        return contentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<ContentDto>> getAll() {
        return ResponseEntity.ok(contentService.findAll());
    }
    
    @GetMapping("/published")
    public ResponseEntity<List<ContentDto>> getPublished() {
        return ResponseEntity.ok(contentService.findAllPublished());
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ContentDto>> getByCategory(@PathVariable String categoryId) {
        return ResponseEntity.ok(contentService.findByCategoryWithDescendants(categoryId));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ContentDto> update(@PathVariable String id, 
                                              @RequestBody UpdateContentRequest request) {
        try {
            return contentService.update(id, request)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/publish")
    public ResponseEntity<ContentDto> publish(@PathVariable String id) {
        return contentService.publish(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<ContentDto> unpublish(@PathVariable String id) {
        return contentService.unpublish(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (contentService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
