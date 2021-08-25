package com.example.googlemapnote.models.location;

import com.google.gson.annotations.SerializedName;

public class NewLocationRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("uniqueLngLatId")
    private String uniqueLngLatId;

    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    public NewLocationRequest(String name, String uniqueLngLatId, double lat, double lng) {
        this.name = name;
        this.uniqueLngLatId = uniqueLngLatId;
        this.lat = lat;
        this.lng = lng;
    }
}
