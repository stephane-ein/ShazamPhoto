package fr.isen.shazamphoto.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.database.TaggedMonumentDAO;

public class NearestMonumentsFragment extends Fragment {

    public static NearestMonumentsFragment newInstance() {
        NearestMonumentsFragment fragment = new NearestMonumentsFragment();
        return fragment;
    }

    public NearestMonumentsFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_nearest_monuments, container, false);
      /*  Button button = (Button) view.findViewById(R.id.button_launch_google_map);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NearestMonuments.class);
                startActivity(intent);
            }
        });*/

        ListView listview = (ListView) view.findViewById(R.id.listview_nearest_monument);

        TaggedMonumentDAO taggedMonumentDAO = new TaggedMonumentDAO(getActivity());
        taggedMonumentDAO.open();
        final List<Monument> monumentsList = taggedMonumentDAO.getAllMonuments();
        taggedMonumentDAO.close();
        final ArrayList<Monument> monuments = new ArrayList<>();
        for (Monument monument : monumentsList) {
            monuments.add(monument);
        }

        CustomListAdapter adapter = new CustomListAdapter(getActivity(), monuments);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }


}
