package com.lhr.manager.service;

import com.lhr.manager.configuration.Sfe4jConfiguration;
import com.lhr.manager.constant.GlobalConstants;
import com.lhr.manager.util.FileUtils;
import com.lhr.manager.vo.FileContentVO;
import com.lhr.manager.vo.FileTreeVO;
import com.lhr.manager.vo.FileVO;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class FileExplorerService {

    // 随机替换字串 因为 数据到前端会因为换行问题 导致JavaScript语法错误
    // 必须与前端保持一致
    private final static String ENTER_REPLACE = "BV,KeOX$e^S[po:^?c3#";

    private Sfe4jConfiguration sfe4jConfiguration;

    public Map<String, Object> buildMetaInfo() {
        Map<String, Object> metaInfo = new HashMap<>();
        metaInfo.put("title", StringUtils.isNotEmpty(sfe4jConfiguration.getTitle()) ? sfe4jConfiguration.getTitle() : GlobalConstants.DEFAULT_TITLE);
        metaInfo.put("description", StringUtils.isNotEmpty(sfe4jConfiguration.getDescription()) ? sfe4jConfiguration.getDescription() : GlobalConstants.DEFAULT_DESCRIPTION);
        metaInfo.put("quickLinks", sfe4jConfiguration.getQuickLinks() != null ? sfe4jConfiguration.getQuickLinks() : Collections.emptyMap());
        return metaInfo;
    }

    public FileTreeVO buildFileTree(String dir) {

        String currentDirectoryPath = StringUtils.isNotEmpty(dir) ? dir : sfe4jConfiguration.getBaseDirPath();

        File currentDirectory = new File(currentDirectoryPath);

        if (sfe4jConfiguration.getRestrictToBaseDir() && !isWithinBase(currentDirectory)) {
            currentDirectory = new File(sfe4jConfiguration.getBaseDirPath());
        }

        Set<FileVO> childDirectories = new TreeSet<>();
        Set<FileVO> files = new TreeSet<>();

        for (File file : currentDirectory.listFiles()) {
            if (file.isDirectory()) {
                childDirectories.add(new FileVO(file));
            } else {
                files.add(new FileVO(file));
            }
        }

        FileTreeVO fileTree = new FileTreeVO();
        fileTree.setCurrentDirectory(new FileVO(currentDirectory));
        if (currentDirectory.getParentFile() != null
                && (!sfe4jConfiguration.getRestrictToBaseDir() || isWithinBase(currentDirectory.getParentFile()))) {
            fileTree.setParentDirectory(new FileVO(currentDirectory.getParentFile()));
        }
        fileTree.setChildDirectories(childDirectories);
        fileTree.setFiles(files);

        return fileTree;
    }

    private boolean isWithinBase(File dir) {
        boolean isWithinBase = false;
        File localDir = dir;
        File baseDir = new File(sfe4jConfiguration.getBaseDirPath());
        if (baseDir.equals(dir)) {
            isWithinBase = true;
        }

        while (!isWithinBase && localDir.getParentFile() != null) {
            if (baseDir.equals(localDir.getParentFile())) {
                isWithinBase = true;
            }
            localDir = localDir.getParentFile();
        }

        return isWithinBase;
    }

    /**
     * Setter
     */
    public void setSfe4jConfiguration(Sfe4jConfiguration sfe4jConfiguration) {
        this.sfe4jConfiguration = sfe4jConfiguration;
    }

    public FileContentVO readFile(String filePath) {
        try {
            File file = new File(filePath);

            FileContentVO fileContentVO = new FileContentVO();
            fileContentVO.setFileName(file.getName());

            String content = new String(FileUtils.readToByteArray(file));
            fileContentVO.setContent(content.replace("\r\n", ENTER_REPLACE).replace("\n", ENTER_REPLACE));
            fileContentVO.setAbsolutePath(file.getParentFile().getAbsolutePath() + "/");

            return fileContentVO;
        } catch (IOException exp) {
            return null;
        }
    }
}
