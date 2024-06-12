package com.lhr.manager.entity;

/**
 * @description:
 * @author: LHR
 * @date: 2024-04-20 15:02
 **/
public class User {

    private String userName;
    private String password;
    private String nikeName;

    private Integer tryTimes;
    private Boolean isOn; // 是否登录状态
    private Boolean isLocked; // 是否是锁定状态

    public User(String nikeName, String userName, String password, Integer tryTimes, Boolean isOn, Boolean isLocked) {
        this.nikeName = nikeName;
        this.userName = userName;
        this.password = password;
        this.tryTimes = tryTimes;
        this.isOn = isOn;
        this.isLocked = isLocked;
    }

    public String getNikeName() {
        return nikeName;
    }

    public void setTryTimes(Integer tryTimes) {
        this.tryTimes = tryTimes;
    }

    public void setOn(Boolean on) {
        isOn = on;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Integer getTryTimes() {
        return tryTimes;
    }

    public Boolean getOn() {
        return isOn;
    }

    public Boolean getLocked() {
        return isLocked;
    }

}
