package com.cms.presentation;

import com.cms.application.CategoryService;
import com.cms.domain.category.Category;
import com.cms.presentation.dto.CategoryResponse;
import com.cms.presentation.dto.CategoryTreeNode;
import com.cms.presentation.dto.ContentSummary;
import com.cms.presentation.dto.CreateCategoryRequest;
import com.cms.presentation.dto.UpdateCategoryRequest;
import com.cms.domain.content.ContentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(originPatterns = "http://localhost:*", allowCredentials = "true")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories().stream()
            .map(cat -> CategoryResponse.from(cat,
                categoryService.hasChildren(cat.getId()),
                categoryService.getContentCount(cat.getId())))
            .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryTreeNode>> getCategoryTree() {
        List<Category> rootCategories = categoryService.getRootCategories();
        List<CategoryTreeNode> tree = rootCategories.stream()
            .map(cat -> buildTreeNode(cat))
            .collect(Collectors.toList());
        return ResponseEntity.ok(tree);
    }

    private CategoryTreeNode buildTreeNode(Category category) {
        CategoryTreeNode node = CategoryTreeNode.from(category, categoryService.getContentCount(category.getId()));

        List<Category> children = categoryService.getChildren(category.getId());
        node.setChildren(children.stream()
            .map(child -> buildTreeNode(child))
            .collect(Collectors.toList()));

        return node;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        CategoryResponse response = CategoryResponse.from(category,
            categoryService.hasChildren(id),
            categoryService.getContentCount(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<List<CategoryResponse>> getChildren(@PathVariable Long id) {
        List<CategoryResponse> children = categoryService.getChildren(id).stream()
            .map(cat -> CategoryResponse.from(cat,
                categoryService.hasChildren(cat.getId()),
                categoryService.getContentCount(cat.getId())))
            .collect(Collectors.toList());
        return ResponseEntity.ok(children);
    }

    @GetMapping("/{id}/descendants")
    public ResponseEntity<List<CategoryResponse>> getDescendants(@PathVariable Long id) {
        List<CategoryResponse> descendants = categoryService.getDescendants(id).stream()
            .map(cat -> CategoryResponse.from(cat,
                categoryService.hasChildren(cat.getId()),
                categoryService.getContentCount(cat.getId())))
            .collect(Collectors.toList());
        return ResponseEntity.ok(descendants);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CreateCategoryRequest request) {
        Category category = categoryService.createCategory(
            request.getName(),
            request.getDescription(),
            request.getParentId(),
            request.getDisplayOrder()
        );
        CategoryResponse response = CategoryResponse.from(category, false, 0);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody UpdateCategoryRequest request) {
        Category category = categoryService.updateCategory(
            id,
            request.getName(),
            request.getDescription(),
            request.getParentId(),
            request.getDisplayOrder()
        );
        CategoryResponse response = CategoryResponse.from(category,
            categoryService.hasChildren(id),
            categoryService.getContentCount(id));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "PREVENT") String handleChildren,
            @RequestParam(defaultValue = "PREVENT") String handleContent) {
        categoryService.deleteCategory(id, handleChildren, handleContent);
        return ResponseEntity.noContent().build();
    }
}
