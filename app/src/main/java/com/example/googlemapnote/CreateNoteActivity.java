package com.example.googlemapnote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class CreateNoteActivity extends AppCompatActivity {

    private double latitudeDouble;
    private double longitudeDouble;
    private LatLng currentLatLng;

    private ArrayList <LatLng> allExistedMarkers;

    private GlobalClass globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        // Calling Application class (see application tag in AndroidManifest.xml)
        globalVariable = (GlobalClass) getApplicationContext();

        // [START get_data_from_MapsActivity_intent]
        Intent getDataFromMapsActivityIntent = getIntent();

        // get data of UNIQUE_LAT_LNG and GOOGLE_MAP
        String uniqueIdCombineLatLng = getDataFromMapsActivityIntent.getStringExtra(MapsActivity.UNIQUE_ID_COMBINE_LAT_LNG);

        // separate latitudeDouble and longitudeDouble from UNIQUE_LAT_LNG
        String[] latLngArr = uniqueIdCombineLatLng.split(";");
        String latitudeStr = latLngArr[0];
        String longitudeStr = latLngArr[1];
        latitudeDouble = Double.parseDouble(latitudeStr);
        longitudeDouble = Double.parseDouble(longitudeStr);
        currentLatLng = new LatLng(latitudeDouble, longitudeDouble);

        // textViewLatLng
        TextView textViewLatLng = (TextView) findViewById(R.id.textViewLatLng);
        textViewLatLng.setText("latitude: " +latitudeDouble+ "; longitude: " +longitudeDouble);
        //textViewLatLng.setText("Name : "+name+" "+"Email : "+email+" ");
        // [END get_data_from_MapsActivity_intent] */
    }



    /**
     * [START onclick_functions_for_three_buttons_in_note]
     */
    // [START button_add_note]
    public void addNote(View view) {
        // Add marker on google map
        addMarker(currentLatLng);

        // Save note
        /**
         *
         */

        // Return to the previous Maps Activity
        finish();
    }
    // [END button_add_note]

    // [START button_cancel]
    public void cancelCreateNote(View view) {
        // cancel create note
        cancelCreateNote();

        // Return to the previous Maps Activity
        finish();
    }
    // [END button_cancel]

    // [START button_delete_whole_note_list]
    public void deleteWholeNoteList(View view) {
        // delete current marker on google map
        deleteMarker(currentLatLng);

        // Return to the previous Maps Activity
        finish();
    }
    // [END button_delete_whole_note_list]
    /**
     * [END onclick_functions_for_three_buttons_in_note]
     */



    /**
     * [START crud_for_marker]
     */
    // [START add_marker_on_google_map]
    public void addMarker(LatLng targetAddLatLng) {
        globalVariable.addMarker(targetAddLatLng);
    }
    // [END add_marker_on_google_map]

    // [START cancel_create_note_and_return_to_prev_google_map]
    public void cancelCreateNote() {
        globalVariable.setIsCurrentMarkerInserted(false);
    }
    // [END cancel_create_note_and_return_to_prev_google_map]

    // [START delete_marker_on_google_map]
    public void deleteMarker(LatLng targetDeleteLatLng) {
        globalVariable.deleteMarker(targetDeleteLatLng);
    }
    // [END delete_marker_on_google_map]
    /**
     * [END crud_for_marker]
     */
}