package com.example.googlemapnote;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemapnote.controllers.RetrofitClient;
import com.example.googlemapnote.models.location.NewLocationRequest;
import com.example.googlemapnote.models.notes.NewNoteRequest;
import com.example.googlemapnote.models.notes.Note;
import com.example.googlemapnote.models.notes.NoteResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;

public class CreateNoteActivity extends AppCompatActivity {

    private double latitudeDouble;
    private double longitudeDouble;
    private String uniqueIdCombineLatLng;
    private LatLng currentLatLng;

    private EditText txtName;
    private EditText txtTags;
    private EditText txtContent;

    private FloatingActionButton fabOCR;
    private ExtendedFloatingActionButton fabParentSetting;
    private TextView ocrActionText;

    private Button btnAddNote;

    Boolean isFabsVisible;

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
        uniqueIdCombineLatLng = getDataFromMapsActivityIntent.getStringExtra(MapsActivity.UNIQUE_ID_COMBINE_LAT_LNG);

        // separate latitudeDouble and longitudeDouble from UNIQUE_LAT_LNG
        String[] latLngArr = uniqueIdCombineLatLng.split(";");
        String latitudeStr = latLngArr[0];
        String longitudeStr = latLngArr[1];
        latitudeDouble = Double.parseDouble(latitudeStr);
        longitudeDouble = Double.parseDouble(longitudeStr);
        currentLatLng = new LatLng(latitudeDouble, longitudeDouble);

        /* [Start UI initialize] */

        // text field
        txtName = findViewById(R.id.edit_txt_name);
        txtTags = findViewById(R.id.edit_txt_tags);
        txtContent = findViewById(R.id.edit_txt_content);

        // fab
        ocrActionText = findViewById(R.id.ocr_action_text);
        fabOCR = findViewById(R.id.ocr_fab);
        fabParentSetting = findViewById(R.id.setting_parent_fab);

        // btn
        btnAddNote = findViewById(R.id.buttonAddNote);

        /* [END UI initialize] */

        /* [Start UI Setting] */
        txtName.setFocusableInTouchMode(true);
        txtTags.setFocusableInTouchMode(true);
        txtContent.setFocusableInTouchMode(true);

        fabOCR.setVisibility(View.GONE);
        ocrActionText.setVisibility(View.GONE);
        isFabsVisible = false;
        fabParentSetting.shrink();

        /* [END UI Setting] */

        /* [START Event Listeners] */
        fabParentSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFabsVisible) {
                    // when isAllFabsVisible becomes
                    // true make all the action name
                    // texts and FABs VISIBLE.
                    fabOCR.show();
                    ocrActionText
                            .setVisibility(View.VISIBLE);
                    // Now extend the parent FAB, as
                    // user clicks on the shrinked
                    // parent FAB
                    fabParentSetting.extend();
                    // make the boolean variable true as
                    // we have set the sub FABs
                    // visibility to GONE
                    isFabsVisible = true;
                } else {
                    // when isAllFabsVisible becomes
                    // true make all the action name
                    // texts and FABs GONE.
                    fabOCR.hide();
                    ocrActionText
                            .setVisibility(View.GONE);
                    // Set the FAB to shrink after user
                    // closes all the sub FABs
                    fabParentSetting.shrink();
                    // make the boolean variable false
                    // as we have set the sub FABs
                    // visibility to GONE
                    isFabsVisible = false;
                }
            }
        });
        /* [END Event Listeners] */

//
        // textViewLatLng
//        TextView textViewLatLng = (TextView) findViewById(R.id.textViewLatLng);
//        textViewLatLng.setText("latitude: " +latitudeDouble+ "; longitude: " +longitudeDouble);
        // [END get_data_from_MapsActivity_intent] */
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int[] sourceCoordinates = new int[2];
            v.getLocationOnScreen(sourceCoordinates);
            float x = ev.getRawX() + v.getLeft() - sourceCoordinates[0];
            float y = ev.getRawY() + v.getTop() - sourceCoordinates[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this);
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideKeyboard(Activity activity) {
        txtContent.clearFocus();
        txtName.clearFocus();
        txtTags.clearFocus();

        if (activity != null && activity.getWindow() != null) {
            activity.getWindow().getDecorView();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }

    public void addNewNote(View view) {
        String name = txtName.getText().toString();
        String[] tagStrArr = txtTags.getText().toString().split(",");
        String content = txtContent.getText().toString();
        int userId = GlobalClass.getInstance().getCurrentUser().getId();

        NewLocationRequest newLocationRequest = new NewLocationRequest(
                name,
                uniqueIdCombineLatLng,
                latitudeDouble,
                longitudeDouble
        );

        NewNoteRequest newNoteRequest = new NewNoteRequest(
                name,
                content,
                userId,
                tagStrArr,
                newLocationRequest
        );

        Log.d("new note request:", new Gson().toJson(newNoteRequest));

        Call<NoteResponse> call = RetrofitClient.getInstance().getMyApi().addNote(newNoteRequest);

        call.enqueue(new retrofit2.Callback<NoteResponse>() {
            @Override
            public void onResponse(Call<NoteResponse> call, retrofit2.Response<NoteResponse> response) {
                if(response.isSuccessful()) {
                    NoteResponse resUser = response.body();
                    Note note = resUser.getNote();
                    Log.w("Note add success", new Gson().toJson(note));
                    Toast.makeText(getApplicationContext(), "Added Successful", Toast.LENGTH_SHORT).show();

                    MapsActivity.updateMarker(note);
                } else {
                    Log.w("Note add failed", "Failed");
                    Toast.makeText(getApplicationContext(), "Failed to add note", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NoteResponse> call, Throwable t) {
                Log.d("Note add failed", t.toString());
                Toast.makeText(getApplicationContext(), "Failed to add note", Toast.LENGTH_LONG).show();  // ??? <- SOMETIME GOES IN HERE ???
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void cancelCreateNote(View view) {
        super.finish();
    }
}