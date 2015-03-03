package fr.isen.shazamphoto.database;

import android.content.Context;

import java.util.List;

public abstract class ListMonumentDAO extends ShazamDAO{

    public ListMonumentDAO(Context pContext) {
        super(pContext);
    }

    public abstract List<Monument> getAllMonuments();

}
