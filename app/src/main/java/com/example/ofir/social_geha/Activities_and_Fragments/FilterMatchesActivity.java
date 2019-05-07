package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ofir.social_geha.FilterParameters;
import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;
import com.example.ofir.social_geha.ScreenItem;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FilterMatchesActivity extends AppCompatActivity {

    private ViewPager screenPager;
    FilterMatchesPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0;
    TextView tvSkip;
    List<ScreenItem> mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.filter_match_buttons);

        // ini views
        btnNext = findViewById(R.id.btn_next);
        tabIndicator = findViewById(R.id.tab_indicator);

        tvSkip = findViewById(R.id.tv_skip);

        // fill list screen
        // ALL PHOTOS FROM https://pixabay.com/
        mList = new ArrayList<>();
        String description = this.getString(R.string.strings_filter_desc);
        mList.add(new ScreenItem(this.getString(R.string.strings_filter_create_conv), description, R.drawable.chat_icon));
        mList.add(new ScreenItem(this.getString(R.string.strings_filter_basic_info), description, R.drawable.people));
        mList.add(new ScreenItem(this.getString(R.string.strings_filter_choose_lang), this.getString(R.string.strings_filter_lang_desc), R.drawable.earth_round));

        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new FilterMatchesPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tablayout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // next button click Listner
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                }

                showScreen(position);
                screenPager.setCurrentItem(position);

            }
        });


        // tablayout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showScreen(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // skip button click listener
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScreen(mList.size() - 1);
                screenPager.setCurrentItem(mList.size());
            }
        });
    }

    private void showScreen(int position) {
        if (position == mList.size() - 1) { // at the languages window
            btnNext.setVisibility(View.GONE);
            tvSkip.setVisibility(View.INVISIBLE);
            tabIndicator.setVisibility(View.GONE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            tvSkip.setVisibility(View.VISIBLE);
            tabIndicator.setVisibility(View.VISIBLE);
        }
    }

    public void findMatch(View view) {
        introViewPagerAdapter.updatePersonInfo();
        // from list to enumset
        EnumSet<Person.Language> temp = EnumSet.noneOf(Person.Language.class); // make an empty enumset
        temp.addAll(introViewPagerAdapter.languages_preference); // add varargs to it


        Log.d("PEOPLE FOUND", "========== " + introViewPagerAdapter.gender_preference + "===========");

        Intent myIntent = new Intent(FilterMatchesActivity.this, AvailableMatches.class);
        myIntent.setFlags(myIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        FilterParameters filterObj = new FilterParameters(temp, introViewPagerAdapter.kind_preference,
                introViewPagerAdapter.gender_preference, introViewPagerAdapter.religion_preference, introViewPagerAdapter.lower_bound, introViewPagerAdapter.upper_bound);
        myIntent.putExtra("filterObject", filterObj);
        FilterMatchesActivity.this.startActivity(myIntent);
    }
}