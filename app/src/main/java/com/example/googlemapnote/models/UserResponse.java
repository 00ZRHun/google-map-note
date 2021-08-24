package com.example.googlemapnote.models;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("data")
    private User user;

    @SerializedName("msg")
    private String msg;

    public void setUser(User user) {
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public User getUser() {
        return user;
    }
}
