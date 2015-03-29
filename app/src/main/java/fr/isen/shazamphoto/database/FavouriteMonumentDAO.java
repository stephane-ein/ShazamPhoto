package fr.isen.shazamphoto.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FavouriteMonumentDAO extends ListMonumentDAO{

    public FavouriteMonumentDAO(Context pContext) {
        super(pContext);
    }

    public void insert(long filmId) {
        ContentValues value = new ContentValues();
        value.put(DatabaseHandler.FAVOURITE_MONUMENTS_KEY, filmId);
        mDb.insert(DatabaseHandler.FAVOURITE_MONUMENTS_TABLE_NAME, null, value);
    }
    public void insert(Monument monument) {
        insert(monument.getId());
    }

    public void delete(long id) {
        // Delete the monument from the FavouriteMonumentTable
        String[] selectionArgs = {Long.toString(id)};
        mDb.delete(DatabaseHandler.FAVOURITE_MONUMENTS_TABLE_NAME, DatabaseHandler.FAVOURITE_MONUMENTS_KEY + " = ?", selectionArgs);
    }

    public void delete(Monument monument) {
        delete(monument.getId());
    }

    public Monument select(long id) {
        Monument monument = null;
        String selection = DatabaseHandler.FAVOURITE_MONUMENTS_KEY + " = ?";
        String[] selectionArgs = {Long.toString(id)};
        Cursor c = mDb.query(DatabaseHandler.FAVOURITE_MONUMENTS_TABLE_NAME, DatabaseHandler.FAVOURITE_MONUMENTS_ALL_COLUMNS, selection, selectionArgs, null, null, null);
        if(c.moveToFirst()) {
            selection = DatabaseHandler.MONUMENTS_KEY + " = ?";
            Cursor c2 = mDb.query(DatabaseHandler.MONUMENTS_TABLE_NAME, DatabaseHandler.MONUMENTS_ALL_COLUMNS, selection, selectionArgs, null, null, null);
            if(c2.moveToFirst()) {
                monument = cursorToMonument(c2);
            }
        }
        return monument;
    }

    @Override
    public List<Monument> getAllMonuments() {
        List<Monument> monuments = new ArrayList<Monument>();
        Cursor c = mDb.query(DatabaseHandler.FAVOURITE_MONUMENTS_TABLE_NAME, DatabaseHandler.FAVOURITE_MONUMENTS_ALL_COLUMNS, null, null, null, null, null);
        while(c.moveToNext()) {
            long filmId = c.getLong(0);
            System.out.println("FavoriteMonumentDAO id : "+filmId);
            String selection = DatabaseHandler.MONUMENTS_KEY + " = ?";
            String[] selectionArgs = {Long.toString(filmId)};
            Cursor c2 = mDb.query(DatabaseHandler.MONUMENTS_TABLE_NAME, DatabaseHandler.MONUMENTS_ALL_COLUMNS, selection, selectionArgs, null, null, null);
            if(c2.moveToFirst()){
                Monument monument = cursorToMonument(c2);
                monuments.add(monument);
            }
        }

        c.close();

        return monuments;
    }
}
