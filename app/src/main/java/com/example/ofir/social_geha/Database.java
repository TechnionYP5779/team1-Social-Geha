package com.example.ofir.social_geha;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public final class Database {
    private static final Database DB = new Database();
    private static String TAG = "DatabaseStatus";
    private static String MESSAGES = "messages";
    private static String USERS = "users";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();


    public static Database getInstance() {
        return DB;
    }

    public void sendMessage(String message, String fromUser, String toUser) {
        db.collection(MESSAGES)
                .add(new Message(message, fromUser, toUser))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: Added the new message to the DB" + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: FAILED " + e.getMessage());
                    }
                });
    }

    public void addUser(final String username, String personalCode){
        db.collection(USERS).document(username).set(new User(username, personalCode)).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Added the new user to the DB" + username);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: FAILED " + e.getMessage());
                    }
                });
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public String getLoggedInUserID() {
        return auth.getUid();
    }

    public void disconnectUser(){
        auth.signOut();
    }


}
