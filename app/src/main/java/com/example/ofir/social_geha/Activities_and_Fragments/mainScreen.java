package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.R;

public class mainScreen extends AppCompatActivity {

    LinearLayout settings_screen;
    LinearLayout edit_info;
    LinearLayout all_conversations;
    LinearLayout search_guide;

    private static final int LOGIN_RETURN_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        settings_screen = this.findViewById(R.id.settings);
        edit_info = this.findViewById(R.id.edit_info);
        all_conversations = this.findViewById(R.id.current_conversations);
        search_guide = this.findViewById(R.id.search_guide);

        if(!Database.getInstance().isLoggedIn()){
            promptLogin();
        }
    }

    public void gotoScreen(View view) {
        if (view.equals(settings_screen)) {
            // move to intro pager screen - TODO : is this the right place ?
            Intent myIntent = new Intent(mainScreen.this, SettingsMainActivity.class);
            mainScreen.this.startActivity(myIntent);
        }
        if (view.equals(edit_info)){
            Intent myIntent = new Intent(mainScreen.this, SettingsInfoEditActivity.class);
            mainScreen.this.startActivity(myIntent);
         }
        if (view.equals(all_conversations)){
            Intent myIntent = new Intent(mainScreen.this, AllChatsActivity.class);
            mainScreen.this.startActivity(myIntent);
        }
        if(view.equals(search_guide)) {
            Intent myIntent = new Intent(mainScreen.this, FilterMatchesActivity.class);
            myIntent.setFlags(myIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            mainScreen.this.startActivity(myIntent);
        }
    }

    public void promptLogin() {
        Intent intent = new Intent(mainScreen.this, Login.class);
        startActivityForResult(intent, LOGIN_RETURN_CODE);
    }

    private void loginSuccess() {
        Toast.makeText(getApplicationContext(), "מחובר בהצלחה!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_RETURN_CODE) {
            if (resultCode == RESULT_OK) {
                loginSuccess();
            } else {
                promptLogin();
            }
        }
    }

}
