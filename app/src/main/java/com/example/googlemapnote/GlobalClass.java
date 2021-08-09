package com.example.googlemapnote;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class GlobalClass extends Application {

    private LatLng marker1 = new LatLng(37.5015 ,-122.0301);
//    private LatLng marker2 = new LatLng(37.3807 ,-121.9974);
//    private LatLng marker3 = new LatLng(37.4223 ,-122.1133);
    private ArrayList<LatLng> allExistedMarkerArrL = new ArrayList<LatLng>();
    //allExistedMarkerArrL.add(marker1, marker2, marker3); -> ???

    private boolean isCurrentMarkerInserted = false;
    private LatLng deletedElement;

    @Override
    public void onCreate() {
        super.onCreate();
//        allExistedMarkerArrL.add(marker1);
    }


    // [START insert_test_data]
    public GlobalClass() {
//        allExistedMarkerArrL.add(marker1);
//        allExistedMarkerArrL.add(marker2);
//        allExistedMarkerArrL.add(marker3);
    }
    // [END insert_test_data]

    // [START marker]
//    public ArrayList<LatLng> getMarker() {
//        return allExistedMarkerArrL;
//    }
//
//    public void addMarker(LatLng targetAddLatLng) {
//        Log.d("Add marker", "add marker");
//        if (!allExistedMarkerArrL.contains(targetAddLatLng)) {   // exclude the old element
//            allExistedMarkerArrL.add(targetAddLatLng);
//            isCurrentMarkerInserted = true;
//        }
//    }
//
//    // ??? -> store markerName instead of lat;lng
//    public void deleteMarker(LatLng targetDeleteLatLag) {
//        if (allExistedMarkerArrL.contains(targetDeleteLatLag)) {   // exclude the new element
//            // get element index and use the element at that index
//            allExistedMarkerArrL.remove(allExistedMarkerArrL.indexOf(targetDeleteLatLag));
//
//            // pass deleted element value to the setter function
//            setDeletedElement(targetDeleteLatLag);
//        }
//    }
//    // [END marker]
//
//    // [START other_info]
//    // insert stuff
//    public boolean isCurrentMarkerInserted() {
//        return isCurrentMarkerInserted;
//    }
//
//    public void setIsCurrentMarkerInserted(boolean isCurrentMarkerInserted) {
//        this.isCurrentMarkerInserted = isCurrentMarkerInserted;
//    }
//
//    // ??? -> store markerName instead of lat;lng
//    public LatLng getDeletedElement() {
//        return deletedElement;
//    }
//
//    public void setDeletedElement(LatLng deletedElement) {
//        this.deletedElement = deletedElement;
//    }
    // [END other_info]
}
