package com.example.database.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *todo implement
 * todo comment
 *
 * @author Eric Tsang
 */
class DBAccess extends SQLiteOpenHelper
{
    /**
     * if DB schema changes, you must implement an onUpgrade,
     * and change the db version.
     * DB version >= 1.
     */
    public static final int DATABASE_VERSION = 1;

    /** name of file used to save the database data */
    public static final String DATABASE_NAME = "EverythingDatabase.db";

    public static DBAccess sInstance;

    //////////////////
    // constructors //
    //////////////////

    private DBAccess(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //////////////////////
    // public interface //
    //////////////////////

    public static DBAccess getInstance(Context context)
    {
        if(sInstance == null)
        {
            sInstance = new DBAccess(context);
        }
        return sInstance;
    }

    /////////////////////////////////////
    // SQLiteOpenHelper implementation //
    /////////////////////////////////////

    @Override
    public void onCreate(SQLiteDatabase db) {
        NamesTable.sInstance.deleteTable(SQLiteDatabaseAdapter.wrap(db));
        NamesTable.sInstance.createTable(SQLiteDatabaseAdapter.wrap(db));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
        switch(oldVersion) {
            case 1:
                NamesTable.sInstance.deleteTable(SQLiteDatabaseAdapter.wrap(db));
                onCreate(db);
            case 2:
                NamesTable.sInstance.deleteTable(SQLiteDatabaseAdapter.wrap(db));
                onCreate(db);
            case 3:
                NamesTable.sInstance.deleteTable(SQLiteDatabaseAdapter.wrap(db));
                onCreate(db);
            case 4:
                NamesTable.sInstance.deleteTable(SQLiteDatabaseAdapter.wrap(db));
                onCreate(db);
            case 5:
                NamesTable.sInstance.deleteTable(SQLiteDatabaseAdapter.wrap(db));
                onCreate(db);
            case 6:
                NamesTable.sInstance.deleteTable(SQLiteDatabaseAdapter.wrap(db));
                onCreate(db);
            case 7:
                NamesTable.sInstance.deleteTable(SQLiteDatabaseAdapter.wrap(db));
                onCreate(db);
        }
        */
    }
}
