package com.example.database.database;

import android.accounts.OperationCanceledException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;

/**
 * Created by Eric Tsang on 21/12/2014.
 */
class ContentProviderAdapter implements Database
{
    private ContentResolver mDb;

    public static Database getWritableDatabase(Context context)
    {
        return ContentProviderAdapter.wrap(context.getContentResolver());
    }

    public static Database wrap(ContentResolver cr)
    {
        return new ContentProviderAdapter(cr);
    }

    private ContentProviderAdapter(ContentResolver cr)
    {
        mDb = cr;
    }

    @Override
    public void close()
    {
        mDb = null;
    }

    @Override
    public void execSQL(String sql)
    {
        throw new IllegalStateException("cannot invoke method in this implementation.");
    }

    @Override
    public boolean isOpen()
    {
        return mDb != null;
    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }

    @Override
    public Cursor query(boolean distinct, String tableName, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit, CancellationSignal cancellationSignal) throws OperationCanceledException
    {
        return mDb.query(Uri.parse(tableName), projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    @Override
    public int update(String tableName, ContentValues values, String selection, String[] selectionArgs)
    {
        return mDb.update(Uri.parse(tableName), values, selection, selectionArgs);
    }

    @Override
    public int delete(String tableName, String selection, String[] selectionArgs)
    {
        return mDb.delete(Uri.parse(tableName), selection, selectionArgs);
    }

    @Override
    public long insert(String tableName, String nullColumnHack, ContentValues values)
    {
        mDb.insert(Uri.parse(tableName), values);
        return -1;
    }
}
