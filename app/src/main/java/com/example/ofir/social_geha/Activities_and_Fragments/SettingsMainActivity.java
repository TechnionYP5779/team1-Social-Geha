package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.R;

public class SettingsMainActivity extends AppCompatActivity {

    private Button share_info_screen;
    private Button password_change_screen;
    private Button ask_for_help;
    private Button delete_data_screen;

    //TODO: REMOVE LATER ON - FOR TESTING
    Button Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    public void gotoScreen(View view) {
        if (view.equals(share_info_screen))
            Toast.makeText(SettingsMainActivity.this , "Share Info", Toast.LENGTH_SHORT).show();
        if (view.equals(password_change_screen)){
            Toast.makeText(SettingsMainActivity.this , "Password Change", Toast.LENGTH_SHORT).show();
        }

        if(view.equals(ask_for_help)) {
            Intent myIntent = new Intent(SettingsMainActivity.this , SettingsHelp.class);
            SettingsMainActivity.this.startActivity(myIntent);
        }

        if(view.equals(delete_data_screen)) {
            Toast.makeText(SettingsMainActivity.this , "Delete Account", Toast.LENGTH_SHORT).show();
        }
    }
}
