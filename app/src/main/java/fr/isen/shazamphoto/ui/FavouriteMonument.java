package fr.isen.shazamphoto.ui;

import android.view.View;

public class FavouriteMonument extends MonumentList {

    private View view;

    public static FavouriteMonument newInstance() {
        FavouriteMonument fragment = new FavouriteMonument();
        return fragment;
    }

    public FavouriteMonument() {
        super(FavouriteMonument.class.getSimpleName());
    }
}
