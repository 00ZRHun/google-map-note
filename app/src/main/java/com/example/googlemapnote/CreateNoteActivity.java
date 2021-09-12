package com.example.googlemapnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class CreateNoteActivity extends AppCompatActivity {
    private static final String TAG = "CreateNoteActivity";
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

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double mAccel;
    private double mAccelCurrent;
    private double mAccelLast;
    private int count = 0;
    private boolean isAlertShowed = false;

    private TextRecognizer recognizer;

    int SELECT_PICTURE = 200;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            mAccelCurrent = Math.sqrt((x*x + y*y + z*z));
            mAccel = Math.abs(mAccelCurrent - mAccelLast);
            mAccelLast = mAccelCurrent;

            if(mAccel > 2) {
                count++;

                if(count == 2) {
                    shakeDetect();
//                    Toast.makeText(getContext(), "Shake detected.", Toast.LENGTH_SHORT).show();
                    count = 0;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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
        fabOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
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
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

    private void shakeDetect() {
        if(isAlertShowed) return;

        isAlertShowed = true;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Delete all text?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        txtContent.getText().clear();
                        isAlertShowed = false;
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        isAlertShowed = false;
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
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
        String[] tagStrArr = null;

        if(txtTags.getText().toString().split(",")[0] == "")
            tagStrArr = new String[0];
        else
            tagStrArr = txtTags.getText().toString().split(",");

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

    // this function is triggered when
    // the Select Image Button is clicked
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Result code" + resultCode);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
//                    IVPreviewImage.setImageURI(selectedImageUri);
                    recognizer = TextRecognition.getClient();
                    Bitmap bitmap = null;

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    InputImage image = InputImage.fromBitmap(bitmap, 0);

                    recognizer
                            .process(image)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(@NonNull Text texts) {
                                    processTextRecognitionResult(texts);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                }
            }
        }
    }

    private void processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        String s = "";
        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    s += elements.get(k).getText() + " ";
                }
            }
        }
        String prev = txtContent.getText().toString();
        txtContent.setText(prev + s);
    }
}