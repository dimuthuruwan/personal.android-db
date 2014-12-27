package com.example.database.database;

import android.accounts.OperationCanceledException;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CancellationSignal;

/**
 * Created by Eric Tsang on 21/12/2014.
 */
class SQLiteDatabaseAdapter implements Database
{
    private SQLiteDatabase mDb;

    public static Database getWritableDatabase(Context context)
    {
        return SQLiteDatabaseAdapter.wrap(DBAccess.getInstance(context).getWritableDatabase());
    }

    public static Database wrap(SQLiteDatabase db)
    {
        return new SQLiteDatabaseAdapter(db);
    }

    private SQLiteDatabaseAdapter(SQLiteDatabase db)
    {
        mDb = db;
    }

    @Override
    public void close()
    {
        if(mDb != null)
        {
            mDb.close();
            mDb = null;
        }
    }

    @Override
    public void execSQL(String sql)
    {
        mDb.execSQL(sql);
    }

    @Override
    public boolean isOpen()
    {
        return mDb.isOpen();
    }

    @Override
    public boolean isReadOnly()
    {
        return mDb.isReadOnly();
    }

    @Override
    public Cursor query(boolean distinct, String tableName, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit, CancellationSignal cancellationSignal) throws OperationCanceledException
    {
        return mDb.query(distinct, tableName, projection, selection, selectionArgs, groupBy, having, sortOrder, limit, cancellationSignal);
    }

    @Override
    public int update(String tableName, ContentValues values, String selection, String[] selectionArgs)
    {
        return mDb.update(tableName, values, selection, selectionArgs);
    }

    @Override
    public int delete(String tableName, String selection, String[] selectionArgs)
    {
        return mDb.delete(tableName, selection, selectionArgs);
    }

    @Override
    public long insert(String tableName, String nullColumnHack, ContentValues values)
    {
        return mDb.insert(tableName, nullColumnHack, values);
    }
}
