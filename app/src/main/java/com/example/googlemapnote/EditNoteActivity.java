package com.example.googlemapnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemapnote.adapters.NoteListAdapter;
import com.example.googlemapnote.controllers.RetrofitClient;
import com.example.googlemapnote.models.notes.DeleteNoteBody;
import com.example.googlemapnote.models.notes.DeleteNoteResponse;
import com.example.googlemapnote.models.notes.Note;
import com.example.googlemapnote.models.notes.NoteResponse;
import com.example.googlemapnote.models.notes.UpdateNoteBody;
import com.example.googlemapnote.models.notes.UpdateNoteRequest;
import com.example.googlemapnote.models.tag.Tag;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import retrofit2.Call;

public class EditNoteActivity extends AppCompatActivity {

    private static final String TAG = "EditNoteActivity";

    private Boolean fromMaps = false;
    private int noteId;

    private EditText txtName;
    private EditText txtTags;
    private EditText txtContent;

    private FloatingActionButton fabOCR;
    private ExtendedFloatingActionButton fabParentSetting;
    private TextView ocrActionText;

    private Button btnEditNote;

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
        setContentView(R.layout.activity_update_note);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Calling Application class (see application tag in AndroidManifest.xml)
        globalVariable = (GlobalClass) getApplicationContext();

        // [START get_data_from_MapsActivity_intent]
        Intent intent = getIntent();

        Note note = null;

        if(intent.getExtras().getSerializable(MapsActivity.EDIT_NOTE_INTENT) != null) {
            note = (Note) intent.getExtras().getSerializable(MapsActivity.EDIT_NOTE_INTENT);
            fromMaps = true;
        }
        else if(intent.getExtras().getSerializable(NoteListAdapter.EDIT_NOTE_INTENT) != null) {
            // from listview
            note = (Note) intent.getExtras().getSerializable(NoteListAdapter.EDIT_NOTE_INTENT);
        }

        noteId = note.getId();
        List<Tag> tagList = note.getTagList();

        /* [Start UI initialize] */
        // text field
        txtName = findViewById(R.id.edit_txt_name);
        txtName.setText(note.getTitle());

        txtTags = findViewById(R.id.edit_txt_tags);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            String tags = tagList
                    .stream()
                    .map(t -> String.valueOf(t.getTag()))
                    .collect(Collectors.joining(", "));
            txtTags.setText(tags);
        } else {
            StringBuilder str = new StringBuilder("");

            // Traversing the ArrayList
            for (Tag t : tagList) {

                // Each element in ArrayList is appended
                // followed by comma
                str.append(t.getTag()).append(",");
            }

            // StringBuffer to String conversion
            String commaseparatedlist = str.toString();

            // By following condition you can remove the last
            // comma
            if (commaseparatedlist.length() > 0)
                commaseparatedlist
                        = commaseparatedlist.substring(
                        0, commaseparatedlist.length() - 1);

            txtTags.setText(commaseparatedlist);
        }

        txtContent = findViewById(R.id.edit_txt_content);
        txtContent.setText(note.getDescription());

        // fab
        ocrActionText = findViewById(R.id.ocr_action_text);
        fabOCR = findViewById(R.id.ocr_fab);
        fabParentSetting = findViewById(R.id.setting_parent_fab);

        // btn
        btnEditNote = findViewById(R.id.btn_edit);

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

    private void shakeDetect() {
        if(isAlertShowed == true) return;
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

    public void updateNote(View view) {
        String name = txtName.getText().toString();
        String[] tagStrArr = null;

        if(txtTags.getText().toString().split(",")[0] == "")
            tagStrArr = new String[0];
        else
            tagStrArr = txtTags.getText().toString().split(",");

        String content = txtContent.getText().toString();
        int userId = GlobalClass.getInstance().getCurrentUser().getId();

        Log.d(TAG,"Content"+tagStrArr.length);

        UpdateNoteBody updatedNote = new UpdateNoteBody();
        updatedNote.setTitle(name);
        updatedNote.setDescription(content);

        UpdateNoteRequest updateNoteRequest = new UpdateNoteRequest(
                updatedNote,
                userId,
                tagStrArr
        );
        Log.d("update note request:", new Gson().toJson(updateNoteRequest));

        Call<NoteResponse> call = RetrofitClient.getInstance().getMyApi().updateNote(noteId, updateNoteRequest);

        call.enqueue(new retrofit2.Callback<NoteResponse>() {
            @Override
            public void onResponse(Call<NoteResponse> call, retrofit2.Response<NoteResponse> response) {
                if(response.isSuccessful()) {
                    NoteResponse resUser = response.body();
                    Note note = resUser.getNote();
                    Log.w("UpdateNote", new Gson().toJson(note));
                    Toast.makeText(getApplicationContext(), "Edited Successful", Toast.LENGTH_SHORT).show();

                    if(fromMaps)
                        MapsActivity.updateMarker(note);

                } else {
                    Log.w("Note update failed", "Failed");
                    Toast.makeText(getApplicationContext(), "Failed to edit note", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NoteResponse> call, Throwable t) {
                Log.d("Note update failed", t.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to edit note", Toast.LENGTH_LONG).show();  // ??? <- SOMETIME GOES IN HERE ???
            }
        });
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

    public void confirmNoteDelete(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Delete this note?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteNote();
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

    public void
    deleteNote() {
        int userId = GlobalClass.getInstance().getCurrentUser().getId();

        DeleteNoteBody deleteNote = new DeleteNoteBody(userId);

        try {
            Call<DeleteNoteResponse> call = RetrofitClient.getInstance().getMyApi().deleteNote(noteId, deleteNote);
            call.enqueue(new retrofit2.Callback<DeleteNoteResponse>() {
                @Override
                public void onResponse(Call<DeleteNoteResponse> call, retrofit2.Response<DeleteNoteResponse> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Deleted Successful", Toast.LENGTH_SHORT).show();

                        if(fromMaps)
                            MapsActivity.deleteMarker();
                    } else {
                        Log.w("Note update failed", "Failed");
                        Toast.makeText(getApplicationContext(), "Failed to edit note", Toast.LENGTH_SHORT).show();
                    }
                    leave();
                }

                @Override
                public void onFailure(Call<DeleteNoteResponse> call, Throwable t) {
                    Log.d("Note delete failed", t.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed to edit note", Toast.LENGTH_LONG).show();  // ??? <- SOMETIME GOES IN HERE ???
                }
            });
        }catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    private void leave() {
        super.finish();
    }
}