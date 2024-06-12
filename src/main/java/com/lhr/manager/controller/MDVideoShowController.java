package com.lhr.manager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @description:
 * @author: LHR
 * @date: 2024-04-17 23:56
 **/
@RestController
public class MDVideoShowController {

    // video 标签 发送请求到这儿
    private static final Logger logger = LoggerFactory.getLogger(MDVideoShowController.class);

    @GetMapping("/video")
    public ResponseEntity<Resource> playVideo(@RequestParam("path") String path) {
        // Resolve the path to the video file
        Path paths = Paths.get(path);

        // Check if the file exists
        if (!Files.exists(paths)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", paths.getFileName().toString());

        // Create a FileSystemResource for the video file
        Resource videoResource = new FileSystemResource(path);

        try {
            // Return the video file as ResponseEntity
            // logger.info("打开视频 [{}]", path);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(videoResource.contentLength())
                    .body(videoResource);
        } catch (IOException e) {
            logger.error("打开视频失败 [{}]", path);
            // Handle exceptions
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
