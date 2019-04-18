package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;

import java.util.ArrayList;

public class AllChatsActivity extends AppCompatActivity {
    Toolbar mToolbar;
    MatchesListAdapter mAdapter;
    ListView mListView;
    TextView mEmptyView;
    ArrayList<Person> conversationList;
    ArrayList<Person> allList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

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
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(AllChatsActivity.this, adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
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
                    if(p.getRealName().trim().toLowerCase().contains(newText.trim().toLowerCase())){
                        conversationList.add(p);
                    }
                }
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
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

    private void loadList(){
        //Create the list of objects
        conversationList = new ArrayList<>();
        InstantiateList();
        allList = new ArrayList<>(conversationList);

        //Attach to adapter
        mAdapter = new MatchesListAdapter(this, R.layout.match_row_layout, conversationList, true);
        mListView.setAdapter(mAdapter);
    }

    private void InstantiateList(){
//        conversationList.add(new Person("שמוליק שמוליק","מטופל עבר, בן 31, יהודי, דובר עברית","drawable://" + R.drawable.profilepic));
//        conversationList.add(new Person("קיפוד אנונימי","אמא של מטופל עבר, בת 58, יהודיה, דוברת עברית ורוסית","drawable://" + R.drawable.hedgehog));
//        conversationList.add(new Person("לוטרה אנונימית","מטופלת עבר, בת 22, נוצריה, דוברת עברית ואנגלית","drawable://" + R.drawable.otter));
//        conversationList.add(new Person("שמוליק שמוליק","מטופל עבר, בן 31, יהודי, דובר עברית","drawable://" + R.drawable.profilepic));
//        conversationList.add(new Person("קיפוד אנונימי","אמא של מטופל עבר, בת 58, יהודיה, דוברת עברית ורוסית","drawable://" + R.drawable.hedgehog));
    }
}