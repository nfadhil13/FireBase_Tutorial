package com.fdev.betaplayer.Util;

import android.app.Application;

import com.fdev.betaplayer.service.model.User;

public class UserSingleton  {

    private User user;
    private static UserSingleton instance;

    public UserSingleton(){}

    public static UserSingleton getInstance(){
        if(instance==null){
            synchronized (UserSingleton.class){
                if(instance==null){
                    instance = new UserSingleton();
                }
            }

        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
