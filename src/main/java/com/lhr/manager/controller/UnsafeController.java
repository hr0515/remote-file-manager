package com.lhr.manager.controller;


import cn.hutool.core.date.DateTime;
import com.lhr.manager.entity.Share;
import com.lhr.manager.entity.Version;
import com.lhr.manager.util.FileCalc;
import com.lhr.manager.util.SQLiteHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 不用身份认证的 接口 有上传文件post  下载文件get 和 文件分享页面
 * @author: LHR
 * @date: 2024-05-26 01:09
 **/

@Controller
@RequestMapping("/unsafe")
public class UnsafeController {

    @Value("${repository.path}")
    private String repositoryPath;

    @Resource
    private SQLiteHelper sqLiteHelper;

    @Resource
    private FileDownloaderController fileDownloaderController;

    private static Logger log = LoggerFactory.getLogger(UnsafeController.class);

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat tokenSdf = new SimpleDateFormat("yyyyMMddHH");


    public boolean validateToken(String token) {
        Date date = new Date();
        String format = tokenSdf.format(date);
        return format.equals(token);
    }

    private static class RepositoryResult {
        public String msg;
        public int code;
        public Object ret;
    }

    @PostMapping("/repository/push")
    @ResponseBody
    public RepositoryResult push(@RequestParam(required = false) String token, MultipartFile file, String name) {
        RepositoryResult ret = new RepositoryResult();
        if (validateToken(token)) {
            ret.msg = "合法调用, 文件上传可能失败";
            String originalFileAbPath = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).toString();
            String substring = originalFileAbPath.substring(originalFileAbPath.indexOf(name) + name.length());
            Path path = Paths.get(repositoryPath, name, token, Paths.get(substring).toString());
            String replace = path.toString().replace("\\", "/");
            File save = new File(Paths.get(replace).getParent().toString());
            if (!save.exists()) save.mkdirs();
            try {
                file.transferTo(Paths.get(replace));
                ret.msg = "合法调用, 文件上传成功 [" + replace + "]";
                log.info("文件上传push: {}", replace);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            ret.code = 1;
            ret.msg = "不合法调用";
        }
        return ret;
    }

    @GetMapping("/repository/list")
    @ResponseBody
    public List<String> list() {
        File file = new File(repositoryPath);
        if (!file.exists()) file.mkdirs();
        File[] files = file.listFiles(File::isDirectory);
        if (files != null) return files.length == 0 ? new ArrayList<>() : Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        else return new ArrayList<>();
    }

    private String getLatestVersionCode(String name) {
        String ret = null;
        File file = new File(Paths.get(repositoryPath, name).toString());
        if (!file.exists()) file.mkdirs();
        File[] files = file.listFiles(File::isDirectory);
        if (files != null && files.length > 0) {
            List<String> versions = Arrays.stream(files).map(File::getName).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            ret = versions.get(0);
        }
        return ret;
    }
    @GetMapping("/repository/list/detail")
    @ResponseBody
    public RepositoryResult listDetail(@RequestParam(required = false) String token, String repositoryName, String name, String versionCode) throws IOException {
        List<Version> list = new ArrayList<>();
        RepositoryResult ret = new RepositoryResult();
        if (validateToken(token)) {
            if (versionCode == null) versionCode = getLatestVersionCode(repositoryName);
            if (versionCode != null) {
                ret.msg = "合法调用, 文件下载可能失败";
                log.info("构建文件id, 准备clone");
                Files.walkFileTree(Paths.get(repositoryPath, repositoryName, versionCode), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        list.add(sqLiteHelper.insertVersion(file.getFileName().toString(), Files.size(file), name, file.toString(), null));
                        return FileVisitResult.CONTINUE;
                    }
                });
                log.info("调取项目文件说明, 开始clone");
                ret.msg = "合法调用, 共 [" + list.size() +  "] 个文件";
                ret.ret = list;
            }else {
                ret.code = 2;
                ret.msg = "合法调用, 未找到项目, 请检查项目名或版本号";
            }
        }else {
            ret.code = 1;
            ret.msg = "不合法调用";
        }
        return ret;
    }
    @GetMapping("/repository/down")
    public ResponseEntity<org.springframework.core.io.Resource> repositoryDownload(int id) throws FileNotFoundException, UnsupportedEncodingException {
        Version version = sqLiteHelper.selectVersionById(id);
        Date previousDate = version.getTime();
        Date currentDate = new Date();
        long diffInMillis = currentDate.getTime() - previousDate.getTime();
        // 判断时间差是否大于等于一天的毫秒数
        long oneDayInMillis = 60 * 60 * 1000; // 一小时
        if (diffInMillis >= oneDayInMillis) {
            return null;
        } else {
            return fileDownloaderController.exec(version.getPath());  // 没超过一小时才下载 超过了 那就... 判定不合法
        }
    }

    @GetMapping("/down")
    public ResponseEntity<org.springframework.core.io.Resource> download(int id) throws FileNotFoundException, UnsupportedEncodingException {
        Share share = sqLiteHelper.selectShareById(id);
        return fileDownloaderController.exec(share.getPath());
    }

    // 下载页面显示
    @GetMapping("/download/{id}")
    public ModelAndView downloadPage(@PathVariable("id") Integer id) {
        ModelAndView mv = new ModelAndView("unsafe-download");
        Share share = sqLiteHelper.selectShareById(id);
        if (share.getId() == 0) {
            share.setFileName("链接已过期!");
            share.setTime(new DateTime());
            share.setName("");
            share.setNote("");
        }
        mv.addObject("id", share.getId());
        mv.addObject("fileName", share.getFileName());
        mv.addObject("size", FileCalc.convert(share.getSize()));
        mv.addObject("time", sdf.format(share.getTime()));
        mv.addObject("name", share.getName());
        mv.addObject("note", share.getNote());
        return mv;
    }
}
