package com.example.database.database.util;

import android.content.Context;

import com.example.database.database.Database;
import com.example.database.database.QueryArgs;
import com.example.database.database.Row;
import com.example.database.database.Table;
import com.example.database.object.ThreadManager;

/**todo comment
 * Created by Eric Tsang on 20/12/2014.
 */
public class RowLoader
{
    private final Context mContext;
    private final Table mTable;
    private final QueryArgs mQueryArgs;
    private final Callback mCallback;

    private Row[] mRows;
    private boolean mIsRunning;
    private boolean mReloadCursorOnLoad;

    public RowLoader(Context context, Table table, QueryArgs queryArgs, Callback callback)
    {
        mContext = context;
        mTable = table;
        mQueryArgs = queryArgs;
        mCallback = callback;
        mIsRunning = false;
        mReloadCursorOnLoad = false;
    }

    public interface Callback
    {
        public void onRowsLoaded(Row[] rows);
    }

    public synchronized void loadRows()
    {
        if(!mIsRunning)
        {
            mIsRunning = true;
            mReloadCursorOnLoad = false;
            ThreadManager.runOnWorkerThread(new OnLoadStartRunnable());
        }
        else
        {
            mReloadCursorOnLoad = true;
        }
    }

    private class OnLoadStartRunnable implements Runnable
    {
        @Override
        public void run()
        {
            Database db = mTable.getWritableDatabase(mContext);
            mRows = mTable.selectRows(db, mQueryArgs, null);
            ThreadManager.runOnMainThread(new OnLoadFinishRunnable());
            db.close();
        }
    }

    private class OnLoadFinishRunnable implements Runnable
    {
        @Override
        public void run()
        {
            mIsRunning = false;
            if(mReloadCursorOnLoad)
            {
                loadRows();
            }
            mCallback.onRowsLoaded(mRows);
        }
    }
}
