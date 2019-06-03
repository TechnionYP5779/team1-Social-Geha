package com.example.ofir.social_geha.Firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ofir.social_geha.Activities_and_Fragments.MatchesListAdapter;
import com.example.ofir.social_geha.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;


public final class Database {
    private static final Database DB = new Database();
    private static String TAG = "DatabaseStatus";
    private static String MESSAGES = "messages";
    private static String USERS = "users";
    //private Person p;

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseAuth auth = FirebaseAuth.getInstance();


    public static Database getInstance() {
        return DB;
    }

    public void sendMessage(String message, Person from, Person to) {
        sendMessage(message, from.getUserID(), to.getUserID());
    }

    public void sendControlMessage(String message, String fromUser, String toUser) {
        sendMessageInner(message, fromUser, toUser, false);
    }

    public void sendMessage(String message, String fromUser, String toUser) {
        sendMessageInner(message, fromUser, toUser, true);
    }

    public void sendMessageInner(final String message, String fromUser, String toUser, boolean shown) {
        db.collection(MESSAGES)
                .add(new Message(message, fromUser, toUser, shown))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "wrote message: " + message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: FAILED " + e.getMessage());
                    }
                });
    }

    public void addUserPerson(final Person p) {
        Log.d("PERSON PRINTING !", p.getRealName());
        //Log.d("PERSON PRINTING !", p.getCalendarBirthDate().toString());
        //p.getCalendarBirthDate().set
        db.collection(USERS).document(p.getUserID()).set(p).
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

    public void addUser(final String username, String personalCode, String userID) {
        db.collection(USERS).document(username).set(new User(username, personalCode, userID)).
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

    public void queryUsers(Person.Kind kindPref, Person.Gender genderPref, Person.Religion religionPref,
                           EnumSet<Person.Language> languagesPref, Integer lower_bound, Integer upper_bound,
                           final ArrayList<Person> matches_list, final MatchesListAdapter adapter) {
        //TODO: add filter by availability
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
                Log.d("PEOPLE FOUND", "Looking for language: " + language.toString());
                QueryBuilder queryBuilder = new QueryBuilder(db.collection(USERS));
                queryBuilder.addWhereEquals("gender", genderPref)
                        .addWhereEquals("religion", religionPref)
                        .addWhereEquals("kind", kindPref);
                langQueryResults.add(queryBuilder.build().whereArrayContains("spokenLanguages", language.toString()).get());
            }
        }
        // add the age ranges, consider how to calculate the ages using the current date
        List<Task<QuerySnapshot>> ageQueryResults = new ArrayList<>();
        if (lower_bound != null) {
            Calendar youngest = Calendar.getInstance(), oldest = Calendar.getInstance();
            oldest.add(Calendar.YEAR, -upper_bound);
            youngest.add(Calendar.YEAR, -lower_bound);
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
                            Log.d("WOO AH!", "THERE ARE FILTER RESULTS!!");
                            Person p = documentSnapshot.toObject(Person.class);
                            if (!p.getUserID().equals(getInstance().getLoggedInUserID()) &&
                                    !matches_list.contains(p))
                                matches_list.add(p);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }


        if (lower_bound != null) {
            for (Task<QuerySnapshot> task1 : ageQueryResults) {
                task1.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.d("PEOPLE FOUND", "NOT DELETING PEOPLE");
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

    public void sendRequestsToStaff(){
        Database.getInstance()
                .sendRequestsToMatches(Person.Kind.STAFF, null, null,null,null,null);
    }

    public void sendRequestsToMatches(Person.Kind kindPref, Person.Gender genderPref, Person.Religion religionPref,
                           EnumSet<Person.Language> languagesPref, Integer lower_bound, Integer upper_bound) {

        final ArrayList<Person> matches_list = new ArrayList<>();

        //TODO: add filter by availability
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
                Log.d("PEOPLE FOUND", "Looking for language: " + language.toString());
                QueryBuilder queryBuilder = new QueryBuilder(db.collection(USERS));
                queryBuilder.addWhereEquals("gender", genderPref)
                        .addWhereEquals("religion", religionPref)
                        .addWhereEquals("kind", kindPref);
                langQueryResults.add(queryBuilder.build().whereArrayContains("spokenLanguages", language.toString()).get());
            }
        }
        // add the age ranges, consider how to calculate the ages using the current date
        List<Task<QuerySnapshot>> ageQueryResults = new ArrayList<>();
        if (lower_bound != null) {
            Calendar youngest = Calendar.getInstance(), oldest = Calendar.getInstance();
            oldest.add(Calendar.YEAR, -upper_bound);
            youngest.add(Calendar.YEAR, -lower_bound);
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
                            if (!p.getUserID().equals(getInstance().getLoggedInUserID()) &&
                                    !matches_list.contains(p)) {
                                matches_list.add(p);
                                Database.getInstance().sendControlMessage("CHAT REQUEST$", Database.getInstance().getLoggedInUserID(), p.getUserID());
                                Log.d("WOO AH!", "SENT REQUEST TO " + p.getRealName());
                            }
                        }
                    }
                }
            });
        }

        if (lower_bound != null) {
            for (Task<QuerySnapshot> task1 : ageQueryResults) {
                task1.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.d("PEOPLE FOUND", "NOT DELETING PEOPLE");
                                Person currPerson = documentSnapshot.toObject(Person.class);
                                if (!matches_list.contains(currPerson))
                                    matches_list.remove(currPerson);
                            }
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

    public void updateAvailability(Boolean newAvail) {

    }

    public void disconnectUser() {
        auth.signOut();
    }

    public FirebaseFirestore getdb() {
        return db;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    //    public Person getLoggedInPerson() {
//        if(this.p != null) return this.p;
//
//        final Person[] p = {null};
//        Log.d("SHAI", "LOGGEDINID = " + getLoggedInUserID());
//
//        this.getdb().collection("users").whereEqualTo("userID", this.getLoggedInUserID()).get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot doc : task.getResult()) {
//                                p[0] = doc.toObject(Person.class);
//                                Log.d("SHAI", "found PERSON!!!");
//                            }
//                            if(p[0] == null) Log.d("SHAI", "didn't find anyone");
//                        }
//                        Log.d("SHAI", "task unsuccessful!");
//                    }
//                });
//
//        if(p[0] == null) {
//            Log.d("SHAI", "PERSON = null");
//        } else {
//            Log.d("SHAI", "PERSON != null");
//        }
//
//        this.p = p[0];
//        return this.p;
//    }

}
