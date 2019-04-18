package com.example.ofir.social_geha.Firebase;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Range;

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
import java.util.Calendar;
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
        sendMessage(message, from.getUserID(), to.getUserID());
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Set<Person> queryUsers(Person.Kind kindPref,
                                  Person.Gender genderPref,
                                  Person.Religion religionPref,
                                  EnumSet<Person.Language> languagesPref,
                                  Range<Integer> ageRange) {
        List<Task<QuerySnapshot>> langQueryResults = new ArrayList<>();
        // note the different fields
        if (languagesPref.isEmpty()) {
            QueryBuilder queryBuilder = new QueryBuilder(db.collection(USERS));
            queryBuilder.addWhereEquals("gender", genderPref)
                    .addWhereEquals("religion", religionPref)
                    .addWhereEquals("kind", kindPref);
            langQueryResults.add(queryBuilder.build().get());
        } else {
            for (Person.Language language : languagesPref) {
                QueryBuilder queryBuilder = new QueryBuilder(db.collection(USERS));
                queryBuilder.addWhereEquals("gender", genderPref)
                        .addWhereEquals("religion", religionPref)
                        .addWhereEquals("kind", kindPref);
                langQueryResults.add(queryBuilder.build().whereArrayContains("languages", language.toString()).get());
            }
        }
        // add the age ranges, consider how to calculate the ages using the current date
        List<Task<QuerySnapshot>> ageQueryResults = new ArrayList<>();
        if (ageRange != null) {
            Calendar youngest = Calendar.getInstance(), oldest = Calendar.getInstance();
            oldest.add(Calendar.YEAR, -ageRange.getUpper());
            youngest.add(Calendar.YEAR, -ageRange.getLower());
            QueryBuilder queryBuilder = new QueryBuilder(db.collection(USERS));
            queryBuilder.addWhereEquals("gender", genderPref)
                    .addWhereEquals("religion", religionPref)
                    .addWhereEquals("kind", kindPref)
                    .addLowerBound("birth_date", oldest.getTimeInMillis())
                    .addUpperBound("birth_date", youngest.getTimeInMillis());
            ageQueryResults.add(queryBuilder.build().get());
        }
        Set<Person> resultSet = new HashSet<>();
        for (Task<QuerySnapshot> task : langQueryResults) {
            QuerySnapshot result = task.getResult();
            if (result == null) continue;
            for (DocumentSnapshot documentSnapshot : result.getDocuments()) {
                resultSet.add(documentSnapshot.toObject(Person.class));
            }
        }

        if (ageRange != null) {
            for (Task<QuerySnapshot> task : ageQueryResults) {
                QuerySnapshot result = task.getResult();
                if (result == null) continue;
                for (DocumentSnapshot documentSnapshot : result.getDocuments()) {
                    Person currPerson = documentSnapshot.toObject(Person.class);
                    if (!resultSet.contains(currPerson))
                        resultSet.remove(currPerson);
                }
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
