package com.health.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class AvatarStorageService {

    private static final long MAX_SIZE = 2 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg", "image/webp");
    private final Path storageDir;

    public AvatarStorageService(@Value("${app.avatar-dir:uploads/avatars}") String storageDir) {
        this.storageDir = Paths.get(storageDir).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("请选择头像文件");
        if (file.getSize() > MAX_SIZE) throw new IllegalArgumentException("头像不能超过2MB");
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_TYPES.contains(contentType)) throw new IllegalArgumentException("头像只支持PNG、JPG或WebP格式");
        try {
            if (ImageIO.read(file.getInputStream()) == null) throw new IllegalArgumentException("头像文件内容无效");
            Files.createDirectories(storageDir);
            String extension = "image/png".equals(contentType) ? ".png" : "image/webp".equals(contentType) ? ".webp" : ".jpg";
            String filename = UUID.randomUUID() + extension;
            Files.copy(file.getInputStream(), storageDir.resolve(filename));
            return "/api/uploads/avatars/" + filename;
        } catch (IOException exception) {
            throw new IllegalStateException("头像保存失败，请稍后重试", exception);
        }
    }
}
