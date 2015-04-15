package fr.isen.shazamphoto.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.FavouriteMonumentDAO;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.model.ModelNavigation;
import fr.isen.shazamphoto.ui.CustomAdapter.GridFavrouriteAdapter;

public class FavouriteMonument extends MonumentList {

    public static FavouriteMonument newInstance(ModelNavigation modelNavigation) {
        FavouriteMonument fragment = new FavouriteMonument();
        fragment.setModelNavigation(modelNavigation);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite_monument, container, false);

        setAdapter(new GridFavrouriteAdapter(getActivity(), getFavouriteMonuments(), getActivity()));
        setAbsListView((GridView) view.findViewById(R.id.ffm_gridview));

        // The the monuments in the grid view
        setListMonuments(getFavouriteMonuments(), getAdapter(), getAbsListView());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            setAdapter(new GridFavrouriteAdapter(getActivity(), getFavouriteMonuments(), getActivity()));
            setListMonuments(getFavouriteMonuments(), getAdapter(), getAbsListView());
        }
    }

    public ArrayList<Monument> getFavouriteMonuments() {
        FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(getActivity());
        favouriteMonumentDAO.open();
        final List<Monument> monumentsList = favouriteMonumentDAO.getAllMonuments();
        favouriteMonumentDAO.close();
        final ArrayList<Monument> monuments = new ArrayList<>();
        for (Monument monument : monumentsList) {
            monuments.add(monument);
        }

        return monuments;
    }

}
