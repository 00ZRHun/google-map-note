package com.example.googlemapnote;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.googlemapnote.controllers.RetrofitClient;  // API
import com.example.googlemapnote.models.notes.NoteList;  // API
import com.example.googlemapnote.models.notes.Note;  // API
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;  // API

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;  // API
import retrofit2.Response;  // API

/*public class MapsActivity extends AppCompatActivity
//public class MapsActivity extends Fragment
        implements OnMapReadyCallback, Serializable {*/
public class MapsActivity extends MenusActivity
//public class MapsActivity extends Fragment
        implements OnMapReadyCallback, Serializable {

    public static final String EDIT_NOTE_INTENT = "com.example.googlemapnote.EDIT";
    public static final String EDIT_MARKER_POINT_INTENT = "com.example.googlemapnote.EDIT_MARKER_POINT";

    public static final String UNIQUE_ID_COMBINE_LAT_LNG = "com.example.googlemapnote.mapsactivity.UNIQUE_ID_COMBINE_LAT_LNG";
    private static final String TAG = "MapsActivity";   // for debug purpose

    private Intent createNoteIntent;

    private double selectedLat;
    private double selectedLng;
    private double currentLat;
    private double currentLng;
    private boolean hasUserSelectedMarker = false;
    private LatLng deletedElement;
    private boolean doubleBackToExitPressedOnce = false;
    private String toastText;   // ??? -> DEBUG
    private String latLngValue;

    private int userClickToAddMarkerTimes = 0;
    static Marker markerName;
    static Marker selectedMarker;

    private GlobalClass globalVariable;

    private BroadcastReceiver receiver;

    static GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;

    /**
     * retrieve all_existed_markers from database ***
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // onNavigationItemSelected(navigationView.getMenu().getItem(0));  // DON'T KNOW WHY NOT WORK -> will coz this current activity will never be entered
        /*navigationView.getMenu().getItem(0).setChecked(true);*/  // *** -> TEST LATER
        getNavigationView().setCheckedItem(R.id.nav_maps);  // remain selecting the menu item after user click it

        // Calling Application class (see application tag in AndroidManifest.xml)
        globalVariable = (GlobalClass) getApplicationContext();

        // ORI text: Map Location Activity
        /*getSupportActionBar().setTitle("Create Note");  // PREV: GeoTag Note*/

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        createNoteIntent = new Intent(MapsActivity.this, CreateNoteActivity.class);

    }


    private void getNotes() {
        Call<NoteList> call = RetrofitClient.getInstance().getMyApi().getNotes();

        call.enqueue(new retrofit2.Callback<NoteList>() {

            @Override
            public void onResponse(Call<NoteList> call, Response<NoteList> response) {
                NoteList notelist = response.body();
                List<Note> notes = notelist.getNotes();
                Log.w("body", new Gson().toJson(notes));

                for (int i = 0; i < notes.size(); i++) {
                    Note note = notes.get(i);

                    // weird name due to clash name with Java built-in location package.
                    List<com.example.googlemapnote.models.location.Location> list = note.getLocationList();

                    Log.w("geolist", new Gson().toJson(list));
                    LatLng marker = list.get(0).getLatLng(); // always get first lng and lat
                    drawMarker(marker, note);
                }
            }

            @Override
            public void onFailure(Call<NoteList> call, Throwable t) {
                Log.d("err", t.toString());
                Toast.makeText(getApplicationContext(), "Error occur", Toast.LENGTH_LONG).show();  // ??? <- SOMETIME GOES IN HERE ???
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private static void drawMarker(LatLng point, Note note) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point).title(note.getTitle());

        // Adding marker on the Google Map
        mGoogleMap.addMarker(markerOptions).setTag(note);
    }

    public static void updateMarker(Note note) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(selectedMarker.getPosition()).title(note.getTitle());

        Log.w("xxxx", new Gson().toJson(note));

        if(selectedMarker != null)
            selectedMarker.remove();

        drawMarker(selectedMarker.getPosition(), note);
    }

    public static void deleteMarker() {
        selectedMarker.remove();
//        Marker markerName = mGoogleMap.addMarker(new MarkerOptions().position(deletedElement).title("Title"));
//        markerName.remove();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // get the deleted element if any
        //if(globalVariable.getDeletedElement().latitude != 0) {
        /*if(globalVariable.getDeletedElement() != null) {
            deletedElement = globalVariable.getDeletedElement();

            //markerName = mGoogleMap.addMarker(markerOptions);
            Marker markerName = mGoogleMap.addMarker(new MarkerOptions().position(deletedElement).title("Title"));
            markerName.remove();
        }*/

        // [START update_current_location_every_two_minutes]
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
        // [END update_current_location_every_two_minutes]

        // [START add_all_existed_marker]
        this.getNotes();

//            // VS mGoogleMap
//            googleMap
        // [END add_all_existed_marker]

        // [START click_exited_marker_to_enter_into_note]
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (doubleBackToExitPressedOnce) {
                    if(marker.getTag() != null) {
                        Log.d("MarkerTag", new Gson().toJson(marker.getTag()));
                        selectedMarker = marker;
                        Note note = (Note) marker.getTag();
                        Intent editNoteIntent = new Intent(getApplicationContext(), EditNoteActivity.class);
                        editNoteIntent.putExtra(EDIT_NOTE_INTENT, note);

                        startActivity(editNoteIntent);
                    }
//                    createNoteIntent.putExtra(UNIQUE_ID_COMBINE_LAT_LNG, marker.getPosition().latitude + ";" + marker.getPosition().longitude);
//                    startActivity(createNoteIntent);
                } else {
                    doubleBackToExitPressedOnce = true;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }
                //return true;
                return false;
            }
        });
        // [END click_exited_marker_to_enter_into_note]

        // [START setting_a_click_event_handler_for_the_map]
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Remove old marker
                if (hasUserSelectedMarker) {
                    markerName.remove();
                }

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);
                markerOptions.draggable(true);   // ???

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // Animating to the touched position
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                markerName = mGoogleMap.addMarker(markerOptions);

                // Update latitude and longitude TEMP value
                selectedLat = latLng.latitude;
                selectedLng = latLng.longitude;

                selectedMarker = markerName;

                // set user have selected one marker on Google Map
                hasUserSelectedMarker = true;
            }
        });
        // [END setting_a_click_event_handler_for_the_map]
    }

    // [START get_current_location]
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

                // Display traffic.
                mGoogleMap.setTrafficEnabled(true);

                // Sets the map type to be "hybrid"
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                // Update latitude and longitude TEMP value
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
            }
        }
    };
    // [END get_current_location]

    // [START check_location_permission]
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }
    // [END check_location_permission]

    // [START request_permission_result]
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    // [END request_permission_result]

    // [START to_drop_ONLY_ONE_pin]

    /**
     * Called when the taps the + btn
     */
    public void startCreateNoteActivity(View view) {
        // show alert
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        if (hasUserSelectedMarker) {
            alertDialogBuilder.setMessage("Which location you prefer to create note?");
            alertDialogBuilder.setPositiveButton("Current Location", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                            /*//You clicked yes button
                            Toast.makeText(MapsActivity.this,"Create note at current location!!!",Toast.LENGTH_LONG).show();

                            // start new activity by using intent
                            //Intent createNoteIntent = new Intent(MapsActivity.this, CreateNoteActivity.class);
                            createNoteIntent.putExtra(UNIQUE_ID_COMBINE_LAT_LNG, selectedLat+";"+selectedLng);
                            createNoteIntent.putExtra(UNIQUE_ID_COMBINE_LAT_LNG, currentLat+";"+currentLng);
                            startActivity(createNoteIntent);*/

                    toastText = "Create note at current location!!!";
                    latLngValue = currentLat + ";" + currentLng;
                    Toast.makeText(MapsActivity.this, toastText, Toast.LENGTH_LONG).show();

                    // start new activity by using intent
                    Intent createNoteIntent = new Intent(MapsActivity.this, CreateNoteActivity.class);
                    // ??? <<<=== ???
                    createNoteIntent.putExtra(UNIQUE_ID_COMBINE_LAT_LNG, selectedLat + ";" + selectedLng);
                    //createNoteIntent.putExtra(UNIQUE_ID_COMBINE_LAT_LNG, latLngValue);

                    //createNoteIntent.putExtra(GOOGLE_MAP, (Serializable) mGoogleMap);
                    /*Bundle gm = new Bundle(mGoogleMap);*/
                    startActivity(createNoteIntent);
                }
            });

            alertDialogBuilder.setNegativeButton("Selected Location", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    toastText = "Create note at selected location!!!";
                    latLngValue = selectedLat + ";" + selectedLng;
                    Toast.makeText(MapsActivity.this, toastText, Toast.LENGTH_LONG).show();

                    // start new activity by using intent
                    Intent createNoteIntent = new Intent(MapsActivity.this, CreateNoteActivity.class);
                    createNoteIntent.putExtra(UNIQUE_ID_COMBINE_LAT_LNG, selectedLat + ";" + selectedLng);
                    //createNoteIntent.putExtra(UNIQUE_ID_COMBINE_LAT_LNG, latLngValue);
                    //createNoteIntent.putExtra(ALL_EXISTED_MARKERS, allExistedMarkers);

                    /*// clear the temp storage to holder the boolean value if user selected
                    hasUserSelectedMarker = false;*/

                    // clear the temp storage to holder the boolean value if user selected
                    //hasUserSelectedMarker = false;

                    // start create note activity
                    startActivity(createNoteIntent);
                }
            });

            // DON'T KNOW WHY NOT WORK
            /*// Both of the if-else case run below code block
            Toast.makeText(MapsActivity.this, toastText, Toast.LENGTH_LONG).show();

            // start new activity by using intent
            //Intent createNoteIntent = new Intent(MapsActivity.this, CreateNoteActivity.class);
            //createNoteIntent.putExtra(UNIQUE_ID_COMBINE_LAT_LNG, selectedLat+";"+selectedLng);
            createNoteIntent.putExtra(UNIQUE_ID_COMBINE_LAT_LNG, latLngValue);
            startActivity(createNoteIntent);*/

        } else {
            alertDialogBuilder.setMessage("Do you want to create note at your current location?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //You clicked yes button
                            Toast.makeText(MapsActivity.this, "Click note here!!!", Toast.LENGTH_LONG).show();

                            // start new activity by using intent
                            createNoteIntent.putExtra(UNIQUE_ID_COMBINE_LAT_LNG, currentLat + ";" + currentLng);
                            startActivity(createNoteIntent);
                        }
                    });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MapsActivity.this, "Select preference location here!!!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    // [END to_drop_ONLY_ONE_pin]


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void refreshMarker(View view) {
        getNotes();
    }
}
