package com.cms.domain.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "categories")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    @Column(nullable = false)
    private String path;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Set temporary path before insert (will be updated after ID is generated)
        if (path == null) {
            this.path = "/temp/";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    // Business logic methods
    public boolean isRoot() {
        return parent == null;
    }

    public boolean isDescendantOf(Category potentialAncestor) {
        if (potentialAncestor == null) return false;
        Category current = this.parent;
        while (current != null) {
            if (current.getId().equals(potentialAncestor.getId())) return true;
            current = current.getParent();
        }
        return false;
    }

    public boolean wouldCreateCircularReference(Category newParent) {
        if (newParent == null) return false;
        if (this.id == null) return false;
        return newParent.isDescendantOf(this) || newParent.getId().equals(this.id);
    }

    public void updateParent(Category newParent) {
        if (wouldCreateCircularReference(newParent)) {
            throw new CircularReferenceException("Cannot create circular category reference");
        }
        this.parent = newParent;
        updatePath();
    }

    public void updatePath() {
        if (id == null) return;

        if (parent == null) {
            this.path = "/" + id + "/";
        } else {
            this.path = parent.getPath() + id + "/";
        }
    }

    public List<Long> getPathAsIds() {
        return Arrays.stream(path.split("/"))
            .filter(s -> !s.isEmpty())
            .map(Long::parseLong)
            .collect(Collectors.toList());
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }

    public List<Category> getChildren() { return children; }
    public void setChildren(List<Category> children) { this.children = children; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
