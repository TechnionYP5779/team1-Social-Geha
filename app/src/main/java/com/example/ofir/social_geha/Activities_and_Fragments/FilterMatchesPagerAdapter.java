package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.ofir.social_geha.R;
import com.example.ofir.social_geha.ScreenItem;

import java.util.List;

public class FilterMatchesPagerAdapter extends PagerAdapter {

    Context mContext ;
    List<ScreenItem> mListScreen;
    View layoutScreen;
    Animation btnAnim;
    Spinner mSpinner1;
    Spinner mSpinnerGender;
    Spinner mSpinnerReligious;

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


}
