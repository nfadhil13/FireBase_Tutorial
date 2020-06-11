package com.fdev.betaplayer.service.model;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String username;
    @SuppressWarnings("WeakerAccess")
    private String phoneNumber;

    public User() {}

    public User(String uid, String phoneNumber) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
    }

    public User(String uid, String username, String phoneNumber) {
        this.uid = uid;
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

