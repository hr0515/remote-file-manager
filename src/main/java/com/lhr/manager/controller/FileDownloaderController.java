package com.lhr.manager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
public class FileDownloaderController {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloaderController.class);

    @GetMapping("/file-downloader")
    public ResponseEntity<Resource> exec(@RequestParam("file") @Nullable String filePath) throws FileNotFoundException, UnsupportedEncodingException {

        if (filePath == null) return ResponseEntity.notFound().build();

        File file = new File(filePath);
        logger.info("下载文件从 [{}]", file.getAbsolutePath());

        // 确保文件存在
        if (!file.exists()) {
            logger.info("文件不存在 [{}]", file.getAbsolutePath());
            return ResponseEntity.notFound().build();
        }

        // 设置响应头
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(file.getName(), "utf-8"));
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // 创建输入流
        Resource resource = new InputStreamResource(new FileInputStream(file));

        // 返回响应实体
        return ResponseEntity.ok()
                .headers(header)
                .body(resource);
    }

}
