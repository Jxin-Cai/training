package com.cms.presentation.rest;

import com.cms.application.dto.CategoryDto;
import com.cms.application.dto.CategoryTreeDto;
import com.cms.application.dto.CreateCategoryRequest;
import com.cms.application.dto.UpdateCategoryRequest;
import com.cms.application.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody CreateCategoryRequest request) {
        try {
            CategoryDto created = categoryService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable String id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<CategoryTreeDto>> getTree() {
        return ResponseEntity.ok(categoryService.findTree());
    }
    
    @GetMapping("/flat")
    public ResponseEntity<List<CategoryDto>> getFlat() {
        return ResponseEntity.ok(categoryService.findFlat());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable String id, 
                                               @RequestBody UpdateCategoryRequest request) {
        try {
            return categoryService.update(id, request)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            if (categoryService.delete(id)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
