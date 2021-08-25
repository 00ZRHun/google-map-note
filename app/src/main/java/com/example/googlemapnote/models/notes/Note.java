package com.example.googlemapnote.models.notes;

import com.example.googlemapnote.models.location.Location;
import com.example.googlemapnote.models.tag.Tag;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Note implements Serializable {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("id")
    private int id;

    @SerializedName("locations")
    private List<Location> locationList;

    @SerializedName("tags")
    private List<Tag> tagList;

    public List<Tag> getTagList() {
        return tagList;
    }

    public int getId() {
        return id;
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
