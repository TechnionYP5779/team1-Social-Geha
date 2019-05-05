package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.R;

import java.util.ArrayList;

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
        share_info_screen=findViewById(R.id.info_share_button);
        password_change_screen=findViewById(R.id.password_change_button)  ;
        ask_for_help= findViewById(R.id.account_help_button);
        delete_data_screen=findViewById(R.id.account_deactivate_button);

    }
    String[] usersAll = {"אבי ישראלי", "ענת רימון"};
    boolean[] usersChecked ={true, true};
    ArrayList<Integer> mUserItems = new ArrayList<>();
    public void gotoScreen(View view) {
        if (view.equals(share_info_screen)){


            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsMainActivity.this, R.style.AlertDialogCustom);
            mBuilder.setMultiChoiceItems(usersAll, usersChecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    mUserItems.add(position);
                } else {
                    mUserItems.remove((Integer.valueOf(position)));
                }
            }
            });

            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                }
            });

        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < usersChecked.length; i++) {
                    usersChecked[i] = false;
                    mUserItems.clear();
//                            mItemSelected.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
        if (view.equals(password_change_screen)){
            Toast.makeText(SettingsMainActivity.this , getString(R.string.strings_password_change), Toast.LENGTH_SHORT).show();
        }

        if(view.equals(ask_for_help)) {
            Intent myIntent = new Intent(SettingsMainActivity.this , SettingsHelp.class);
            SettingsMainActivity.this.startActivity(myIntent);
        }

        if(view.equals(delete_data_screen)) {
            Toast.makeText(SettingsMainActivity.this , getString(R.string.strings_delete_account), Toast.LENGTH_SHORT).show();
        }
    }
}
