package com.example.googlemapnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

/**
 * This class backup (previous redundant version) purpose
 */

public class MainActivity_V1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    // [START profile_fragment]
    /*private ImageView profile_image;
    private TextView name;
    private TextView email;
    private TextView id;
    private Button sign_out_btn;

    private GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;*/
    // [END profile_fragment]

    /**
     *
     @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menus);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // LATER
        /*navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });*/

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        if (savedInstanceState == null) {  // in case of rerun this oncreate method when user rotate devise / do configuration?
            /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MapsFragment()).commit();  // don't show empty activity when user 1st time enter*/
            navigationView.setCheckedItem(R.id.nav_maps);  // select a menu option as default
        }


        // [START profile_fragment]
        /*// Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        profile_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        id = findViewById(R.id.id);
        sign_out_btn = findViewById(R.id.sign_out_btn);

        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_out_btn:
                        Log.d("TAG", "signOut00: ");
                        signOut();
                        break;
                    // ...
                }
//                signOut();

            }
        });*/
        // [END profile_fragment]
    }

    @Override
    public void onBackPressed() {
        // IF the drawer is open, THEN close the drawer 1st bfr close the activity as normal
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_maps:
                /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MapsFragment()).commit();*/
                break;
            case R.id.nav_notes:
                /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatFragment()).commit();*/
                break;
            case R.id.nav_profile:
                /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();*/
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
                //                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
        // return false;  // do not select the item even user click it
    }

        /*@Override
        public void onPointerCaptureChanged(boolean hasCapture) {

        }*/


    // [START profile_fragment]
    /*// [START main_menu]

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);  // pass menu as input
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout: {
                Log.d("TAG", "signOut00: ");
                signOut();
                return true;
            }
        }
//        return true;
        return super.onOptionsItemSelected(item);
    }

    // [END main_menu]

    @Override
    protected void onStart() {
        super.onStart();

        handleSignInResult();
    }



    private void signOut() {
        Log.d("TAG", "signOut01: ");
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("TAG", "signOut: ");
                        // ...
//                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));  // ???
                        finish();
                    }
                });
    }

    private void handleSignInResult() {
        //        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);  // *** -> ProfileActivity.this
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            name.setText(personName);
            email.setText(personEmail);
            id.setText(personId);
            Picasso.get().load(personPhoto).placeholder(R.mipmap.ic_launcher).into(profile_image);  // profile_image
        }
    }*/
    // [END profile_fragment]
}