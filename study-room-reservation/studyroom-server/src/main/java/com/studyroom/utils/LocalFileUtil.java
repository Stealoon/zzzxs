package com.studyroom.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class LocalFileUtil {

    private static final Logger log = LoggerFactory.getLogger(LocalFileUtil.class);
    private static final String UPLOAD_DIR = "./upload";

    public static String upload(MultipartFile file) {
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;

            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.write(filePath, file.getBytes());

            String url = "/upload/" + fileName;
            log.info("文件上传到本地：{}", url);
            return url;
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
    }
}
