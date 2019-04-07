package com.example.ofir.social_geha.Activities_and_Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    Button button;
    EditText username;
    EditText password;
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
        username = this.findViewById(R.id.user_name);
        password = this.findViewById(R.id.password);
        personal_code = this.findViewById(R.id.personal_code);
        button = this.findViewById(R.id.log_in_button);
        final String missing_fields_err = this.getString(R.string.missing_fields_err_msg);

        // setting listeners
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personal_code_txt = personal_code.getText().toString();
                String username_txt = username.getText().toString();
                String password_txt = password.getText().toString();

                if (username_txt.equals("") || password_txt.equals("")) { // New User
                    if (personal_code_txt.equals("")) {
                        Toast.makeText(Login.this, missing_fields_err, Toast.LENGTH_SHORT).show();
                    } else { // move to sign-up
                        Intent myIntent = new Intent(Login.this, Signup.class);
                        myIntent.putExtra("code", personal_code_txt);
                        startActivityForResult(myIntent, REGISTER_RETURN_CODE);
                    }
                } else {
                    // move to main screen (sign in)
                    signIn(username_txt, password_txt);
                }
            }
        });
    }

    private void signIn(String username, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("מתחבר");
        progressDialog.setMessage("אנא המתן...");
        String email = username.concat("@geha-technion.temp.com");
        progressDialog.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "המשתמש לא קיים או שהסיסמה לא נכונה", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "הקוד הראשוני שהתקבל מגהה",Toast.LENGTH_LONG).show();
    }
}
