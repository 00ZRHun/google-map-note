package com.example.googlemapnote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemapnote.MenusActivity;
import com.example.googlemapnote.R;
import com.example.googlemapnote.adapters.NoteListAdapter;
import com.example.googlemapnote.controllers.RetrofitClient;
import com.example.googlemapnote.models.notes.Note;
import com.example.googlemapnote.models.notes.NoteList;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class NotesActivity extends MenusActivity {

    private static final String TAG = "NotesActivity";
    private RecyclerView recyclerView;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        getNavigationView().setCheckedItem(R.id.nav_notes);  // remain selecting the menu item after user click it

        recyclerView = findViewById(R.id.note_list_recycle_view);
        emptyView = findViewById(R.id.empty_view);
        getNotes();
        // show notes list here...
    }

    private void getNotes() {
        int userId = GlobalClass.getInstance().getCurrentUser().getId();
        Call<NoteList> call = RetrofitClient.getInstance().getMyApi().getUserNotes(userId);

        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(NotesActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("Fetching notes");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // show it
        progressDialog.show();

        call.enqueue(new retrofit2.Callback<NoteList>() {

            @Override
            public void onResponse(Call<NoteList> call, Response<NoteList> response) {
                NoteList notelist = response.body();
                List<Note> notes = notelist.getNotes();
                Log.w("body", new Gson().toJson(notes));

                if(notelist.getNotes().size() == 0)
                    emptyView.setVisibility(View.VISIBLE);

                // add data into recyleview via adapter
                NoteListAdapter adapter = new NoteListAdapter(notelist);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(adapter);

                // dismiss progressbar
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<NoteList> call, Throwable t) {
                Log.d("err", t.toString());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error occur", Toast.LENGTH_LONG).show();  // ??? <- SOMETIME GOES IN HERE ???
            }
        });
    }
}