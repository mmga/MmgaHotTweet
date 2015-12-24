package com.mmga.mmgahottweet.data.model;

import com.google.gson.annotations.SerializedName;


public class Status {
    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("text")
    private String text;

    @SerializedName("user")
    private User user;

    @SerializedName("entities")
    private Entities entities;

    @SerializedName("id_str")
    private String lastId;

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

    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }
}