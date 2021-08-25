package com.example.googlemapnote.services;

import com.example.googlemapnote.models.notes.NewNoteRequest;
import com.example.googlemapnote.models.notes.NoteList;
import com.example.googlemapnote.models.notes.NoteResponse;
import com.example.googlemapnote.models.notes.UpdateNoteRequest;
import com.example.googlemapnote.models.user.User;
import com.example.googlemapnote.models.user.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Api {
    String API_VERSION = "v1";
    String BASE_URL = "https://geonote-mobile.herokuapp.com/api/" + API_VERSION + "/";
        // WORDING: MAYBE consider to change geonotes (add 's')

    @GET("notes")
    Call<NoteList> getNotes();

    @POST("notes")
    Call<NoteResponse> addNote(
            @Body NewNoteRequest note
            );

    @PUT("notes/{id}")
    Call<NoteResponse> updateNote(
            @Path("id") int id,
            @Body UpdateNoteRequest note
            );

    @POST("login")
    Call<UserResponse> login(
            @Body User user
            );

}
