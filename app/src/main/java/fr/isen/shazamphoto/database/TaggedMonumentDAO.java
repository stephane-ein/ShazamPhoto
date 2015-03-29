package fr.isen.shazamphoto.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TaggedMonumentDAO extends ListMonumentDAO{

    public TaggedMonumentDAO(Context pContext) {
        super(pContext);
    }

    public void insert(long filmId) {
        ContentValues value = new ContentValues();
        value.put(DatabaseHandler.VISITED_MONUMENTS_KEY, filmId);
        mDb.insert(DatabaseHandler.VISITED_MONUMENTS_TABLE_NAME, null, value);
    }
    public void insert(Monument film) {
        insert(film.getId());
    }

    public void delete(long id) {
        String[] selectionArgs = {Long.toString(id)};
        // Delete the monument from the TaggedMoumumentTable
        mDb.delete(DatabaseHandler.VISITED_MONUMENTS_TABLE_NAME, DatabaseHandler.VISITED_MONUMENTS_KEY + " = ?", new String[] {Long.toString(id)});
    }

    public void delete(Monument film) {
        delete(film.getId());
    }

    public Monument select(long id) {
        Monument monument = null;
        String selection = DatabaseHandler.VISITED_MONUMENTS_KEY + " = ?";
        String[] selectionArgs = {Long.toString(id)};
        Cursor c = mDb.query(DatabaseHandler.VISITED_MONUMENTS_TABLE_NAME, DatabaseHandler.VISITED_MONUMENTS_ALL_COLUMNS, selection, selectionArgs, null, null, null);
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
        Cursor c = mDb.query(DatabaseHandler.VISITED_MONUMENTS_TABLE_NAME, DatabaseHandler.VISITED_MONUMENTS_ALL_COLUMNS, null, null, null, null, null);
        while(c.moveToNext()) {
            long filmId = c.getLong(0);
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
