package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
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

    Context mContext ;
    List<ScreenItem> mListScreen;
    View layoutScreen;
    Animation btnAnim;
    Spinner mSpinner1;
    Spinner mSpinnerGender;
    Spinner mSpinnerReligious;

    //----------------------------------------
    public Kind kind_preference;
    public Gender gender_preference;
    public Religion religion_preference;
    public List<Language> languages_preference;

    public FilterMatchesPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutScreen = inflater.inflate(R.layout.activity_filter_match,null);

        ImageView imgSlide = layoutScreen.findViewById(R.id.intro_img);
        TextView title = layoutScreen.findViewById(R.id.intro_title);
        TextView description = layoutScreen.findViewById(R.id.intro_description);
        mSpinner1 = layoutScreen.findViewById(R.id.spinner_preferences); // TODO - attach listeners
        mSpinnerGender = layoutScreen.findViewById(R.id.spinner_gender);
        mSpinnerReligious = layoutScreen.findViewById(R.id.spinner_religious);

        mSpinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                gender_preference = Person.fromStringToGenderEnum(mContext.getResources().getStringArray(R.array.gender_preferences)[position],mContext, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                gender_preference = Gender.UNDISCLOSED;
            }

        });

        mSpinnerReligious.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                religion_preference = Person.fromStringToReligion(mContext.getResources().getStringArray(R.array.religious_preferences)[position], true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                religion_preference = Religion.SECULAR;
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

    public void showScreen(int position){
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

    public void updatePersonInfo(){
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
