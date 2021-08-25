package com.example.googlemapnote;

import android.app.Application;

import com.example.googlemapnote.models.user.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class GlobalClass extends Application {

    private static GlobalClass instance;
    private User currentUser = new User();

    private LatLng marker1 = new LatLng(37.5015 ,-122.0301);  // OPTIONS: v: 37.3807, 37.4223; v1: -121.9974, -122.1133
    private ArrayList<LatLng> allExistedMarkerArrL = new ArrayList<LatLng>();
    //allExistedMarkerArrL.add(marker1, marker2, marker3); -> ???

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public static GlobalClass getInstance() {
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
