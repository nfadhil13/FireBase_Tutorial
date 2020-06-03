package com.fdev.betaplayer.service.model;

public class Music {

    private String title;
    private String singer;
    private String postBy;
    private Long postTime;
    private String imageURL;
    private String musicURL;

    public Music() {
    }

    public Music(String title, String singer, String postBy, Long postTime, String imageURL, String musicURL) {
        this.title = title;
        this.singer = singer;
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

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getPostBy() {
        return postBy;
    }

    public void setPostBy(String postBy) {
        this.postBy = postBy;
    }

    public Long getPostTime() {
        return postTime;
    }

    public void setPostTime(Long postTime) {
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
