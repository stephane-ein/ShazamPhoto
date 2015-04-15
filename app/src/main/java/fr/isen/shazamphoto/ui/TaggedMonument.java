package fr.isen.shazamphoto.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.database.TaggedMonumentDAO;
import fr.isen.shazamphoto.model.ModelNavigation;
import fr.isen.shazamphoto.ui.CustomAdapter.GridFavrouriteAdapter;

public class TaggedMonument extends MonumentList {

    public static TaggedMonument newInstance(ModelNavigation modelNavigation) {
        TaggedMonument fragment = new TaggedMonument();
        fragment.setModelNavigation(modelNavigation);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite_monument, container, false);

        setAdapter(new GridFavrouriteAdapter(getActivity(), getTaggedMonuments(), getActivity()));
        setAbsListView((GridView) view.findViewById(R.id.ffm_gridview));

        // The the monuments in the grid view
        setListMonuments(getTaggedMonuments(), getAdapter(), getAbsListView());

        setRetainInstance(true);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            setAdapter(new GridFavrouriteAdapter(getActivity(), getTaggedMonuments(), getActivity()));
            setListMonuments(getTaggedMonuments(), getAdapter(), getAbsListView());
        }
    }

    public ArrayList<Monument> getTaggedMonuments() {

        TaggedMonumentDAO taggedMonumentDAO = new TaggedMonumentDAO(getActivity());
        taggedMonumentDAO.open();
        final List<Monument> monumentsList = taggedMonumentDAO.getAllMonuments();
        taggedMonumentDAO.close();
        final ArrayList<Monument> monuments = new ArrayList<>();
        for (Monument monument : monumentsList) {
            monuments.add(monument);
        }

        return monuments;
    }

}
