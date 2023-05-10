package com.example.githubapi;

public class User {

    public User(String userId, String url, String photoUrl) {
        this.userId = userId;
        this.url = url;
        this.photoUrl = photoUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    String userId;
    String url;
    String photoUrl;

}
