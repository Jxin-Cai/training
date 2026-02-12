package com.cms.presentation.rest;

import com.cms.application.service.ImageUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "http://localhost:3000")
public class ImageController {
    
    private final ImageUploadService imageUploadService;
    
    public ImageController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }
    
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String dataUrl = imageUploadService.convertToBase64(file);
            return ResponseEntity.ok(Map.of("url", dataUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to process image"));
        }
    }
}
