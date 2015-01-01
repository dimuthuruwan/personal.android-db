package com.example.database.database.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.database.database.core.Row;

import java.util.ArrayList;

/**
 * subclass of the {@code BaseAdapter} class that uses a {@code PipeRowLoader}
 *   instance to interface with the database. this can be used by {@code
 *   AdapterViews}.
 */
public abstract class DBAdapter extends BaseAdapter
{
    /**
     * list of rows used for random access.
     */
    private ArrayList<Row> mData;

    /**
     * used to load rows from the database as needed.
     */
    private PipeRowLoader mRowLoader;

    /////////////////
    // constructor //
    /////////////////

    /**
     * instantiates a {@code DBAdapter} object.
     *
     * @param  context context of the application
     * @param  querable used to query the database. determines what is in this
     *   adapter; what gets displayed by observing {@code AdapterViews}.
     */
    public DBAdapter(Context context, PipeRowLoader.Queryable querable)
    {
        super();
        mData = new ArrayList<>();
        mRowLoader = new PipeRowLoader(context, querable,
                new MyRowLoaderObserver());
    }

    //////////////////////
    // public interface //
    //////////////////////

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public Row getItem(int position)
    {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return mData.get(position).getId();
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    @Override
    public void notifyDataSetChanged()
    {
        mRowLoader.loadRows();
    }

    ///////////////////////
    // private interface //
    ///////////////////////

    private class MyRowLoaderObserver implements PipeRowLoader.RowLoadEventListener
    {
        private int mRowIndex;

        @Override
        public void onLoadStart()
        {
            mRowIndex = 0;
        }

        @Override
        public void onRowLoaded(Row row)
        {
            if(mData.size() == mRowIndex)
            {
                mData.add(row);
            }
            else
            {
                mData.set(mRowIndex, row);
            }

            ++mRowIndex;

            DBAdapter.super.notifyDataSetChanged();
        }

        @Override
        public void onLoadFinish()
        {
            while(mData.size() > mRowIndex)
            {
                mData.remove(mRowIndex);
            }

            DBAdapter.super.notifyDataSetChanged();
        }
    }
}
