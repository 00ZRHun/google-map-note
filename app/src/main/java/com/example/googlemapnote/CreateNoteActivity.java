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
        // [END get_data_from_MapsActivity_intent] */
    }
}