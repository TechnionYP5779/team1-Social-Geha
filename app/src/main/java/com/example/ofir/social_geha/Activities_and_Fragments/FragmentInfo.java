package com.example.ofir.social_geha.Activities_and_Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ofir.social_geha.Activities_and_Fragments.activity_main_drawer;
import com.example.ofir.social_geha.R;

/**
 * This class does nothing except display an info page
 */
public class FragmentInfo extends Fragment {
    public FragmentInfo() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((activity_main_drawer)getActivity()).setActionBarTitle("אודות");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

}
