package com.snapora.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Component
public class FileStorageUtil {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "video/mp4", "video/webm"
    );

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file type. Allowed: jpg, png, gif, webp, mp4, webm");
        }
        LocalDate now = LocalDate.now();
        String subDir = now.getYear() + "/" + String.format("%02d", now.getMonthValue());
        Path dirPath = Paths.get(uploadDir, subDir);
        Files.createDirectories(dirPath);

        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + extension;
        Path filePath = dirPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + subDir + "/" + filename;
    }

    public void deleteFile(String url) {
        if (url == null || !url.startsWith("/uploads/")) return;
        try {
            Path path = Paths.get(uploadDir, url.substring("/uploads/".length()));
            Files.deleteIfExists(path);
        } catch (IOException ignored) {}
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
