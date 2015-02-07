package fr.isen.shazamphoto.database;

import android.content.Context;
import android.database.Cursor;

import java.util.List;

public abstract class ListMonumentDAO extends DAOBase{

    public ListMonumentDAO(Context pContext) {
        super(pContext);
    }

    public abstract List<Monument> getAllMonuments();

    protected Monument cursorToMonument(Cursor cursor) {
        return new Monument(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), new Localization(0,0,0));
    }
}
