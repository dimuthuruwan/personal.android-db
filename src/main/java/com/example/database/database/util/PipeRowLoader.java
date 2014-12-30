package com.example.database.database.util;

import android.content.Context;

import com.example.database.database.DBAccess;
import com.example.database.database.core.Row;
import com.example.database.database.core.Table;
import com.example.database.object.ThreadManager;

/**todo comment
 * Created by Eric Tsang on 23/12/2014.
 */
public class PipeRowLoader
{
    private final Context mContext;
    private final Table mTable;
    private final Queryable mQueryable;
    private final Callback mCallback;

    private boolean mIsRunning;
    private boolean mReloadCursorOnLoad;

    public PipeRowLoader(Context context, Table table, Queryable queryable, Callback callback)
    {
        mContext = context;
        mTable = table;
        mQueryable = queryable;
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

    public interface Queryable
    {
        public void query(DBAccess.OnRowLoadedListener listener);
    }

    private class OnLoadStartRunnable implements Runnable
    {
        @Override
        public void run()
        {
            mQueryable.query(new OnRowLoadedRunnable());
            ThreadManager.runOnMainThread(new OnLoadFinishRunnable());
        }
    }

    private class OnRowLoadedRunnable implements Runnable, DBAccess.OnRowLoadedListener, Cloneable
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
