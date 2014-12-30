package com.example.database.database.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.database.database.DBAccess;
import com.example.database.database.core.Row;
import com.example.database.database.core.Table;

import java.util.ArrayList;

/**todo comment
 * Created by Eric Tsang on 20/12/2014.
 */
public abstract class DBAdapter extends BaseAdapter
{
    /** {@code Runnable} instance used to load {@code Cursor} objects from a {@code Table}. */
    private ArrayList<Row> mData;

    private PipeRowLoader mRowLoader;

    public DBAdapter(Context context, Table table, PipeRowLoader.Queryable querable)
    {
        super();
        mData = new ArrayList<>();
        mRowLoader = new PipeRowLoader(context, table, querable, new MyRowLoaderObserver());
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

    private class MyRowLoaderObserver implements PipeRowLoader.Callback
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
