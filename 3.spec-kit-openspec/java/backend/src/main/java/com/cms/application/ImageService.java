package com.cms.application;

import com.cms.domain.image.Image;
import com.cms.domain.image.ImageRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Service
@Transactional
public class ImageService {

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private final Tika tika = new Tika();

    @Value("${app.upload.path:uploads}")
    private String uploadPath;

    @Autowired
    private ImageRepository imageRepository;

    public Image uploadImage(MultipartFile file) throws IOException {
        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }

        // Detect MIME type by magic bytes
        String detectedMimeType = tika.detect(file.getInputStream());
        if (!ALLOWED_MIME_TYPES.contains(detectedMimeType)) {
            throw new IllegalArgumentException("Invalid file type: " + detectedMimeType);
        }

        // Generate safe filename
        String extension = getExtension(detectedMimeType);
        String filename = Image.generateFilename(extension);

        // Re-encode image for security
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new IllegalArgumentException("Invalid image file");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = getFormatName(detectedMimeType);
        ImageIO.write(image, formatName, baos);

        // Save to file system
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path destination = uploadDir.resolve(filename);
        Files.write(destination, baos.toByteArray());

        // Create Image entity
        Image uploadedImage = new Image();
        uploadedImage.setFilename(filename);
        uploadedImage.setOriginalFilename(file.getOriginalFilename());
        uploadedImage.setMimeType(detectedMimeType);
        uploadedImage.setFileSize(file.getSize());
        uploadedImage.setWidth(image.getWidth());
        uploadedImage.setHeight(image.getHeight());

        return imageRepository.save(uploadedImage);
    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElse(null);
    }

    public void deleteImage(Long id) throws IOException {
        Image image = imageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        // Delete file from file system
        Path filePath = Paths.get(uploadPath, image.getFilename());
        Files.deleteIfExists(filePath);

        // Delete from database
        imageRepository.deleteById(id);
    }

    private String getExtension(String mimeType) {
        return switch (mimeType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> throw new IllegalStateException("Unknown MIME type: " + mimeType);
        };
    }

    private String getFormatName(String mimeType) {
        return switch (mimeType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            default -> throw new IllegalStateException("Unknown MIME type: " + mimeType);
        };
    }
}
