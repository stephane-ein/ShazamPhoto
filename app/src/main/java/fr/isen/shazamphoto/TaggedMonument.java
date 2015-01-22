package fr.isen.shazamphoto;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;


public class TaggedMonument extends Fragment {

    public static TaggedMonument newInstance(String param1, String param2) {
        TaggedMonument fragment = new TaggedMonument();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TaggedMonument() {
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
        View view =  inflater.inflate(R.layout.fragment_tagged_monument, container, false);

        ArrayList<Monument> monuments = new ArrayList<>();
        monuments.add(new Monument(123, "Tour Eiffel", "Date", "date", 1, 2, 3, new Localization()));
        monuments.add(new Monument(123, "Tour Eiffel", "Date", "date", 1, 2, 3, new Localization()));

        CustomListAdapter adapter = new CustomListAdapter(getActivity(), monuments);
        ListView listview = (ListView) view.findViewById(R.id.list_tagged_monument);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        return view;
    }

}
