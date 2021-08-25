package com.example.googlemapnote.models.tag;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TagList {
    @SerializedName("tags")
    private List<Tag> tags;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
