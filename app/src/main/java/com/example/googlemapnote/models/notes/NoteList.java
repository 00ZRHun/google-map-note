package com.example.googlemapnote.models.notes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NoteList {
    @SerializedName("data")
    private List<Note> notes;

    public List<Note> getNotes() {
        return notes;
    }
}
