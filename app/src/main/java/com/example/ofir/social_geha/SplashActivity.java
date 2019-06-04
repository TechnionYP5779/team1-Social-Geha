package com.example.ofir.social_geha;

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

import com.example.ofir.social_geha.Activities_and_Fragments.Login;
import com.example.ofir.social_geha.Activities_and_Fragments.activity_main_drawer;
import com.example.ofir.social_geha.Firebase.Database;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView logo;
    private static int splashTimeOut = 3000;
    private static final int LOGIN_RETURN_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo_splash);
        progressBar = findViewById(R.id.splash_pb);

        Animation logo_animation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        logo.startAnimation(logo_animation);
        progressBar.startAnimation(logo_animation);


        if (!Database.getInstance().isLoggedIn()) {
            promptLogin();
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveToHomeScreen();
                }
            }, splashTimeOut);
        }
    }

    public void promptLogin() {
        Intent intent = new Intent(SplashActivity.this, Login.class);
        startActivityForResult(intent, LOGIN_RETURN_CODE);
    }

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

    private void moveToHomeScreen(){
        startActivity(new Intent(SplashActivity.this, activity_main_drawer.class));
        finish();
    }
}
