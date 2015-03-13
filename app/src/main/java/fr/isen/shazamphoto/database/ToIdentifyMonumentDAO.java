package fr.isen.shazamphoto.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by .Sylvain on 22/01/2015.
 */
public class ToIdentifyMonumentDAO extends DAOBase {
    public ToIdentifyMonumentDAO(Context pContext) {
        super(pContext);
    }

    public long insert(ToIdentifyMonument monument) {
        Cursor c = mDb.query(DatabaseHandler.LOCALIZATION_TABLE_NAME, DatabaseHandler.LOCALIZATION_ALL_COLUMNS, DatabaseHandler.LOCALIZATION_LATITUDE + " = ? AND " + DatabaseHandler.LOCALIZATION_LONGITUDE + " = ?", new String[] {String.valueOf(monument.getLocalization().getLatitude()), String.valueOf(monument.getLocalization().getLongitude())}, "", "", "");
        if(c.moveToFirst()) {
            monument.getLocalization().setId(c.getLong(0));
        }
        else {
            ContentValues value = new ContentValues();
            value.put(DatabaseHandler.LOCALIZATION_LATITUDE, monument.getLocalization().getLatitude());
            value.put(DatabaseHandler.LOCALIZATION_LONGITUDE, monument.getLocalization().getLongitude());
            monument.getLocalization().setId(mDb.insert(DatabaseHandler.LOCALIZATION_TABLE_NAME, null, value));
        }
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

    public void edit(ToIdentifyMonument monument) {
        ContentValues value = monumentToValues(monument);
        mDb.update(DatabaseHandler.MONUMENTS_TABLE_NAME, value, DatabaseHandler.MONUMENTS_KEY  + " = ?", new String[] {String.valueOf(monument.getId())});
    }

    public ToIdentifyMonument select(long id) {
        String args[] = {String.valueOf(id)};
        ToIdentifyMonument monument = null;
        Cursor c = mDb.query(DatabaseHandler.MONUMENTS_TABLE_NAME, DatabaseHandler.MONUMENTS_ALL_COLUMNS, DatabaseHandler.MONUMENTS_KEY + " = ?", args, "", "", "");
        if(c.moveToFirst()) {
            monument =  cursorToMonument(c);
            String args2[] = {String.valueOf(c.getLong(2))};
            c = mDb.query(DatabaseHandler.LOCALIZATION_TABLE_NAME, DatabaseHandler.LOCALIZATION_ALL_COLUMNS, DatabaseHandler.LOCALIZATION_KEY + " = ?", args2, "", "", "");
            if(c.moveToFirst()) {
                monument.setLocalization(new Localization(c.getLong(0), c.getDouble(1), c.getDouble(2)));
            }
        }
        return monument;
    }

    public List<ToIdentifyMonument> getAllMonuments() {
        List<ToIdentifyMonument> monuments = new ArrayList<ToIdentifyMonument>();

        Cursor c = mDb.query(DatabaseHandler.MONUMENTS_TABLE_NAME, DatabaseHandler.MONUMENTS_ALL_COLUMNS, null, null, null, null, null);

        while(c.moveToNext()) {
            ToIdentifyMonument monument = cursorToMonument(c);
            String args2[] = {String.valueOf(c.getLong(2))};
            c = mDb.query(DatabaseHandler.LOCALIZATION_TABLE_NAME, DatabaseHandler.LOCALIZATION_ALL_COLUMNS, DatabaseHandler.LOCALIZATION_KEY + " = ?", args2, "", "", "");
            if(c.moveToFirst()) {
                monument.setLocalization(new Localization(c.getLong(0), c.getDouble(1), c.getDouble(2)));
            }
            monuments.add(monument);
        }

        c.close();

        return monuments;
    }

    private ContentValues monumentToValues(ToIdentifyMonument monument) {
        ContentValues value = new ContentValues();
        value.put(DatabaseHandler.MONUMENTS_PHOTO_PATH, monument.getPhotoPath());
        value.put(DatabaseHandler.MONUMENTS_LOCALISATION_KEY, monument.getLocalization().getId());

        return value;
    }

    protected ToIdentifyMonument cursorToMonument(Cursor cursor) {
        return new ToIdentifyMonument(cursor.getLong(0), cursor.getString(1), null);
    }
}
