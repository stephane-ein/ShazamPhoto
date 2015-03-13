package fr.isen.shazamphoto.database;

import android.content.Context;
import android.database.Cursor;

public abstract class ShazamDAO extends DAOBase {

    public ShazamDAO(Context pContext) {
        super(pContext);
    }

    protected Monument cursorToMonument(Cursor cursor) {
        return new Monument(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), new Localization(0,0.0,0.0));
    }
}
