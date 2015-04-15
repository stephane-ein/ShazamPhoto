package fr.isen.shazamphoto.database;

import android.content.Context;
import android.database.Cursor;

public abstract class ShazamDAO extends DAOBase {

    public ShazamDAO(Context pContext) {
        super(pContext);
    }

    protected Monument cursorToMonument(Cursor cursor) {
        return new Monument(cursor.getLong(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5),
                cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), new Localization(0, cursor.getDouble(9), cursor.getDouble(10)), cursor.getString(12));
    }
}
