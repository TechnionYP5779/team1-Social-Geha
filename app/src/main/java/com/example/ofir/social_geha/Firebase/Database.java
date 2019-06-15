package com.example.ofir.social_geha.Firebase;

import android.util.Log;

import com.example.ofir.social_geha.Activities_and_Fragments.MatchesListAdapter;
import com.example.ofir.social_geha.Notifications.GehaMessagingService;
import com.example.ofir.social_geha.Person;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;


public final class Database {
    private static final Database DB = new Database();
    // String constants
    private static String TAG = "DatabaseStatus";
    private static String MESSAGES = "messages";
    private static String USERS = "users";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    /***
     * @return the single instance of Database
     */
    public static Database getInstance() {
        return DB;
    }

    /***
     * Send a text message between two people
     * @param message The content of the message
     * @param from The person from which the message is sent
     * @param to The person to whom the message is sent
     */
    public void sendMessage(String message, Person from, Person to) {
        sendMessage(message, from.getUserID(), to.getUserID());
    }

    /***
     * Send a control message (i.e., users do not see these messages) between two people
     * @param message The content of the control message (usually has a tag in the beginning)
     * @param fromUser The id of the person which sends the control message
     * @param toUser The id of the person which receives the control message
     */
    public void sendControlMessage(String message, String fromUser, String toUser) {
        sendMessageInner(message, fromUser, toUser, false);
    }

    /***
     * A simplified version of the above sendMessage which uses ids instead of Person
     * @param message The content of the message
     * @param fromUser The person from which the message is sent
     * @param toUser The person to whom the message is sent
     */
    public void sendMessage(String message, String fromUser, String toUser) {
        sendMessageInner(message, fromUser, toUser, true);
    }

    /***
     * The actual implementation of sending messages (text and control), should not be used directly
     * @param shown Whether the message is control (false) or text (true)
     */
    private void sendMessageInner(final String message, String fromUser, String toUser, boolean shown) {
        db.collection(MESSAGES)
                .add(new Message(message, fromUser, toUser, shown))
                .addOnSuccessListener(documentReference -> Log.d(TAG, "wrote message: " + message))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: FAILED " + e.getMessage()));
    }

    /***
     * Adds the Person to the database. If it already exists, this updates its values based on the id
     * @param p The person to insert/update
     */
    public void addUserPerson(final Person p) {
        Log.d("PERSON PRINTING !", p.getRealName());
        db.collection(USERS).document(p.getUserID()).set(p).
                addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "onSuccess: Added the new user to the DB" + p.getRealName());
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    GehaMessagingService.storeToken(Database.getInstance().getAuth(), Database.getInstance().getdb(), task.getResult().getToken());
                                }
                            });
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: FAILED " + e.getMessage()));
    }

    /***
     * Creates an empty person when the user first signs up to the app
     * @param username Dummy parameter
     * @param personalCode The code used to sign up, it is verified in the database
     * @param userID The id of the current user
     */
    public void addUser(final String username, String personalCode, String userID) {
        db.collection(USERS).document(username).set(new User(username, personalCode, userID)).
                addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "onSuccess: Added the new user to the DB" + username);
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    GehaMessagingService.storeToken(Database.getInstance().getAuth(), Database.getInstance().getdb(), task.getResult().getToken());
                                }
                            });
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: FAILED " + e.getMessage()));
    }

    /***
     * After a certain time no mentor answered to a conversation request, all the staff users are notified
     */
    public void sendRequestsToStaff() {
        Database.getInstance()
                .sendRequestsToMatches(Person.Kind.STAFF, null, null, null, null, null);
    }

    /***
     * Based on the parameters, all the available mentors get a conversation request
     * @param kindPref The kind of the mentor (past patient or a family member of a past patient)
     * @param genderPref The gender
     * @param religionPref The religion
     * @param languagesPref The languages spoken
     * @param lower_bound Currently a dummy parameter
     * @param upper_bound Currently a dummy parameter
     */
    public void sendRequestsToMatches(Person.Kind kindPref, Person.Gender genderPref, Person.Religion religionPref,
                                      EnumSet<Person.Language> languagesPref, Integer lower_bound, Integer upper_bound) {

        final ArrayList<Person> matches_list = new ArrayList<>();

        List<Task<QuerySnapshot>> langQueryResults = new ArrayList<>();
        // note the different fields
        if (languagesPref == null || languagesPref.isEmpty()) {
            QueryBuilder queryBuilder = new QueryBuilder(db.collection(USERS));
            queryBuilder.addWhereEquals("gender", genderPref)
                    .addWhereEquals("religion", religionPref)
                    .addWhereEquals("kind", kindPref)
                    .addWhereEquals("availability", true);
            langQueryResults.add(queryBuilder.build().get());
        } else {
            for (Person.Language language : languagesPref) { //not null && not empty
                Log.d("PEOPLE FOUND", "Looking for language: " + language.toString());
                QueryBuilder queryBuilder = new QueryBuilder(db.collection(USERS));
                queryBuilder.addWhereEquals("gender", genderPref)
                        .addWhereEquals("religion", religionPref)
                        .addWhereEquals("kind", kindPref)
                        .addWhereEquals("availability", true);
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
                    .addUpperBound("birth_date", youngest.getTimeInMillis())
                    .addWhereEquals("availability", true);
            ageQueryResults.add(queryBuilder.build().get());
        }

        //STARTING TO FILTER
        for (Task<QuerySnapshot> task : langQueryResults) {
            task.addOnCompleteListener(task12 -> {
                if (task12.getResult() != null) {
                    for (DocumentSnapshot documentSnapshot : task12.getResult()) {
                        Person p = documentSnapshot.toObject(Person.class);
                        if (!p.getUserID().equals(getInstance().getLoggedInUserID()) &&
                                !matches_list.contains(p)) {
                            matches_list.add(p);
                            Database.getInstance().sendControlMessage("CHAT REQUEST$", Database.getInstance().getLoggedInUserID(), p.getUserID());
                            Log.d("CHAT_REQ", "SENT REQUEST TO " + p.getRealName());
                        }
                    }
                }
            });
        }

        if (lower_bound != null) {
            for (Task<QuerySnapshot> task1 : ageQueryResults) {
                task1.addOnCompleteListener(task -> {
                    if (task.getResult() != null) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            Log.d("PEOPLE FOUND", "NOT DELETING PEOPLE");
                            Person currPerson = documentSnapshot.toObject(Person.class);
                            if (!matches_list.contains(currPerson))
                                matches_list.remove(currPerson);
                        }
                    }
                });
            }
        }

    }

    /***
     * @return Whether some user is logged in right now
     */
    public boolean noLoggedInUser() {
        return auth.getCurrentUser() == null;
    }

    /***
     * @return The id of the logged in user
     */
    public String getLoggedInUserID() {
        return auth.getUid();
    }

    /***
     * Mentors can receive conversation requests only if they are available
     * This function is used to update the availability of mentors
     * @param newAvail The new value of availability
     */
    public void updateAvailability(Boolean newAvail) {
        Database.getInstance().getdb().collection("users")
                .document(Database.getInstance().getLoggedInUserID())
                .update("availability", newAvail).addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
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

}
