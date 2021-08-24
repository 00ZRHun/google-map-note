package com.example.googlemapnote.services;

import com.example.googlemapnote.models.NoteList;
import com.example.googlemapnote.models.User;
import com.example.googlemapnote.models.UserResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {
    String API_VERSION = "v1";
    String BASE_URL = "https://geonote-mobile.herokuapp.com/api/" + API_VERSION + "/";
        // WORDING: MAYBE consider to change geonotes (add 's')

    @GET("notes")
    Call<NoteList> getNotes();

    @POST("login")
    Call<UserResponse> login(
            @Body User user
            );
}
