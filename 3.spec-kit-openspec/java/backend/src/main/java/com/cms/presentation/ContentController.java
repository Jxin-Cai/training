package com.cms.presentation;

import com.cms.application.ContentService;
import com.cms.domain.content.Content;
import com.cms.domain.content.ContentStatus;
import com.cms.presentation.dto.ContentSummary;
import com.cms.presentation.dto.CreateContentRequest;
import com.cms.presentation.dto.UpdateContentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/content")
@CrossOrigin(originPatterns = "http://localhost:*", allowCredentials = "true")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping
    public ResponseEntity<List<ContentSummary>> getAllContent(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        ContentStatus contentStatus = status != null ? ContentStatus.valueOf(status) : null;
        List<ContentSummary> content;

        if (categoryId != null) {
            content = contentService.getContentByCategory(categoryId, false, contentStatus).stream()
                .map(ContentSummary::from)
                .collect(Collectors.toList());
        } else {
            content = contentService.getAllContent().stream()
                .map(ContentSummary::from)
                .collect(Collectors.toList());
        }

        return ResponseEntity.ok(content);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable Long id) {
        Content content = contentService.getContentById(id);
        if (content == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(content);
    }

    @PostMapping
    public ResponseEntity<Content> createContent(@RequestBody CreateContentRequest request) {
        ContentStatus status = request.getStatus() != null ?
            ContentStatus.valueOf(request.getStatus()) : ContentStatus.DRAFT;

        Content content = contentService.createContent(
            request.getTitle(),
            request.getBody(),
            request.getCategoryId(),
            status
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(content);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Content> updateContent(
            @PathVariable Long id,
            @RequestBody UpdateContentRequest request) {
        Content content = contentService.updateContent(
            id,
            request.getTitle(),
            request.getBody(),
            request.getCategoryId()
        );
        return ResponseEntity.ok(content);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Content> publishContent(@PathVariable Long id) {
        Content content = contentService.publishContent(id);
        return ResponseEntity.ok(content);
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<Content> unpublishContent(@PathVariable Long id) {
        Content content = contentService.unpublishContent(id);
        return ResponseEntity.ok(content);
    }
}
