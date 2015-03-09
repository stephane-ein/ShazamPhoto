package fr.isen.shazamphoto.ui;

import android.os.Bundle;
import android.view.View;

import fr.isen.shazamphoto.model.ModelNavigation;

public class FavouriteMonument extends MonumentList {

    private View view;

    public static FavouriteMonument newInstance(ModelNavigation modelNavigation) {
        FavouriteMonument fragment = new FavouriteMonument();
        Bundle args = new Bundle();
        args.putSerializable(ModelNavigation.KEY, modelNavigation);
        fragment.setArguments(args);
        return fragment;
    }

    public FavouriteMonument() {
        super(FavouriteMonument.class.getSimpleName());
    }


}
