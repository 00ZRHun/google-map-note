package com.example.googlemapnote.models.notes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DeleteNoteBody implements Serializable {
    @SerializedName("user_id")
    private int userId;

    public DeleteNoteBody(int userId) {
        this.userId = userId;
    }
}
