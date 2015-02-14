package fr.isen.shazamphoto.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.isen.shazamphoto.R;


public class AddMonument extends Fragment {


    public static AddMonument newInstance() {
        AddMonument fragment = new AddMonument();
        return fragment;
    }

    public AddMonument() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_monument, container, false);
    }



}
