package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AllChatsActivity extends AppCompatActivity {
    Toolbar mToolbar;
    MatchesListAdapter mAdapter;
    private FileHandler mFileHandler;
    ListView mListView;
    TextView mEmptyView;
    ArrayList<Person> conversationList;
    ArrayList<Person> allList;
    private FirebaseFirestore mFirestore;
    private static final String MESSAGES = "messages";
    private static final String USERS = "users";

    private static final int LOGIN_RETURN_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        if(!Database.getInstance().isLoggedIn()){
            promptLogin();
        }

        mFileHandler = new FileHandler(this);
        mFirestore = FirebaseFirestore.getInstance();
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.new_conversation_fb);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllChatsActivity.this, FilterMatchesActivity.class));
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mToolbar.getNavigationIcon().setColorFilter(this.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        mListView = (ListView) findViewById(R.id.list);
        mEmptyView = (TextView) findViewById(R.id.emptyView);

        loadList();


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = mListView.getItemAtPosition(position);
                Person person = (Person)o; //As you are using Default String Adapter
                Intent myIntent = new Intent(AllChatsActivity.this, ChatActivity.class);
                myIntent.putExtra("EXTRA_PERSON_ID", person.getUserID());
                myIntent.putExtra("EXTRA_NAME", person.getAnonymousIdentity().getName());
                myIntent.putExtra("EXTRA_PHOTO_URL", person.getAnonymousIdentity().getImageName());
                AllChatsActivity.this.startActivity(myIntent);
            }
        });


        mListView.setEmptyView(mEmptyView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_search, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);

        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                conversationList.clear();
                for(Person p : allList){
                    Log.d("PEOPLE", p.getAnonymousIdentity().getName());
                    if(p.getAnonymousIdentity().getName().trim().toLowerCase().contains(newText.trim().toLowerCase())){
                        conversationList.add(p);
                    }
                }
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void promptLogin() {
        Intent intent = new Intent(AllChatsActivity.this, Login.class);
        startActivityForResult(intent, LOGIN_RETURN_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // handle arrow click here
        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        if(id == R.id.menu_settings){
            // move to settings screen
            Intent intent = new Intent(AllChatsActivity.this, SettingsMainActivity.class);
            AllChatsActivity.this.startActivity(intent);
        }
        if(id == R.id.menu_edit_information){
            Intent intent = new Intent(AllChatsActivity.this, SettingsInfoEditActivity.class);
            AllChatsActivity.this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadList(){
        //Create the list of objects
        conversationList = new ArrayList<>();
        allList = new ArrayList<>();
        populateConversationsList();

        //Attach to adapter
        mAdapter = new MatchesListAdapter(this, R.layout.match_row_layout, conversationList, true);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_RETURN_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,"מחובר בהצלחה",Toast.LENGTH_SHORT).show();
            } else {
                promptLogin();
            }
        }
    }

    private void populateConversationsList(){
        mFirestore.collection(MESSAGES).whereEqualTo("toPersonID", Database.getInstance().getLoggedInUserID())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            return;
                        }
                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if(doc.getType() == DocumentChange.Type.ADDED){
                                //String message = doc.getDocument().getString("message");
                                //Log.d("COOLTEST","Content: " + message);
                                Message message = doc.getDocument().toObject(Message.class);
                                mFirestore.collection(MESSAGES).document(doc.getDocument().getId()).delete();
                                mFileHandler.writeMessage(message);
                                updateList();
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                });

        mFirestore.collection(MESSAGES).whereEqualTo("fromPersonID", Database.getInstance().getLoggedInUserID())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            return;
                        }
                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if(doc.getType() == DocumentChange.Type.ADDED){
                                //String message = doc.getDocument().getString("message");
                                //Log.d("COOLTEST","Content: " + message);

                                Message message = doc.getDocument().toObject(Message.class);
                                if(!message.getFromPersonID().equals(Database.getInstance().getLoggedInUserID())){
                                    mFirestore.collection(MESSAGES).document(doc.getDocument().getId()).delete();
                                    mFileHandler.writeMessage(message);
                                    updateList();
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                    }
                });
        updateList();

    }


    public void updateList(){
        conversationList.clear();
        allList.clear();
        Set<String> personIdList = new HashSet<>();
        for(Message msg : mFileHandler.getMessages()){
            Log.d("COOLTEST","found msg:" + msg.getFromPersonID());
            personIdList.add(msg.getFromPersonID());
        }
        personIdList.remove(Database.getInstance().getLoggedInUserID());
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
                        conversationList.add(myPerson);
                        allList.add(myPerson);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}