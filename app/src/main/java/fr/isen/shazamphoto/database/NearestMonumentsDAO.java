package fr.isen.shazamphoto.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public class NearestMonumentsDAO extends ShazamDAO{

    public NearestMonumentsDAO(Context pContext) {
        super(pContext);
    }

    public void insert(long monumentId, long nearestMonumentId) {
        ContentValues value = new ContentValues();
        value.put(DatabaseHandler.NEAREST_MONUMENTS_MONUMENT_KEY, monumentId);
        value.put(DatabaseHandler.NEAREST_MONUMENTS_NEAREST_MONUMENT_KEY, nearestMonumentId);
        mDb.insert(DatabaseHandler.NEAREST_MONUMENTS_TABLE_NAME, null, value);
    }

    public void delete(long id) {
        String[] args = {Long.toString(id)};
        mDb.delete(DatabaseHandler.NEAREST_MONUMENTS_TABLE_NAME,
                DatabaseHandler.NEAREST_MONUMENTS_MONUMENT_KEY + " = ?", args);
    }

    public ArrayList<Monument> getNearestMonuments(long id){
        ArrayList<Monument> monuments = new ArrayList<>();

        String args[] = {String.valueOf(id)};
        Cursor c = mDb.query(DatabaseHandler.NEAREST_MONUMENTS_TABLE_NAME,
                DatabaseHandler.NEAREST_MONUMENTS_ALL_COLUMNS,
                DatabaseHandler.NEAREST_MONUMENTS_MONUMENT_KEY + " = ?", args, "", "", "");

        while(c.moveToNext()) {
            long idNearestMonument = c.getLong(2);

            String argsM[] = {String.valueOf(idNearestMonument)};
            Monument monument = null;
            Cursor cM = mDb.query(DatabaseHandler.MONUMENTS_TABLE_NAME,
                    DatabaseHandler.MONUMENTS_ALL_COLUMNS, DatabaseHandler.MONUMENTS_KEY + " = ?",
                    argsM, "", "", "");
            if(cM.moveToFirst()) {
                monument =  cursorToMonument(cM);
                String args2[] = {String.valueOf(cM.getLong(7))};
                cM = mDb.query(DatabaseHandler.LOCALIZATION_TABLE_NAME,
                        DatabaseHandler.LOCALIZATION_ALL_COLUMNS,
                        DatabaseHandler.LOCALIZATION_KEY + " = ?", args2, "", "", "");
                if(cM.moveToFirst()) {
                    monument.setLocalization(new Localization(cM.getLong(0), cM.getDouble(1),
                            cM.getDouble(2)));
                }
            }
            monuments.add(monument);
        }

        return monuments;
    }
}
