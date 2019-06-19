package com.example.androidnotification.model;

import android.graphics.drawable.Drawable;

public class MyItemNotification {
    private Drawable avatar;
    private String name;
    private String content;

    public MyItemNotification() {
    }

    public MyItemNotification(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public MyItemNotification(Drawable avatar, String name, String content) {
        this.avatar = avatar;
        this.name = name;
        this.content = content;
    }

    public Drawable getAvatar() {
        return avatar;
    }

    public void setAvatar(Drawable avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
