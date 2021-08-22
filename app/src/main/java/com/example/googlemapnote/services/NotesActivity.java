package com.example.googlemapnote.services;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.googlemapnote.MenusActivity;
import com.example.googlemapnote.R;

public class NotesActivity extends MenusActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        getNavigationView().setCheckedItem(R.id.nav_notes);  // remain selecting the menu item after user click it

        // show notes list here...
    }
}