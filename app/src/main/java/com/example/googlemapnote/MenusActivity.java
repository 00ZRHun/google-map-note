
package com.example.googlemapnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

public class MenusActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * Menus -> slide menu (navigation drawer) & option menu (contextual menus)
     * <p>
     * This activity always re-call when start another activity,
     * so we put the setCheckedItem() in corresponding activity (in the onCreate())
     * <p>
     * cannot override onCreate() -> DON'T KNOW WHY?
     * or else ERROR: GeoNotes keeps stopping
     */

    private DrawerLayout drawerLayout;
    private DrawerLayout drawer;
    private FrameLayout frameLayout;
    private NavigationView navigationView;

    // [START get_user_info_from_sign_in_with_Google]
    // var for Google sign in
    private GoogleSignInClient mGoogleSignInClient;  // login (get client list) & logout

    // var for UI components
    private ImageView profile_image;
    private TextView name;

    // var for user acc info
    private String userName;
    private String userGivenName;
    private String userFamilyName;
    private String userEmail;
    private String userId;
    private Uri userPhoto;
    // [END get_user_info_from_sign_in_with_Google]

    @Override
    public void setContentView(int layoutResID) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_menus, null);
        frameLayout = (FrameLayout) drawerLayout.findViewById(R.id.fragment_container);

        getLayoutInflater().inflate(layoutResID, frameLayout, true);

        //super.setContentView(layoutResID);  // DEFAULT GIVEN
        super.setContentView(drawerLayout);

        // Your drawer content...
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // show app name on the toolbar -> android:label="@string/app_name"

        drawer = findViewById(R.id.drawer_layout);
        // navigationView = (NavigationView) findViewById(R.id.nav_view);  // ALT
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MenusActivity.this);  // register listener (handle user click action)

        // put the setCheckedItem() in corresponding activity (in the onCreate())
        /*if (savedInstanceState == null) {  // in case of rerun this oncreate method when user rotate devise / do configuration?
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MapsFragment()).commit();  // don't show empty activity when user 1st time enter
            navigationView.setCheckedItem(R.id.nav_maps);  // select a menu option as default
        }*/

        // DON'T KNOW WHY DON'T WORK -> put the setCheckedItem() in corresponding activity (in the onCreate())
        /*navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });*/

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();


        // [START get_user_info_from_sign_in_with_Google]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        View header = navigationView.getHeaderView(0);
        // name = findViewById(R.id.username);  // NOT WORK, refer to below
        name = header.findViewById(R.id.navigation_header_name);
        profile_image = header.findViewById(R.id.navigation_header_profile_image);

        handleSignInResult();
        // [END get_user_info_from_sign_in_with_Google]
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
                /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,  // use activity instead of fragment  // open fragment
                        new ChatFragment()).commit();*/
                startActivity(new Intent(MenusActivity.this, MapsActivity.class));
                break;
            case R.id.nav_notes:
                startActivity(new Intent(MenusActivity.this, NotesActivity.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(MenusActivity.this, ProfileActivity.class));
                break;
//            case R.id.nav_share:
//                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_send:
//                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
//                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        // return false;  // DEFAULT GIVEN: do not select the item even user click it
        return true;
    }

    // [START get_user_info_from_sign_in_with_Google]
    private void handleSignInResult() {
        // GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());       // DON'T KNOW WHY NO WORK
        // GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this.getActivity());  // DON'T KNOW WHY NO WORK
        // MenusActivity.this.getActivity()  // DON'T KNOW WHY NO WORK
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MenusActivity.this);  // ALT
        if (acct != null) {
            userName = acct.getDisplayName();
            userGivenName = acct.getGivenName();
            userFamilyName = acct.getFamilyName();
            userEmail = acct.getEmail();
            userId = acct.getId();
            userPhoto = acct.getPhotoUrl();

            // cannot set value at here (MAYBE becoz called every time), so we pass the sub class
            name.setText(userName);
            Picasso.get().load(userPhoto).placeholder(R.mipmap.ic_launcher).into(profile_image);  // profile_image
        }
    }
    // [END get_user_info_from_sign_in_with_Google]


    // [START getter_method_for_user_acc_info]
    public String getUserName() {
        return userName;
    }

    public String getUserGivenName() {
        return userGivenName;
    }

    public String getUserFamilyName() {
        return userFamilyName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public Uri getUserPhoto() {
        return userPhoto;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    // [END getter_method_for_user_acc_info]


    // [START main_menu]
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // MenuInflater inflater = getSupportMenuInflater();  // deprecated
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);  // pass menu as input

        // [START change_text_color]
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            SpannableString spannable = new SpannableString(
                    menu.getItem(i).getTitle().toString()
            );
            /*spannable.setSpan(new ForegroundColorSpan(Color.RED),*/
            /*text.setSpan(new ForegroundColorSpan(Color.WHITE),
                    0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
            spannable.setSpan(new ForegroundColorSpan(Color.BLACK),
                    0, spannable.length(), 0);

            menuItem.setTitle(spannable);
        }
        // [END change_text_color]

        // return super.onCreateOptionsMenu(menu);  // DEFAULT GIVEN
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                signOut();
                break;
        }
        //return true;
        return super.onOptionsItemSelected(item);
    }


    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MenusActivity.this, "Successful Logout", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MenusActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }


    // [END main_menu]

    // [START handle_click_on_logout_btn]
    // METHOD 1 - register click listener for sign out btn
        /*sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_out_btn:
                        signOut();
                        Toast.makeText(ProfileActivity.this, "Successful Logout", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });*/

    // METHOD 2 - register click listener for sign out btn
    /*sign_out_btn.setOnClickListener(this::onClick);
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_out_btn:
                signOut();
                Toast.makeText(ProfileActivity.this, "Successful Logout", Toast.LENGTH_SHORT).show();
                break;
        }
    }*/

    /**
     * === accessor modifier / access privilege ====
     * - weaken / strengthen
     * narrow down scope:
     * 1. different package non-subclass
     * 2. different package subclass
     * 3. same package non-subclass
     * 4. same package subclass
     * 5. same class
     * package == folder
     * <p>
     * public       -> (ALL)                                    => same class, same package (subclass & non-subclass), different package (folder) (subclass & non-subclass)
     * protected    -> EXCEPT different package non-subclass    => same class, same package (subclass & non-subclass), different package (folder) (subclass)
     * default      -> EXCEPT different package                 => same class, same package (subclass & non-subclass)
     * private      -> (ONLY same class)                        => same class
     */
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_out_btn:
                signOut();
                break;
        }
    }
    // [END handle_click_on_logout_btn]
}

