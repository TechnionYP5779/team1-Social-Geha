package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ofir.social_geha.FilterParameters;
import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Firebase.Message;
import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class maintains the match-list screen. The list starts out empty and people who have agreed to chat are added to it
 * If within a certain amount of time no one agrees to chat, then the in-house counsel is suggested
 */
public class AvailableMatches extends AppCompatActivity {

    ListView listView;
    ArrayList<Person> approved_matches_list;
    ProgressBar progressBar;
    MatchesListAdapter adapter;
    TextView progressBarTitle;
    private FirebaseFirestore mFirestore;
    private static final String MESSAGES_DB = "messages";
    private boolean someApproved = false;


    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_available_matches);

        FilterParameters filterParams = (FilterParameters) getIntent().getSerializableExtra("filterObject");

        progressBar = findViewById(R.id.progressBar);
        progressBarTitle = findViewById(R.id.progressBar_title);
        listView = findViewById(R.id.available_matches);

        //setup a listener such that clicking a match will move us to the chat screen with them
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Object o = listView.getItemAtPosition(position);
            Person person = (Person) o; //As you are using Default String Adapter
            Intent myIntent = new Intent(AvailableMatches.this, ChatActivity.class);
            myIntent.putExtra("EXTRA_PHOTO_URL", person.getAnonymousIdentity().getImageName());
            myIntent.putExtra("EXTRA_PHOTO_COLOR", person.getAnonymousIdentity().getImageColor());
            myIntent.putExtra("EXTRA_NAME", person.getAnonymousIdentity().getName());
            myIntent.putExtra("EXTRA_PERSON_ID", person.getUserID());
            myIntent.putExtra("EXTRA_INITIATOR", true);
            AvailableMatches.this.startActivity(myIntent);
        });

        showItems(true);
        // ------ PROGRESS BAR START

        mFirestore = FirebaseFirestore.getInstance();
        //set listener to receive "CHAT ACCEPT" messages and add these people to matchesList
        mFirestore.collection(MESSAGES_DB).whereEqualTo("toPersonID", Database.getInstance().getLoggedInUserID())
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null || queryDocumentSnapshots == null) {
                        return;
                    }

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            Message message = doc.getDocument().toObject(Message.class); //added message
                            mFirestore.collection(MESSAGES_DB).document(doc.getDocument().getId()).delete();

                            /* self message or a text message (not a control message) - we ignore*/
                            if (message.getShown() || message.getFromPersonID().equals(Database.getInstance().getLoggedInUserID()))
                                continue;

                            String text = message.getMessage();
                            String senderUID = message.getFromPersonID();
                            if (text.substring(0, "CHAT ACCEPT$".length()).equals("CHAT ACCEPT$")) {
                                //got new accepted chat message from p, we read p's information and add them to the list
                                Database.getInstance().getdb().collection("users").whereEqualTo("userID", senderUID).get()
                                        .addOnCompleteListener(task -> {
                                            if (task.getResult() != null && task.isSuccessful()) {
                                                for (QueryDocumentSnapshot doc1 : task.getResult()) {
                                                    Person p = doc1.toObject(Person.class);
                                                    approved_matches_list.add(p);
                                                    adapter.notifyDataSetChanged();
                                                    if(!someApproved){ // First match so show list
                                                        showItems(false);
                                                    }
                                                    someApproved = true;
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });

        loadToolbar();

        //Create the list of objects
        approved_matches_list = new ArrayList<>();

        //Attach to adapter
        adapter = new MatchesListAdapter(this, R.layout.match_row_layout, approved_matches_list);
        listView.setAdapter(adapter);

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

    /**
     * Implements the navigation back when the back button is pressed
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // ==================================
    //          PRIVATE FUNCTIONS
    // ==================================

    /**
     * Initialize and load the actionbar
     */
    private void loadToolbar() {
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if(toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setColorFilter(this.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
    }

    /**
     * makes the progress bar visible if @param progress_bar_shown, and hides it otherwise.
     * @param progress_bar_shown - whether or not to show the views
     */
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

    /**
     * The function finds users that are available to chat & match the criteria. Then, it sends a request to all of them.
     * After 30 seconds, the staff is queried.
     * After 60 seconds, the search is aborted.
     *
     * @param f - parameters according to which we should filter
     */
    private void filterUsers(FilterParameters f) {
        // query users to find appropriate matches and send them a chat request
        Database.getInstance()
                .sendRequestsToMatches(f.getKind(), f.getGender(), f.getReligion(), f.getLanguages()
                        , f.getLower_bound(), f.getUpper_bound() /*, possibles_list, adapter*/);

        //sent messages to all - now we wait for response
        new Timer().schedule(new StaffSearch(), 30000); //send the staff a request in 30 seconds
        new Timer().schedule(new AbortSearch(), 60000); //abort the search in 60 seconds
    }

    /**
     * This class can be given to a timer to make sure that after an X amount of time, we search for the staff counsel
     */
    class StaffSearch extends TimerTask {
        public void run() {
            runOnUiThread(() -> {
                String toast_txt = "טרם נמצאו אנשים מתאימים"+"\n"+"פונה אל אנשי הצוות במקום";
                progressBarTitle.setText(toast_txt);
                if(someApproved) return;
                Database.getInstance().sendRequestsToStaff();
            });
        }
    }

    /**
     * This class can be given to a timer to make the match search stop with an X amount of time
     */
    class AbortSearch extends TimerTask {
        public void run() {
            runOnUiThread(() -> {
                if(someApproved) return;

                Toast.makeText(AvailableMatches.this, "אני מצטער, לא מצאנו לך אנשים לשוחח עימם.", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                progressBarTitle.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
            });
        }
    }

}
