package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ofir.social_geha.FilterParameters;
import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Firebase.Message;
import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.ofir.social_geha.Activities_and_Fragments.activity_main_drawer.ALL_CHATS_TAG;

public class AvailableMatches extends AppCompatActivity {

    ListView listView;
    ArrayList<Person> approved_matches_list;
    ProgressBar progressBar;
    MatchesListAdapter adapter;
    TextView progressBarTitle;
    private FirebaseFirestore mFirestore;
    private static final String MESSAGES = "messages";
    private boolean someApproved = false;


    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_available_matches);

        FilterParameters filterParams = (FilterParameters) getIntent().getSerializableExtra("filterObject");

        progressBar = findViewById(R.id.progressBar);
        progressBarTitle = findViewById(R.id.progressBar_title);
        listView = findViewById(R.id.available_matches);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Person person = (Person) o; //As you are using Default String Adapter
                Intent myIntent = new Intent(AvailableMatches.this, ChatActivity.class);
                myIntent.putExtra("EXTRA_PHOTO_URL", person.getAnonymousIdentity().getImageName());
                myIntent.putExtra("EXTRA_PHOTO_COLOR", person.getAnonymousIdentity().getImageColor());
                myIntent.putExtra("EXTRA_NAME", person.getAnonymousIdentity().getName());
                myIntent.putExtra("EXTRA_PERSON_ID", person.getUserID());
                myIntent.putExtra("EXTRA_INITIATOR", true);
                AvailableMatches.this.startActivity(myIntent);
                //Toast.makeText(getBaseContext(),person.getPersonID(),Toast.LENGTH_SHORT).show();
            }
        });

        showItems(true);
        // ------ PROGRESS BAR START

        mFirestore = FirebaseFirestore.getInstance();
        //set listener to listen to accepts and add these people to matchesList
        mFirestore.collection(MESSAGES).whereEqualTo("toPersonID", Database.getInstance().getLoggedInUserID())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                Message message = doc.getDocument().toObject(Message.class); //added message
                                mFirestore.collection(MESSAGES).document(doc.getDocument().getId()).delete();
                                // decrypt regular messages
                                String contactUID = message.getFromPersonID();
                                if (message.getShown()) {
                                    continue;
                                }

                                /* self message*/
                                if (contactUID.equals(Database.getInstance().getLoggedInUserID()))
                                    continue;

                                String text = message.getMessage();
                                String senderUID = message.getFromPersonID();
                                if (text.substring(0, "CHAT ACCEPT$".length()).equals("CHAT ACCEPT$")) {
                                    //got new accepted chat message from p
                                    Database.getInstance().getdb().collection("users").whereEqualTo("userID", senderUID).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                                            Person p = doc.toObject(Person.class);

                                                            Log.d("WOO AH!", "added " + p.getRealName() + " to matchlist");
                                                            approved_matches_list.add(p);
                                                            adapter.notifyDataSetChanged();
                                                            if(!someApproved){ // First match so show list
                                                                showItems(false);
                                                            }
                                                            someApproved = true;
                                                        }
                                                    }
                                                }
                                    });
                                }
                            }
                        }
                    }

                });


        loadToolbar();
        loadList();
        filterUsers(filterParams);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // ==================================
    //          PRIVATE FUNCTIONS
    // ==================================

    private void loadList() {
        //Create the list of objects
        approved_matches_list = new ArrayList<>();

        //Attach to adapter
        adapter = new MatchesListAdapter(this, R.layout.match_row_layout, approved_matches_list);
        listView.setAdapter(adapter);
    }

    private void loadToolbar() {
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.getNavigationIcon().setColorFilter(this.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    private void showItems(boolean progress_bar_shown) {
        if (progress_bar_shown) {
            progressBar.setVisibility(View.VISIBLE);
            progressBarTitle.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            progressBarTitle.setVisibility(View.GONE);
        }
    }

    private void filterUsers(FilterParameters f) {
        // query users
        Database.getInstance()
                .sendRequestsToMatches(f.getKind(), f.getGender(), f.getReligion(), f.getLanguages()
                        , f.getLower_bound(), f.getUpper_bound() /*, possibles_list, adapter*/);

//        for (Person p : possibles_list) {
//            //send chat request
//            Database.getInstance().sendControlMessage("CHAT REQUEST$", Database.getInstance().getLoggedInUserID(), p.getUserID());
//        }

        //sent messages to all - now we wait for response
        new Timer().schedule(new StaffSearch(), 30000); //send the staff a request in 5 seconds
        new Timer().schedule(new AbortSearch(), 60000); //abort the search in 10 seconds
    }

    class StaffSearch extends TimerTask {
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    String toast_txt = "טרם נמצאו אנשים מתאימים"+"\n"+"פונה אל אנשי הצוות במקום";
                    progressBarTitle.setText(toast_txt);
                    if(someApproved) return;
                    Database.getInstance().sendRequestsToStaff();
                }
            });
        }
    }

    class AbortSearch extends TimerTask {
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    if(someApproved) return;

                    Toast.makeText(AvailableMatches.this, "אני מצטער, לא מצאנו לך אנשים לשוחח עימם.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    progressBarTitle.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    //onBackPressed();
                }
            });
        }
    }

}
