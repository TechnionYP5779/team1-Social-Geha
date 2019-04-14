package com.example.ofir.social_geha.Firebase;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.ofir.social_geha.Person;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    public void sendMessage(String message, Person from, Person to) {
        sendMessage(message, from.getPersonID(), to.getPersonID());
    }

    public void sendMessage(String message, String fromUser, String toUser) {
        db.collection(MESSAGES)
                .add(new Message(message, fromUser, toUser))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: Added the new message_layout to the DB" + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: FAILED " + e.getMessage());
                    }
                });
    }

    public void addUser(final String username, String personalCode) {
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Set<Person> queryUsers(Boolean isPreviousPatient,
                                  Person.Gender genderPref,
                                  Person.Religion religionPref,
                                  EnumSet<Person.Language> langsPref) {
        List<Task<QuerySnapshot>> queryResults = new ArrayList<>();
        if (langsPref.isEmpty()) {
            QueryBuilder queryBuilder = new QueryBuilder(db.collection(USERS));
            queryBuilder.addWhereEquals("gender", genderPref)
                    .addWhereEquals("religion", religionPref)
                    .addWhereEquals("is_prev_patient", isPreviousPatient);
            queryResults.add(queryBuilder.build().get());
        } else {
            for (Person.Language language : langsPref) {
                QueryBuilder queryBuilder = new QueryBuilder(db.collection(USERS));
                queryBuilder.addWhereEquals("gender", genderPref)
                        .addWhereEquals("religion", religionPref)
                        .addWhereEquals("is_prev_patient", isPreviousPatient);
                queryResults.add(queryBuilder.build().whereArrayContains("languages", language.toString()).get());
            }
        }
        Set<Person> resultSet = new HashSet<>();
        for (Task<QuerySnapshot> task : queryResults) {
            QuerySnapshot result = task.getResult();
            if (result == null) continue;
            for (DocumentSnapshot documentSnapshot : result.getDocuments()) {
                resultSet.add(new Person(documentSnapshot));
            }
        }
        return resultSet;
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public String getLoggedInUserID() {
        return auth.getUid();
    }

    public void disconnectUser() {
        auth.signOut();
    }


}
