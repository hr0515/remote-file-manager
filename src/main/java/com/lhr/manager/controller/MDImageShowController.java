package com.lhr.manager.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class MDImageShowController {

    private static final Logger logger = LoggerFactory.getLogger(MDImageShowController.class);

    @GetMapping("/image")
    public ResponseEntity<Resource> getImage(@RequestParam("path") String path) {
        try {
            Path file = Paths.get(path);
            Resource resource = new UrlResource(file.toUri());
            Path fileName = file.getFileName();

            // 选择显示形式 这里不写 mediaType 返回的就是 二进制文本数据
            MediaType mediaType = MediaType.IMAGE_JPEG;
            if (fileName.toString().toLowerCase().endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else if (fileName.toString().toLowerCase().endsWith(".gif")) {
                mediaType = MediaType.IMAGE_GIF;
            }
            if (resource.exists() || resource.isReadable()) {
//                logger.info("打开图片 [{}]", path);
                return ResponseEntity.ok().contentType(mediaType).body(resource);
            } else {
                logger.info("打开图片失败 [{}]", path);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
