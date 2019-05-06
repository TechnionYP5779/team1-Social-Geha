package com.example.ofir.social_geha.Firebase;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Range;

import com.example.ofir.social_geha.Activities_and_Fragments.MatchesListAdapter;
import com.example.ofir.social_geha.Person;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.Objects;
import java.util.Set;


public final class Database {
    private static final Database DB = new Database();
    private static String TAG = "DatabaseStatus";
    private static String MESSAGES = "messages";
    private static String USERS = "users";

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
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


    public void addUserPerson(final Person p){
        Log.d("PERSON PRINTING !", p.getRealName());
        db.collection(USERS).document(p.getRealName()).set(p).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Added the new user to the DB" + p.getRealName());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: FAILED " + e.getMessage());
                    }
                });
    }

    public void addUser(final String username, String personalCode,String userID) {
        db.collection(USERS).document(username).set(new User(username, personalCode,userID)).
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

    public void queryUsers(Person.Kind kindPref,
                           Person.Gender genderPref,
                           Person.Religion religionPref,
                           EnumSet<Person.Language> languagesPref,
                           Range<Integer> ageRange,
                           final ArrayList<Person> matches_list,
                           final MatchesListAdapter adapter) {

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
                Log.d("PEOPLE FOUND","Looking for language: "+language.toString());
                QueryBuilder queryBuilder = new QueryBuilder(db.collection(USERS));
                queryBuilder.addWhereEquals("gender", genderPref)
                        .addWhereEquals("religion", religionPref)
                        .addWhereEquals("kind", kindPref);
                langQueryResults.add(queryBuilder.build().whereArrayContains("spokenLanguages", language.toString()).get());
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

        //STARTING TO FILTER
        for (Task<QuerySnapshot> task : langQueryResults) {
            task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult() != null) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            Person p = documentSnapshot.toObject(Person.class);
                            if(!p.getUserID().equals(getInstance().getLoggedInUserID()) &&
                            !matches_list.contains(p))
                                matches_list.add(p);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }

        if (ageRange != null) {
            for (Task<QuerySnapshot> task1 : ageQueryResults) {
                task1.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.d("PEOPLE FOUND","NOT DELETING PEOPLE");
                                Person currPerson = documentSnapshot.toObject(Person.class);
                                if (!matches_list.contains(currPerson))
                                    matches_list.remove(currPerson);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
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
