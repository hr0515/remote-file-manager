package com.lhr.manager.entity;

import cn.hutool.core.date.DateTime;

import java.util.Date;

/**
 * @description:
 * @author: LHR
 * @date: 2024-05-31 00:26
 **/
public class Version {

    private int id;
    private String fileName;
    private long size;
    private DateTime time;
    private String name;
    private String path;
    private String note;

    public Version() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Version(int id, String fileName, long size, DateTime time, String name, String path, String note) {
        this.id = id;
        this.fileName = fileName;
        this.size = size;
        this.time = time;
        this.name = name;
        this.path = path;
        this.note = note;
    }
}
