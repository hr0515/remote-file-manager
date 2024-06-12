package com.lhr.manager.controller;


import com.lhr.manager.util.FileCalc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @description:
 * @author: LHR
 * @date: 2024-04-29 22:11
 **/
@Controller
public class UploadController {

    @Value("${upload.path}")
    private String filePath;

    private ServletContext servletContext;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    private final DateTimeFormatter format2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @GetMapping("/upload")
    public ModelAndView upload(@RequestParam(value = "dir", required = false) String dir) {
        ModelAndView mv = new ModelAndView("upload");
        mv.addObject("ctx", servletContext.getContextPath());

        String now = format.format(new Date());
        File file = new File(Paths.get(filePath, now).toString());
        ArrayList<String[]> nameTimeList = new ArrayList<>();  // {time, filename}
        if (file.exists()) {
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                try {
                    BasicFileAttributes attributes = Files.readAttributes(Paths.get(listFile.getAbsolutePath()), BasicFileAttributes.class);
                    FileTime creationTime = attributes.creationTime();
                    LocalDateTime dateTime = creationTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    nameTimeList.add(new String[] {dateTime.format(format2), listFile.getName()});
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else {
            // 不能让用户 任意 路径我都创建文件夹
            mkdirs(file);
        }


        // 根据listB对listA进行排序
        nameTimeList.sort(Comparator.comparing(arr -> arr[0], Collections.reverseOrder()));

        mv.addObject("nameTimeList", nameTimeList);
        String absolutePath = file.getAbsolutePath();
        mv.addObject("explorer", "/file-explorer?dir=" + absolutePath.replace("\\", "/"));

        return mv;
    }

    @PostMapping("/upload")
    @ResponseBody
    public Map upload(@RequestParam("file") MultipartFile file) {
        HashMap<String, Object> map = new HashMap<>();
        String now = format.format(new Date());

        try {
            File existFile = new File(Paths.get(filePath, now, file.getOriginalFilename()).toString());
            if (existFile.exists()) {
                logger.info("文件重复 [{}] [{}]", existFile.getAbsolutePath(), FileCalc.convert(file.getSize()));
                map.put("msg", "文件重复");
                map.put("code", false);
                return map;
            }else {
                mkdirs(existFile);
            }
            file.transferTo(existFile);

            logger.info("上传文件成功 [{}] [{}]", existFile.getAbsolutePath(), FileCalc.convert(file.getSize()));
            map.put("msg", "成功");
            map.put("code", true);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("存储异常");
            map.put("msg", "存储异常");
            map.put("code", false);
            return map;
        }
        return map;
    }
    @Autowired
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void mkdirs(File file) {
        if (file.mkdirs()) {
            logger.info("文件创建成功 [{}]", file.getAbsolutePath());
        } else {
            logger.info("文件创建失败 [{}]", file.getAbsolutePath());
        }
    }

}
