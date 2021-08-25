package com.example.googlemapnote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.googlemapnote.controllers.RetrofitClient;
import com.example.googlemapnote.models.user.User;
import com.example.googlemapnote.models.user.UserResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    private SignInButton signInButton;
    private static final int RC_SIGN_IN = 1;  // OPTION: 120
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // Set the dimensions of the sign-in button.
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        // register onCLick listener to sign in btn
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override  // ***
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });
    }


    // DON'T KNOW WHY this will coz bug
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        // Check for existing Google Sign In account, if the user is already signed in
//        // the GoogleSignInAccount will be non-null.
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        Log.d("onStart: ", account.toString());
//
////        if (account != null) {
////        }
//            updateUI(account);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);// Signed in successfully, show authenticated UI.
            handleLogin(account);
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("TAG", "signInResult:failed code=" + e.getStatusCode());

            Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
    }


    private void updateUI(GoogleSignInAccount account) {
        Log.d("TAG", "updateUI... ");
        startActivity(new Intent(LoginActivity.this, MapsActivity.class));
        finish();  // don't want come back to the main activity, just close the app
    }

    private void handleLogin(GoogleSignInAccount account) {
        User user = new User();
        user.setUsername(account.getDisplayName());
        user.setEmail(account.getEmail());
        user.setAccessToken("hardcode_token");
        Call<UserResponse> call = RetrofitClient.getInstance().getMyApi().login(user);

        call.enqueue(new retrofit2.Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                if(response.isSuccessful()) {
                    UserResponse resUser = response.body();
                    User resultUser = resUser.getUser();
                    GlobalClass.getInstance().setCurrentUser(resultUser);
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w("Login failed", "Failed");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.d("Login failed", t.toString());
                Toast.makeText(getApplicationContext(), "Error occur", Toast.LENGTH_LONG).show();  // ??? <- SOMETIME GOES IN HERE ???
            }
        });

    }
}