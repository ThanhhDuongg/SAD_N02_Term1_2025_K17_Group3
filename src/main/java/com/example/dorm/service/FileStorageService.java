package com.example.dorm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation;
    private final Path avatarLocation;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.avatarLocation = rootLocation.resolve("avatars");
        initDirectories();
    }

    private void initDirectories() {
        try {
            Files.createDirectories(avatarLocation);
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể khởi tạo thư mục lưu trữ ảnh đại diện", ex);
        }
    }

    public String storeAvatar(MultipartFile file, String currentFilename) {
        if (file == null || file.isEmpty()) {
            return currentFilename;
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String newFilename = UUID.randomUUID().toString().replace("-", "");
        if (StringUtils.hasText(extension)) {
            newFilename = newFilename + "." + extension.toLowerCase(Locale.ROOT);
        }

        Path target = avatarLocation.resolve(newFilename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể lưu ảnh đại diện. Vui lòng thử lại sau.", ex);
        }

        deleteAvatar(currentFilename);
        return newFilename;
    }

    public void deleteAvatar(String filename) {
        if (!StringUtils.hasText(filename)) {
            return;
        }

        Path filePath = avatarLocation.resolve(Paths.get(filename).getFileName().toString());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
            // Bỏ qua lỗi khi xóa file cũ để tránh gián đoạn trải nghiệm người dùng
        }
    }
}
