package fr.isen.shazamphoto.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MonumentDAO extends ShazamDAO {
    public MonumentDAO(Context pContext) {
        super(pContext);
    }

    public long insert(Monument monument) {
        ContentValues value = monumentToValues(monument);
        return mDb.insert(DatabaseHandler.MONUMENTS_TABLE_NAME, null, value);
    }

    public void delete(Monument monument) {
        delete(monument.getId());
    }

    public void delete(long id) {
        String[] args = {Long.toString(id)};
        mDb.delete(DatabaseHandler.VISITED_MONUMENTS_TABLE_NAME, DatabaseHandler.VISITED_MONUMENTS_MONUMENT_KEY + " = ?", args);
        mDb.delete(DatabaseHandler.FAVOURITE_MONUMENTS_TABLE_NAME, DatabaseHandler.FAVOURITE_MONUMENTS_MONUMENT_KEY + " = ?", args);
        mDb.delete(DatabaseHandler.MONUMENTS_TABLE_NAME, DatabaseHandler.MONUMENTS_KEY + " = ?", args);
    }

    public void edit(Monument monument) {
        ContentValues value = monumentToValues(monument);
        mDb.update(DatabaseHandler.MONUMENTS_TABLE_NAME, value, DatabaseHandler.MONUMENTS_KEY  + " = ?", new String[] {String.valueOf(monument.getId())});
    }

    public Monument select(long id) {
        String args[] = {String.valueOf(id)};
        Monument monument = null;
        Cursor c = mDb.query(DatabaseHandler.MONUMENTS_TABLE_NAME, DatabaseHandler.MONUMENTS_ALL_COLUMNS, DatabaseHandler.MONUMENTS_KEY + " = ?", args, "", "", "");
        if(c.moveToFirst()) {
            monument =  cursorToMonument(c);
           /* String args2[] = {String.valueOf(c.getLong(7))};
            c = mDb.query(DatabaseHandler.LOCALIZATION_TABLE_NAME, DatabaseHandler.LOCALIZATION_ALL_COLUMNS, DatabaseHandler.LOCALIZATION_KEY + " = ?", args2, "", "", "");
            if(c.moveToFirst()) {
                monument.setLocalization(new Localization(c.getLong(0), c.getDouble(1), c.getDouble(2)));
            }*/
        }
        return monument;
    }


    public Monument select(String name) {
        String args[] = {name};
        Monument monument = null;
        Cursor c = mDb.query(DatabaseHandler.MONUMENTS_TABLE_NAME, DatabaseHandler.MONUMENTS_ALL_COLUMNS, DatabaseHandler.MONUMENTS_NAME + " = ?", args, "", "", "");
        if(c.moveToFirst()) {
            monument =  cursorToMonument(c);
        }
        return monument;
    }
    public List<Monument> getAllMonuments() {
        List<Monument> monuments = new ArrayList<Monument>();

        Cursor c = mDb.query(DatabaseHandler.MONUMENTS_TABLE_NAME, DatabaseHandler.MONUMENTS_ALL_COLUMNS, null, null, null, null, null);

        while(c.moveToNext()) {
            Monument monument = cursorToMonument(c);
            String args2[] = {String.valueOf(c.getLong(7))};
            c = mDb.query(DatabaseHandler.LOCALIZATION_TABLE_NAME, DatabaseHandler.LOCALIZATION_ALL_COLUMNS, DatabaseHandler.LOCALIZATION_KEY + " = ?", args2, "", "", "");
            if(c.moveToFirst()) {
                monument.setLocalization(new Localization(c.getLong(0), c.getDouble(1), c.getDouble(2)));
            }
            monuments.add(monument);
        }

        c.close();

        return monuments;
    }

    private ContentValues monumentToValues(Monument monument) {
        ContentValues value = new ContentValues();
        value.put(DatabaseHandler.MONUMENT_DATABASE_ID, monument.getDatabaseId());
        value.put(DatabaseHandler.MONUMENTS_NAME, monument.getName());
        value.put(DatabaseHandler.MONUMENTS_PHOTO_PATH, monument.getPhotoPath());
        value.put(DatabaseHandler.MONUMENTS_DESCRIPTION, monument.getDescription());
        value.put(DatabaseHandler.MONUMENTS_YEAR, monument.getYear());
        value.put(DatabaseHandler.MONUMENTS_NAME, monument.getName());
        value.put(DatabaseHandler.MONUMENTS_NB_VISITORS, monument.getNbVisitors());
        value.put(DatabaseHandler.MONUMENTS_NB_VISITED, monument.getNbLike());
        value.put(DatabaseHandler.MONUMENTS_LIKED, monument.getLiked());
        value.put(DatabaseHandler.MONUMENTS_LATITUDE, monument.getLocalization().getLatitude());
        value.put(DatabaseHandler.MONUMENTS_LONGITUDE, monument.getLocalization().getLongitude());
        if(monument.getLocalization() != null) value.put(DatabaseHandler.MONUMENTS_LOCALISATION_KEY, monument.getLocalization().getId());
        value.put(DatabaseHandler.MONUMENTS_PHOTO_PATH_LOCAL, monument.getPhotoPathLocal());
        return value;
    }

    public long getMonumentId(Monument film) {
        long id = -1;
        String columns[] = {DatabaseHandler.MONUMENTS_KEY};
        String selectArgs = DatabaseHandler.MONUMENTS_NAME + " = ?";
        String args[] = {film.getName()};
        Cursor c = mDb.query(DatabaseHandler.MONUMENTS_TABLE_NAME, columns, selectArgs, args, "", "", "");
        if(c.moveToFirst()) {
            id = c.getLong(0);
        }
        return id;
    }
}
