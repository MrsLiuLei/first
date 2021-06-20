package com.tanhua.server;

import com.tanhua.domain.db.User;

public class UserHolder {
    private static ThreadLocal<User> threadLocal=new ThreadLocal<>();
    public static void setUser(User user){
        threadLocal.set(user);
    }
    public static User getUser(){
        return threadLocal.get();
    }
    public static Long getUserId(){
        return threadLocal.get().getId();
    }
}
