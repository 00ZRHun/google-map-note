package com.example.googlemapnote.models.notes;

import com.google.gson.annotations.SerializedName;

public class NoteResponse {
    @SerializedName("data")
    private Note note;

    @SerializedName("msg")
    private String msg;

    public Note getNote() {
        return note;
    }

    public String getMsg() {
        return msg;
    }
}
