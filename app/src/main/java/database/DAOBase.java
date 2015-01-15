package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Scay on 15/01/2015.
 */
public abstract class DAOBase {
    protected final static int VERSION = 8;
    protected final static String NOM = "monument_list.db";

    protected SQLiteDatabase mDb = null;
    protected DatabaseHandler mHandler = null;

    public DAOBase(Context pContext) {
        this.mHandler = new DatabaseHandler(pContext, NOM, null, VERSION);
    }

    public void open() {
        mDb = mHandler.getWritableDatabase();
    }

    public void close() {
        mDb.close();
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }
}