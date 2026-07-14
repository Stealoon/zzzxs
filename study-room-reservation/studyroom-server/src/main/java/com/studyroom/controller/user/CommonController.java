package com.studyroom.controller.user;

import com.studyroom.constant.MessageConstant;
import com.studyroom.exception.BaseException;
import com.studyroom.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController("userCommonController")
@RequestMapping("/user/common")
@Api(tags = "用户端通用接口")
public class CommonController {

    private static final Logger log = LoggerFactory.getLogger(CommonController.class);
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp");

    @Value("${studyroom.upload.path}")
    private String uploadPath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file != null ? file.getOriginalFilename() : "null");

        if (file == null || file.isEmpty()) {
            throw new BaseException(MessageConstant.UPLOAD_FAILED);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BaseException(MessageConstant.UPLOAD_FAILED);
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BaseException(MessageConstant.UPLOAD_FAILED);
        }

        String objectName = UUID.randomUUID().toString() + extension;

        File uploadDir = new File(this.uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (!created) {
                throw new BaseException(MessageConstant.UPLOAD_FAILED);
            }
        }

        File destFile = new File(uploadDir, objectName);
        try {
            Files.copy(file.getInputStream(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage());
            throw new BaseException(MessageConstant.UPLOAD_FAILED);
        }

        String filePath = "/upload/" + objectName;
        log.info("文件上传成功：{}, 实际存储路径：{}", filePath, destFile.getAbsolutePath());

        return Result.success(filePath);
    }
}
