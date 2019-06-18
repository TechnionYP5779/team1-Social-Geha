package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ofir.social_geha.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() { }



    /**
     * Loads the button to start a conversation - DEPRECATED
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((activity_main_drawer)getActivity()).setActionBarTitle("ראשי");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        Button button = v.findViewById(R.id.new_conversation_button);
        button.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), FilterMatchesActivity.class)));
        return v;
    }

}
