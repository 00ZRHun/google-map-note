package com.example.googlemapnote.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NoteList {
    @SerializedName("data")
    private List<Notes> notes;

    public List<Notes> getNotes() {
        return notes;
    }
}
