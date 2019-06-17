package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Notifications.GehaMessagingService;
import com.example.ofir.social_geha.R;
import com.google.firebase.iid.FirebaseInstanceId;

public class SplashActivity extends AppCompatActivity {
    //=====================================================
    //              CLASS VARIABLES
    //=====================================================
    private static final int LOGIN_RETURN_CODE = 1;

    //=====================================================
    //              FUNCTIONS
    //=====================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo_splash);
        ProgressBar progressBar = findViewById(R.id.splash_pb);

        Animation logo_animation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        logo.startAnimation(logo_animation);
        progressBar.startAnimation(logo_animation);

        // If the user is not logged in move to loginScreen.
        if (Database.getInstance().noLoggedInUser()) {
            promptLogin();
        }
        else{ // else move to home screen.
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            GehaMessagingService.storeToken(Database.getInstance().getAuth(), Database.getInstance().getdb(), task.getResult().getToken());
                        }
                    });
            int splashTimeOut = 3000;
            new Handler().postDelayed(() -> moveToHomeScreen(), splashTimeOut);
        }
    }

    //=====================================================
    // Called if the user is not logged in.
    // This function moves to login screen and waits for LOGIN_RETURN_CODE.
    //=====================================================
    public void promptLogin() {
        Intent intent = new Intent(SplashActivity.this, Login.class);
        startActivityForResult(intent, LOGIN_RETURN_CODE);
    }

    //=====================================================
    // Upon result from login activity, if got LOGIN_RETURN_CODE
    // then move to main_drawer_activity (successful login). Else, retry.
    //=====================================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_RETURN_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getString(R.string.strings_succ_login), Toast.LENGTH_SHORT).show();
                moveToHomeScreen();
            } else {
                promptLogin();
            }
        }
    }

    //=====================================================
    // This function starts intent to activity_main_drawer.
    //=====================================================
    private void moveToHomeScreen(){
        startActivity(new Intent(SplashActivity.this, activity_main_drawer.class));
        finish();
    }
}
