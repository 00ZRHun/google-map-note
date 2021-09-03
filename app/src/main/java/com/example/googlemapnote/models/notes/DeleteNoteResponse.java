package com.example.googlemapnote.models.notes;

import com.google.gson.annotations.SerializedName;

public class DeleteNoteResponse {
    @SerializedName("data")
    private String msg;

    public String getMsg() {
        return msg;
    }
}
