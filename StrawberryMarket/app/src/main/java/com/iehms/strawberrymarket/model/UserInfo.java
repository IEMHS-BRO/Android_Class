package com.iehms.strawberrymarket.model;

public class UserInfo {

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserInfo(String createdAt, int id, String name, String phone, String updatedAt, String username) {
        this.createdAt = createdAt;
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.updatedAt = updatedAt;
        this.username = username;
    }

    String createdAt;

    int id;

    String name;

    String phone;

    String updatedAt;

    String username;

}
