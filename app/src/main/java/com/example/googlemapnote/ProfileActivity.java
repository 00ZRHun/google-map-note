package com.example.googlemapnote;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProfileActivity extends MenusActivity {

    // var for UI components
    private ImageView profile_image;
    private TextView name;
    private TextView givenName;
    private TextView familyName;
    private TextView email;
    private TextView id;
    private Button sign_out_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getNavigationView().setCheckedItem(R.id.nav_profile);  // remain selecting the menu item after user click it

        // get UI components
        profile_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);
        givenName = findViewById(R.id.given_name);
        familyName = findViewById(R.id.family_name);
        email = findViewById(R.id.email);
        id = findViewById(R.id.id);
        sign_out_btn = findViewById(R.id.sign_out_btn);

        // register onclick listener for the logout btn
        sign_out_btn.setOnClickListener(this::onClick);  // ***
    }

    @Override
    protected void onStart() {
        super.onStart();

        //handleSignInResult();  // put in the parent class (MainActivity)
        name.setText("Name: " + super.getUserName());
        givenName.setText("Given Name: " + super.getUserGivenName());
        familyName.setText("Family Name: " + super.getUserFamilyName());
        email.setText("Email: " + super.getUserEmail());
        id.setText("Id: " + super.getUserId());
        Picasso.get().load(super.getUserPhoto()).placeholder(R.mipmap.ic_launcher).into(profile_image);  // profile_image
    }
}