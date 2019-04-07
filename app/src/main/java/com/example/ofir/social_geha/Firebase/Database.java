package com.example.ofir.social_geha.Firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public final class Database {
    private static final Database DB = new Database();
    private static String TAG = "DatabaseStatus";
    private static String MESSAGES = "messages";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public static Database getInstance() {
        return DB;
    }

    public void sendMessage(String message, User from, User to) {
        sendMessage(message, from.getUserID(), to.getUserID());
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
}
