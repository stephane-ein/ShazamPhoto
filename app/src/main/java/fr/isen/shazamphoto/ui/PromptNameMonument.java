package fr.isen.shazamphoto.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import fr.isen.shazamphoto.R;

public class PromptNameMonument  extends Fragment {

    public PromptNameMonument() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_unidentified_monument, container, false);
        Button button = (Button) rootView.findViewById(R.id.button_no);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });


        return rootView;
    }
}