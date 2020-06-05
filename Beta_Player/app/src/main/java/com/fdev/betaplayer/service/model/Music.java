package com.fdev.betaplayer.service.model;

import com.google.firebase.Timestamp;

public class Music {

    private String title;
    private String PostByUID;
    private String postBy;
    private Timestamp postTime;
    private String imageURL;
    private String musicURL;

    public Music() {
    }

    public Music(String title, String PostByUID, String postBy, Timestamp postTime, String imageURL, String musicURL) {
        this.title = title;
        this.PostByUID = PostByUID;
        this.postBy = postBy;
        this.postTime = postTime;
        this.imageURL = imageURL;
        this.musicURL = musicURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostByUID() {
        return PostByUID;
    }

    public void setPostByUID(String postByUID) {
        this.PostByUID = postByUID;
    }

    public String getPostBy() {
        return postBy;
    }

    public void setPostBy(String postBy) {
        this.postBy = postBy;
    }

    public Timestamp getPostTime() {
        return postTime;
    }

    public void setPostTime(Timestamp postTime) {
        this.postTime = postTime;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getMusicURL() {
        return musicURL;
    }

    public void setMusicURL(String musicURL) {
        this.musicURL = musicURL;
    }
}
