package com.studyroom.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController("adminCommonController")
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {

    private static final Logger log = LoggerFactory.getLogger(CommonController.class);
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp");

    @org.springframework.beans.factory.annotation.Value("${studyroom.upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Map<String, Object> upload(MultipartFile file){
        Map<String, Object> result = new HashMap<>();
        log.info("文件上传：{}", file != null ? file.getOriginalFilename() : "null");

        try {
            if (file == null || file.isEmpty()) {
                result.put("code", 500);
                result.put("message", "文件为空");
                return result;
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                result.put("code", 500);
                result.put("message", "文件名无效");
                return result;
            }

            String extension = "";
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
                extension = originalFilename.substring(dotIndex).toLowerCase();
            }

            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                result.put("code", 500);
                result.put("message", "文件格式不支持，请上传jpg/jpeg/png/gif/bmp格式的图片");
                return result;
            }

            String objectName = UUID.randomUUID().toString() + extension;

            java.io.File uploadDir = new java.io.File(this.uploadPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    result.put("code", 500);
                    result.put("message", "上传目录创建失败");
                    return result;
                }
            }

            java.io.File destFile = new java.io.File(uploadDir, objectName);
            Files.copy(file.getInputStream(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            String filePath = "/upload/" + objectName;
            log.info("文件上传成功：{}, 实际存储路径：{}", filePath, destFile.getAbsolutePath());
            result.put("code", 200);
            result.put("url", filePath);
            result.put("data", filePath);
            return result;
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage());
            result.put("code", 500);
            result.put("message", "文件上传失败");
            return result;
        }
    }

}