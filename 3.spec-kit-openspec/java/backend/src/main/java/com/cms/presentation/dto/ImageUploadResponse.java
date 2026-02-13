package com.cms.presentation.dto;

import com.cms.domain.image.Image;

public class ImageUploadResponse {
    private Long id;
    private String url;
    private String filename;
    private String originalFilename;
    private String mimeType;
    private Long fileSize;
    private Integer width;
    private Integer height;

    public static ImageUploadResponse from(Image image) {
        ImageUploadResponse response = new ImageUploadResponse();
        response.setId(image.getId());
        response.setUrl(image.getUrl());
        response.setFilename(image.getFilename());
        response.setOriginalFilename(image.getOriginalFilename());
        response.setMimeType(image.getMimeType());
        response.setFileSize(image.getFileSize());
        response.setWidth(image.getWidth());
        response.setHeight(image.getHeight());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
}
