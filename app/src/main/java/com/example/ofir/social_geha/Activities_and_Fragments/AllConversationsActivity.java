package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Firebase.Message;
import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

public class AllConversationsActivity extends AppCompatActivity {

    private static final String MESSAGES = "messages";
    private static final String USERS = "users";
    private ListView mListView;
    private ArrayList<Person> mConversations;
    private MatchesListAdapter adapter;
    private FileHandler mFileHandler;
    private FirebaseFirestore mFirestore;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_conversations);


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mListView = (ListView)findViewById(R.id.conversations);
        mFileHandler = new FileHandler(this);
        mFirestore = FirebaseFirestore.getInstance();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = mListView.getItemAtPosition(position);
                Person person = (Person)o; //As you are using Default String Adapter
                Intent myIntent = new Intent(AllConversationsActivity.this, ChatActivity.class);
                myIntent.putExtra("EXTRA_PERSON_ID", person.getUserID());
                AllConversationsActivity.this.startActivity(myIntent);
                //Toast.makeText(getBaseContext(),person.getPersonID(),Toast.LENGTH_SHORT).show();
            }
        });

        showItems(true);
        // ------ PROGRESS BAR START
        loadToolbar();
        loadList();
        // ------ END
        showItems(false);
    }


    private void loadToolbar(){
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.getNavigationIcon().setColorFilter(this.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    private void showItems(boolean progress_bar_shown){
        if(progress_bar_shown){
            mProgressBar.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
        else{
            mListView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }


    private void loadList(){
        //Create the list of objects
        mConversations = new ArrayList<>();
        populateConversationsList();

        //Attach to adapter
        adapter = new MatchesListAdapter(this, R.layout.match_row_layout, mConversations, true);
        mListView.setAdapter(adapter);
    }


    private void populateConversationsList(){

        mFirestore.collection(MESSAGES).whereEqualTo("toPersonID", Database.getInstance().getLoggedInUserID())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d("COOLTEST","Error: " + e.getMessage());
                            return;
                        }

                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if(doc.getType() == DocumentChange.Type.ADDED){
                                //String message = doc.getDocument().getString("message");
                                //Log.d("COOLTEST","Content: " + message);
                                Message message = doc.getDocument().toObject(Message.class);
                                mFirestore.collection(MESSAGES).document(doc.getDocument().getId()).delete();
                                mFileHandler.writeMessage(message);

                            }
                        }

                    }
                });


        Set<String> personIdList = new HashSet<>();
        for(Message msg : mFileHandler.getMessages()){
            Log.d("COOLTEST","found msg:" + msg.getFromPersonID());
            personIdList.add(msg.getFromPersonID());
        }
        for(String id : personIdList){
            Task<QuerySnapshot> personQueryTask = mFirestore.collection(USERS).whereEqualTo("userID", id).get();
            personQueryTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    QuerySnapshot person = task.getResult();
                    Log.d("COOLTEST","get user Complete");
                    if(!person.isEmpty()) {
                        Person myPerson = person.toObjects(Person.class).get(0);
                        Log.d("COOLTEST","got user: " + myPerson.getDescription());
                        mConversations.add(myPerson);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}

