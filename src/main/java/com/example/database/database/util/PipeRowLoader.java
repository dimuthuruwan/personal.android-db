package com.example.database.database.util;

import android.content.Context;

import com.example.database.database.DBAccess;
import com.example.database.database.core.Row;
import com.example.database.object.ThreadManager;

/**
 * loads rows from the database when requested. this object will ignore multiple
 *   requests to load rows from the database. if it is requested to load rows
 *   from the database while it is already loading the rows from the database,
 *   it will wait until the current operation finished, then load the rows
 *   again.
 */
public class PipeRowLoader
{
    /**
     * context of the application.
     */
    private final Context mContext;

    /**
     * implementation of the {@code Queryable} interface used to query the
     *   database. when the object is requested to load the rows from the
     *   database.
     */
    private final Queryable mQueryable;

    /**
     * implementation of the {@code RowLoadEventListener} interface. methods of
     *   the instance will be invoked as this object loads rows from the
     *   database.
     */
    private final RowLoadEventListener mCallback;

    /**
     * true if this instance is currently loading rows from the database; false
     *   otherwise.
     */
    private boolean mIsRunning;

    /**
     * true if another request to load rows from the database is made while this
     *   instance is already loading rows from the database. this will trigger
     *   the object to load the rows from the database again when it finishes
     *   the current load operation.
     */
    private boolean mReloadCursorOnLoad;

    /////////////////
    // constructor //
    /////////////////

    /**
     * instantiates a {@code PipeRowLoader} instance.
     *
     * @param  context {@code context} of the application.
     * @param  queryable implementation of the {@code Queryable} interface used
     *   to load rows from the database.
     * @param  callback implementation of the {@code RowLoadEventListener}
     *   interface. methods of the instance will be invoked as rows are loaded
     *   from the database.
     */
    public PipeRowLoader(Context context, Queryable queryable,
                         RowLoadEventListener callback)
    {
        mContext = context;
        mQueryable = queryable;
        mCallback = callback;

        mIsRunning = false;
        mReloadCursorOnLoad = false;
    }

    //////////////////////
    // public interface //
    //////////////////////

    /**
     * interface with callbacks. these methods will be invoked by the {@code
     *   PipeRowLoader} as rows are loaded from the database.
     */
    public interface RowLoadEventListener
    {
        /**
         * invoked when a loading request is made.
         */
        public void onLoadStart();

        /**
         * invoked as rows are loaded from the database.
         */
        public void onRowLoaded(Row row);

        /**
         * invoked after the last row has been loaded and parsed out of the
         *   database.
         */
        public void onLoadFinish();
    }

    /**
     * interface with callbacks. used by the {@code PipeRowLoader} to query the
     *   database when requested.
     */
    public interface Queryable
    {
        /**
         * method invoked by the {@code PipeRowLoader} object to query the
         *   database. the passed {@code listener} must be passed into the
         *   database query method of {@code DBAccess}.
         *
         * @param listener object to be passed to the database query method.
         */
        public void query(DBAccess.OnRowLoadedListener listener);
    }

    /**
     * used to request the {@code PipeRowLoader} instance to load rows from the
     *   database. when this method is invoked while rows are currently being
     *   loaded from the database, it will wait until the current operation is
     *   finished before loading rows from the database again. any subsequent
     *   requests made while rows are being loaded will have no effect.
     */
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

    ///////////////////////
    // private interface //
    ///////////////////////

    private class OnLoadStartRunnable implements Runnable
    {
        @Override
        public void run()
        {
            mQueryable.query(new OnRowLoadedRunnable());
            ThreadManager.runOnMainThread(new OnLoadFinishRunnable());
            mIsRunning = false;
            if(mReloadCursorOnLoad)
            {
                loadRows();
            }
        }
    }

    private class OnRowLoadedRunnable implements Runnable, DBAccess.OnRowLoadedListener, Cloneable
    {
        private Row mLoadedRow;

        @Override
        public void run()
        {
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
            mCallback.onLoadFinish();
        }
    }
}
