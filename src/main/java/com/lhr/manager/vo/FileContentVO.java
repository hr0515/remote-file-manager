package com.lhr.manager.vo;

public class FileContentVO {
    private String fileName;
    private String content;
    private String absolutePath;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
