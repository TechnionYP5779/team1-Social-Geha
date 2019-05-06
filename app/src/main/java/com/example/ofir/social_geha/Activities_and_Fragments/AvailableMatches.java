package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.ofir.social_geha.AnonymousIdentity;
import com.example.ofir.social_geha.FictitiousIdentityGenerator;
import com.example.ofir.social_geha.FilterParameters;
import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class AvailableMatches extends AppCompatActivity {

    ListView listView;
    ArrayList<Person>  matches_list;
    ProgressBar progressBar;
    MatchesListAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_available_matches);

        FilterParameters filterParms = (FilterParameters) getIntent().getSerializableExtra("filterObject");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView)findViewById(R.id.available_matches);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Person person = (Person)o; //As you are using Default String Adapter
                Intent myIntent = new Intent(AvailableMatches.this, ChatActivity.class);
                myIntent.putExtra("EXTRA_PHOTO_URL", person.getAnonymousIdentity().getImageName());
                myIntent.putExtra("EXTRA_NAME", person.getAnonymousIdentity().getName());
                myIntent.putExtra("EXTRA_PERSON_ID", person.getUserID());
                AvailableMatches.this.startActivity(myIntent);
                //Toast.makeText(getBaseContext(),person.getPersonID(),Toast.LENGTH_SHORT).show();
            }
        });

        showItems(true);
        // ------ PROGRESS BAR START
        loadToolbar();
        loadList();
        filterUsers(filterParms);
        // ------ END
        showItems(false);

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

    private void loadList(){
        //Create the list of objects
        matches_list = new ArrayList<>();

        //Attach to adapter
        adapter = new MatchesListAdapter(this, R.layout.match_row_layout, matches_list);
        listView.setAdapter(adapter);
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
            progressBar.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else{
            listView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void filterUsers(FilterParameters f){
        // query users
        Database.getInstance().queryUsers(f.getKind(), f.getGender(), f.getReligion(),
                f.getLanguages(), f.getLower_bound(), f.getUpper_bound(), matches_list, adapter);
    }
}
