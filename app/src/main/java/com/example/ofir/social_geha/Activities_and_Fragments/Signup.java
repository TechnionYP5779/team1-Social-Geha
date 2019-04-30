package com.example.ofir.social_geha.Activities_and_Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {
    EditText username;
    EditText password;
    EditText passwordVerify;
    Button signup;
    String code;

    private static final int MAX_USERNAME_LENGTH = 16;
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_PASSWORD_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 6;

    enum Result {INCORRECT_LENGTH, INVALID_CHARACTERS, OK}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        code = getIntent().getStringExtra("code");

        //getViews
        username = findViewById(R.id.user_name);
        password = findViewById(R.id.password);
        passwordVerify = findViewById(R.id.password_verify);
        signup = findViewById(R.id.signup_button);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    register(username.getText().toString(), password.getText().toString(), code);
                }
            }
        });

    }

    private Result isUsernameValid() {
        String check = username.getText().toString();
        Log.d("TTTT", "isUsernameValid: "+check);
        if (check.length() > MAX_USERNAME_LENGTH || check.length() < MIN_USERNAME_LENGTH) {
            return Result.INCORRECT_LENGTH;
        }
        if (!check.matches("[A-Za-z0-9_]+")) {
            return Result.INVALID_CHARACTERS;
        }
        return Result.OK;
    }

    private Result isPasswordValid() {
        String check = password.getText().toString();
        if (check.length() > MAX_PASSWORD_LENGTH || check.length() < MIN_PASSWORD_LENGTH) {
            return Result.INCORRECT_LENGTH;
        }
        if (!check.matches("[A-Za-z0-9_@#$]+")) {
            return Result.INVALID_CHARACTERS;
        }
        return Result.OK;
    }

    private boolean isPasswordVerifyValid() {
        String pass = password.getText().toString();
        String check = passwordVerify.getText().toString();
        return pass.equals(check);
    }

    private boolean isValid(){
        Result res = isUsernameValid();
        if(res == Result.INCORRECT_LENGTH){
            Toast.makeText(this, "אורך שם המשתמש צריך להיות בין 3 ל-16 תווים", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(res == Result.INVALID_CHARACTERS){
            Toast.makeText(this, "שם המשתמש צריך להכיל אותיות באנגלית, מספרים וקו תחתון בלבד", Toast.LENGTH_SHORT).show();
            return false;
        }
        res = isPasswordValid();
        if(res == Result.INCORRECT_LENGTH){
            Toast.makeText(this, "אורך הסיסמה צריך להיות בין 6 ל-20 תווים", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(res == Result.INVALID_CHARACTERS){
            Toast.makeText(this, "הסיסמה צריכה להכיל אותיות באנגלית, מספרים והתווים: _,@,$,# בלבד", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!isPasswordVerifyValid()){
            Toast.makeText(this, "הסיסמאות לא תואמות", Toast.LENGTH_SHORT).show();
            return false;
        }
        Toast.makeText(this, "הכל תקין", Toast.LENGTH_SHORT).show();
        return true;
    }

    public void register(final String username, String password, final String personalCode) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("נרשם");
        progressDialog.setMessage("אנא המתן...");
        String email = username.concat("@geha-technion.temp.com");
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Database.getInstance().addUser(username, personalCode, Database.getInstance().getLoggedInUserID());
                            setResult(RESULT_OK);
                            finish();
                            Toast.makeText(Signup.this, "נרשמת", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Signup.this, "שם המשתמש כבר תפוס!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
