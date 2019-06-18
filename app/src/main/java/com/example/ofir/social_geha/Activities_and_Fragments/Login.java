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

import com.example.ofir.social_geha.AdminGivenData;
import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
                //if a personal code was inputted, check if it exists on the admin side
                String personal_code_txt = personal_code.getText().toString();
                if (personal_code_txt.equals("")) { // missing personal code
                    Toast.makeText(Login.this, missing_fields_err, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("YARIN", "onClick: DOING THIS");
                    //login as admin
                    Database.getInstance().getAuth().signInWithEmailAndPassword("adminCode@admin.com", "adminadmin")
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //find the personal code
                            Database.getInstance().getdb().collection("adminAddedUsers").whereEqualTo("user_code", personal_code_txt)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    Log.d("YARIN", "onClick: SUCCESS "+personal_code_txt);
                                    Intent myIntent = new Intent(Login.this, SettingsInfoEditActivity.class);
                                    for (DocumentSnapshot doc : queryDocumentSnapshots){
                                        Log.d("YARIN", "onSuccess: RECORDED SOMETHING");
                                    }
                                    // found something, allow registration
                                    if(queryDocumentSnapshots.size() > 0){
                                        AdminGivenData adminGivenData = queryDocumentSnapshots.getDocuments().get(0).toObject(AdminGivenData.class);
                                        myIntent.putExtra("code", personal_code_txt);
                                        myIntent.putExtra("adminGivenData", adminGivenData);
                                        startActivityForResult(myIntent, REGISTER_RETURN_CODE);
                                    }
                                    //not found, notify user
                                    else{
                                        Toast.makeText(Login.this, "הקוד שברשותך לא רשום במערכת", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("YARIN", "onFailure: FAILED "+e.getMessage());
                                }
                            });

                        }
                    });

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
