package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    //				****************table FILM*******************
    //listes des colomnes de la table monument
    public static final String MONUMENT_KEY = "id";
    public static final String MONUMENT_NAME = "name";
    public static final String MANUMENT_YEAR = "year";
    public static final String MONUMENT_DESCRIPTION = "description";
    //tableau content toutes les colomnes de la table
    public static final String FILM_ALL_COLUMNS[] = {DatabaseHandler.MONUMENT_KEY, DatabaseHandler.MONUMENT_NAME, MANUMENT_YEAR, MONUMENT_DESCRIPTION};
    public static final String FILM_TABLE_NAME = "monument";
    //script de création de la table
    public static final String FILM_TABLE_CREATE =
            "CREATE TABLE " + FILM_TABLE_NAME + " (" +
                    MONUMENT_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MONUMENT_NAME + " TEXT NOT NULL, " +
                    MANUMENT_YEAR + " TEXT, " +
                    MONUMENT_DESCRIPTION + " TEXT, ";
    //script de suppression
    public static final String FILM_TABLE_DROP = "DROP TABLE IF EXISTS " + FILM_TABLE_NAME + ";";

    //		****************table FAVOURITEFILMS*******************
    public static final String FAVOURITE_FILM_KEY = "filmId";
    public static final String FAVOURITE_ALL_COLUMNS[] = {DatabaseHandler.FAVOURITE_FILM_KEY};
    public static final String FAVOURITE_TABLE_NAME = "favouriteFilms";
    public static final String FAVOURITE_TABLE_CREATE =
            "CREATE TABLE " + FAVOURITE_TABLE_NAME + " (" +
                    DatabaseHandler.FAVOURITE_FILM_KEY + " INTEGER," +
                    "FOREIGN KEY(" + DatabaseHandler.FAVOURITE_FILM_KEY + ") REFERENCES " + DatabaseHandler.FILM_TABLE_NAME + "(" + DatabaseHandler.MONUMENT_KEY + "));";
    public static final String FAVOURITE_TABLE_DROP = "DROP TABLE IF EXISTS " + FAVOURITE_TABLE_NAME + ";";

    //		****************table MONUMENTS VISITED*******************
    public static final String TOSEE_FILM_KEY = "filmId";
    public static final String TOSEE_ALL_COLUMNS[] = {DatabaseHandler.TOSEE_FILM_KEY};
    public static final String TOSEE_TABLE_NAME = "toSeeFilms";
    public static final String TOSEE_TABLE_CREATE =
            "CREATE TABLE " + TOSEE_TABLE_NAME + " (" +
                    DatabaseHandler.TOSEE_FILM_KEY + " INTEGER," +
                    "FOREIGN KEY(" + DatabaseHandler.TOSEE_FILM_KEY + ") REFERENCES " + DatabaseHandler.FILM_TABLE_NAME + "(" + DatabaseHandler.MONUMENT_KEY + "));";
    public static final String TOSEE_TABLE_DROP = "DROP TABLE IF EXISTS " + TOSEE_TABLE_NAME + ";";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //cr�ation de tables : ex�cution des diff�rents scripts correspondant � chaque table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FILM_TABLE_CREATE);
        db.execSQL(FAVOURITE_TABLE_CREATE);
        db.execSQL(TOSEE_TABLE_CREATE);
    }

    //mise � jour des tables : suppression des tables en executant les scripts correspondant
    //puis recr�ation avec la fonction ci-dessus
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(FILM_TABLE_DROP);
        db.execSQL(FAVOURITE_TABLE_DROP);
        db.execSQL(TOSEE_TABLE_DROP);
        onCreate(db);
    }
}