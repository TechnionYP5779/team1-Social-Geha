package com.example.ofir.social_geha;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    // ==================================
    //          CLASS VARIABLES
    // ==================================
    Button button;
    EditText username;
    EditText password;
    EditText personal_code;

    // ==================================
    //              FUNCTIONS
    // ==================================
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // get views
        username = this.findViewById(R.id.user_name);
        password = this.findViewById(R.id.password);
        personal_code = this.findViewById(R.id.personal_code);
        button = this.findViewById(R.id.log_in_button);

        // setting listeners
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personal_code_txt = personal_code.getText().toString();
                String username_txt = username.getText().toString();
                String password_txt = password.getText().toString();

                if(username_txt.equals("") || password_txt.equals("")){ // New User
                    if(personal_code_txt.equals("")){
                        Toast.makeText(Login.this, "Please fill all missing fields", Toast.LENGTH_SHORT).show();
                    }
                    else{ // move to signup
                        Intent myIntent = new Intent(Login.this, Signup.class);
                        Login.this.startActivity(myIntent);
                    }
                }
                else{
                    // move to main screen
                    Intent myIntent = new Intent(Login.this, mainScreen.class);
                    Login.this.startActivity(myIntent);
                }
            }
        });
    }
}
