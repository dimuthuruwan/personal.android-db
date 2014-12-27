package com.example.database.database;

import android.accounts.OperationCanceledException;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.CancellationSignal;

/** todo comment
 * Created by Eric Tsang on 21/12/2014.
 */
public interface Database
{
    public void close();
    public void execSQL(String sql);
    public boolean isOpen();
    public boolean isReadOnly();
    public Cursor query(boolean distinct, String tableName, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit, CancellationSignal cancellationSignal) throws OperationCanceledException;
    public int update(String tableName, ContentValues values, String selection, String[] selectionArgs);
    public int delete(String tableName, String selection, String[] selectionArgs);
    public long insert(String tableName, String nullColumnHack, ContentValues values);
}
