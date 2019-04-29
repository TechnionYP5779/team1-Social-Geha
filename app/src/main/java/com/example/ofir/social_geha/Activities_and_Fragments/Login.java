package com.example.ofir.social_geha.Activities_and_Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ofir.social_geha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    // ==================================
    //          CLASS VARIABLES
    // ==================================
    FloatingActionButton button;
    EditText personal_code;

    private int REGISTER_RETURN_CODE = 100;

    // ==================================
    //              FUNCTIONS
    // ==================================
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // get views
        personal_code = this.findViewById(R.id.personal_code);
        button = this.findViewById(R.id.log_in_button);
        final String missing_fields_err = this.getString(R.string.missing_fields_err_msg);

        // setting listeners
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personal_code_txt = personal_code.getText().toString();
                if (personal_code_txt.equals("")) { // missing personal code
                    Toast.makeText(Login.this, missing_fields_err, Toast.LENGTH_SHORT).show();
                } else {
                    Intent myIntent = new Intent(Login.this, SettingsInfoEditActivity.class);
                    myIntent.putExtra("code", personal_code_txt);
                    startActivityForResult(myIntent, REGISTER_RETURN_CODE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REGISTER_RETURN_CODE) {
            if (resultCode == RESULT_OK) {
                //SIGNUP WORKED AND WE ARE NOW LOGGED IN - WE CAN FINISH THIS ACTIVITY
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    public void personalCodeInfo(View view) {
        Toast.makeText(this, this.getString(R.string.whats_this_msg),Toast.LENGTH_LONG).show();
    }
}
