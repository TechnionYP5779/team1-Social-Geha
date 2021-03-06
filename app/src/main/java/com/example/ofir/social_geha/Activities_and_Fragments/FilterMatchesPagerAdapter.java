package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.Person.*;
import com.example.ofir.social_geha.R;
import com.example.ofir.social_geha.ScreenItem;

import java.util.ArrayList;
import java.util.List;

public class FilterMatchesPagerAdapter extends PagerAdapter {

    //=====================================================
    //              CLASS VARIABLES
    //=====================================================
    private Context mContext ;
    private List<ScreenItem> mListScreen;
    private View layoutScreen;
    private Animation btnAnim;

    //----------------------------------------
    Kind kind_preference = null;
    Gender gender_preference = null;
    Religion religion_preference = null;
    List<Language> languages_preference= null;
    Integer lower_bound = null;
    Integer upper_bound = null;

    //=====================================================
    //              Functions
    //=====================================================
    // C'tor
    FilterMatchesPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }

    //=====================================================
    // This functions instantiates the views. (For example sets the right image)
    // It handles the new position screen data and calls showScreen with it(FilterMatchesActivity).
    //=====================================================
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutScreen = inflater.inflate(R.layout.activity_filter_match,null);

        ImageView imgSlide = layoutScreen.findViewById(R.id.intro_img);
        TextView title = layoutScreen.findViewById(R.id.intro_title);
        TextView description = layoutScreen.findViewById(R.id.intro_description);
        Spinner mSpinner1 = layoutScreen.findViewById(R.id.spinner_preferences); // TODO - attach listeners
        Spinner mSpinnerGender = layoutScreen.findViewById(R.id.spinner_gender);
        Spinner mSpinnerReligious = layoutScreen.findViewById(R.id.spinner_religious);
        Spinner mSpinnerAge = layoutScreen.findViewById(R.id.spinner_age);

        mSpinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                gender_preference = Person.fromStringToGenderEnum(mContext.getResources().getStringArray(R.array.gender_preferences)[position],mContext, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                gender_preference = null;
            }

        });

        mSpinnerReligious.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                religion_preference = Person.fromStringToReligion(mContext.getResources().getStringArray(R.array.religious_preferences)[position], true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                religion_preference = null;
            }

        });

        mSpinnerAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_value = mContext.getResources().getStringArray(R.array.age_preferences)[position];
                if(selected_value.equals("לא משנה")) {
                    lower_bound = null;
                    upper_bound = null;
                    return;
                }
                String[] numbers = selected_value.split("–"); //splits the string based on string
                lower_bound = Integer.valueOf(numbers[0]);
                upper_bound = Integer.valueOf(numbers[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lower_bound = null;
                upper_bound = null;
            }
        });

        title.setText(mListScreen.get(position).getTitle());
        description.setText(mListScreen.get(position).getDescription());
        imgSlide.setImageResource(mListScreen.get(position).getScreenImg());

        showScreen(position);

        btnAnim = AnimationUtils.loadAnimation(mContext,R.anim.button_animation);
        container.addView(layoutScreen);

        return layoutScreen;
    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    //=====================================================
    // This function shows the right views according to the position
    // For example, in the last screen we want to see the languages checkboxes.
    //=====================================================
    private void showScreen(int position){
        if(position == mListScreen.size() - 1){ // languages tab
            layoutScreen.findViewById(R.id.person_preferences).setVisibility(View.GONE);
            layoutScreen.findViewById(R.id.general_information).setVisibility(View.GONE);
            layoutScreen.findViewById(R.id.languages_checkboxes).setVisibility(View.VISIBLE);
            layoutScreen.findViewById(R.id.btn_get_started).setAnimation(btnAnim);
        }
        else{
            layoutScreen.findViewById(R.id.languages_checkboxes).setVisibility(View.GONE);
            if(position == 0){ // first tab
                layoutScreen.findViewById(R.id.person_preferences).setVisibility(View.VISIBLE);
                layoutScreen.findViewById(R.id.general_information).setVisibility(View.GONE);
            }
            else{ // basic information tab
                layoutScreen.findViewById(R.id.general_information).setVisibility(View.VISIBLE);
                layoutScreen.findViewById(R.id.person_preferences).setVisibility(View.GONE);
            }
        }
    }

    //=====================================================
    // This function updates the languages_preference variable to contain only selected values.
    //=====================================================
    void updatePersonInfo(){
        kind_preference = Kind.PAST_PATIENT; // TODO - fix that
        languages_preference = new ArrayList<>();

        boolean heb_language = ((CheckBox) layoutScreen.findViewById(R.id.chkHebrew)).isChecked();
        boolean amhar_language =((CheckBox) layoutScreen.findViewById(R.id.chkAmhar)).isChecked();
        boolean arab_language = ((CheckBox)layoutScreen.findViewById(R.id.chkArab)).isChecked();
        boolean english_language = ((CheckBox)layoutScreen.findViewById(R.id.chkEnglish)).isChecked();
        boolean french_language =((CheckBox) layoutScreen.findViewById(R.id.chkFrench)).isChecked();
        boolean russian_language = ((CheckBox)layoutScreen.findViewById(R.id.chkRussian)).isChecked();

        if(heb_language){
            languages_preference.add(Language.HEBREW);
        }
        if(amhar_language){
            languages_preference.add(Language.AMHARIC);
        }
        if(english_language){
            languages_preference.add(Language.ENGLISH);
        }
        if(arab_language){
            languages_preference.add(Language.ARABIC);
        }
        if(french_language){
            languages_preference.add(Language.FRENCH);
        }
        if(russian_language){
            languages_preference.add(Language.RUSSIAN);
        }

    }

}
