package com.example.database.database.util;

import android.content.Context;

import com.example.database.database.Database;
import com.example.database.database.QueryArgs;
import com.example.database.database.Row;
import com.example.database.database.Table;
import com.example.database.object.ThreadManager;

/**todo comment
 * Created by Eric Tsang on 23/12/2014.
 */
public class PipeRowLoader
{
    private final Context mContext;
    private final Table mTable;
    private final QueryArgs mQueryArgs;
    private final Callback mCallback;

    private boolean mIsRunning;
    private boolean mReloadCursorOnLoad;

    public PipeRowLoader(Context context, Table table, QueryArgs queryArgs, Callback callback)
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
        public void onLoadStart();
        public void onRowLoaded(Row row);
        public void onLoadFinish();
    }

    public synchronized void loadRows()
    {
        if(!mIsRunning)
        {
            mIsRunning = true;
            mReloadCursorOnLoad = false;
            mCallback.onLoadStart();
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
            mTable.selectRows(db, mQueryArgs, new OnRowLoadedRunnable());
            ThreadManager.runOnMainThread(new OnLoadFinishRunnable());
            db.close();
        }
    }

    private class OnRowLoadedRunnable implements Runnable, Table.OnRowLoadedListener, Cloneable
    {
        private Row mLoadedRow;

        @Override
        public void run()
        {
            mIsRunning = false;
            if(mReloadCursorOnLoad)
            {
                loadRows();
            }
            mCallback.onRowLoaded(mLoadedRow);
        }

        @Override
        public void onRowLoaded(Row row)
        {
            mLoadedRow = row;
            try
            {
                ThreadManager.runOnMainThread((OnRowLoadedRunnable) this.clone());
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
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
            mCallback.onLoadFinish();
        }
    }
}
