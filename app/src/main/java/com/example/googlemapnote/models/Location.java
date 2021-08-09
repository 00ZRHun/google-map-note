package com.example.googlemapnote.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("id")
    private int id;

    @SerializedName("unique_lng_lat_id")
    private String uniqueLngLatId;

    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    public LatLng getLatLng() {
        return new LatLng(this.lat, this.lng);
    }
}
