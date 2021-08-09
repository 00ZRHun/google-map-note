package com.example.googlemapnote.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Notes {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("locations")
    private List<Location> locationList;

    public Notes(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }
}
