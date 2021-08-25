package com.example.googlemapnote.models.notes;

import com.example.googlemapnote.models.location.NewLocationRequest;
import com.google.gson.annotations.SerializedName;

public class UpdateNoteRequest {
    @SerializedName("note")
    private UpdateNoteBody note;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("tags")
    private String[] tags;


    public UpdateNoteRequest(UpdateNoteBody note, int userId, String[] tags) {
        this.note = note;
        this.userId = userId;
        this.tags = tags;
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

}
