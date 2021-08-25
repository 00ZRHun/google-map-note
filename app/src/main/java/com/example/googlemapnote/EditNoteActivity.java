package com.example.googlemapnote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemapnote.controllers.RetrofitClient;
import com.example.googlemapnote.models.notes.Note;
import com.example.googlemapnote.models.notes.NoteResponse;
import com.example.googlemapnote.models.notes.UpdateNoteBody;
import com.example.googlemapnote.models.notes.UpdateNoteRequest;
import com.example.googlemapnote.models.tag.Tag;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import retrofit2.Call;

public class EditNoteActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);

        // Calling Application class (see application tag in AndroidManifest.xml)
        globalVariable = (GlobalClass) getApplicationContext();

        // [START get_data_from_MapsActivity_intent]
        Intent intent = getIntent();

        Note note = (Note) intent.getExtras().getSerializable(MapsActivity.EDIT_NOTE_INTENT);
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

    public void updateNote(View view) {
        String name = txtName.getText().toString();
        String[] tagStrArr = txtTags.getText().toString().split(",");
        String content = txtContent.getText().toString();
        int userId = GlobalClass.getInstance().getCurrentUser().getId();

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
}