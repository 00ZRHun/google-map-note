package com.example.googlemapnote.models.notes;

import com.example.googlemapnote.models.location.Location;
import com.example.googlemapnote.models.location.NewLocationRequest;
import com.google.gson.annotations.SerializedName;

public class NewNoteRequest {
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("tags")
    private String[] tags;
    @SerializedName("geolocation")
    private NewLocationRequest geolocation;

    public NewNoteRequest(String title, String description, int userId, String[] tags, NewLocationRequest geolocation) {
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.tags = tags;
        this.geolocation = geolocation;
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

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public NewLocationRequest getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(NewLocationRequest geolocation) {
        this.geolocation = geolocation;
    }
}
