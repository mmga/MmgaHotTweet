package com.mmga.mmgahottweet.data.model;

import com.google.gson.annotations.SerializedName;


public class Status {
    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("text")
    private String text;

    @SerializedName("user")
    private User user;


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}