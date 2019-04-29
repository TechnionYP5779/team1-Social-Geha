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

    private Button edit_info_screen;
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

        //added:
//
        edit_info_screen = this.findViewById(R.id.info_edit_button);
//        share_info_screen = this.findViewById(R.id.info_share_button);
//        password_change_screen = this.findViewById(R.id.password_change_button);
//        delete_data_screen = this.findViewById(R.id.account_deactivate_button);

//
//        Btn = findViewById(R.id.logout);
//
//        if (!Database.getInstance().isLoggedIn()) {
//       //     promptLogin();
//        }
        // end
    }

    private static final int LOGIN_RETURN_CODE = 1;


    public void gotoScreen(View view) {
        if (view.equals(edit_info_screen)){
            Intent myIntent = new Intent(SettingsMainActivity.this , SettingsInfoEditActivity.class);
            SettingsMainActivity.this.startActivity(myIntent);
        }
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
//        if (view.equals(Btn)){
//            Database.getInstance().disconnectUser();
//            promptLogin();
//        }
    }


//    public void promptLogin() {
//        Intent intent = new Intent(SettingsMainActivity.this, Login.class);
//        startActivityForResult(intent, LOGIN_RETURN_CODE);
//    }
//
//    private void loginSuccess() {
//        Toast.makeText(getApplicationContext(), "מחובר בהצלחה!", Toast.LENGTH_SHORT).show();
//    }

}
