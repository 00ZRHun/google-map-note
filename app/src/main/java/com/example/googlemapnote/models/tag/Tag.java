package com.example.googlemapnote.models.tag;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Tag implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String tag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
