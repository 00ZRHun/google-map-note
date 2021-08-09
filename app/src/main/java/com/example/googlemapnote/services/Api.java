package com.example.googlemapnote.services;

import com.example.googlemapnote.models.NoteList;
import com.example.googlemapnote.models.Notes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {
    String API_VERSION = "v1";
    String BASE_URL = "https://geonote-mobile.herokuapp.com/api/" + API_VERSION + "/";

    @GET("notes")
    Call<NoteList> getNotes();
}
