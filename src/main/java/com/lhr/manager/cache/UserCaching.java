package com.lhr.manager.cache;

import com.lhr.manager.entity.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: LHR
 * @date: 2024-04-20 15:11
 **/
public class UserCaching {

    private static final Map<String, User> userPool = new ConcurrentHashMap<>();

    public static User getUser(String userName) {
        return userPool.get(userName);
    }
    public static void setUser(String userName, User user) {
        userPool.put(userName, user);
    }

}
