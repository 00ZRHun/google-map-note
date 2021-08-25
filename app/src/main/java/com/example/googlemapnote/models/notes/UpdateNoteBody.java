package com.example.googlemapnote.models.notes;

import com.example.googlemapnote.models.location.Location;
import com.example.googlemapnote.models.tag.Tag;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UpdateNoteBody implements Serializable {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("tags")
    private List<Tag> tagList;

    public List<Tag> getTagList() {
        return tagList;
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

}
