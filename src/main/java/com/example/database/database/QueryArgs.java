package com.example.database.database;

import android.os.CancellationSignal;

/**
 * todo comment
 * encapsulates the arguments used to perform a selection query on a {@code
 *   Table}.
 */
public class QueryArgs
{
    private boolean mDistinct;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mGroupBy;
    private String mHaving;
    private String mOrderBy;
    private String mLimit;
    private CancellationSignal mCancellationSignal;

    /**todo update this comment!!!!!!!!!
     * selects the specified rows from this instance and returns them as an
     *   array of {@code Row} instances
     *
     * @param  orderBy order used to sort the returned {@code Row[]}.
     * @param  groupBy column names to group returned rows.
     * @param  having used to filter groups
     * @param  selection row filter with "?" placeholders
     * @param  selectionArgs replaces the placeholders in {@code selection}
     */
    public QueryArgs(boolean distinct, String selection, String[] selectionArgs,
            String groupBy, String having, String orderBy, String limit,
            CancellationSignal cancellationSignal)
    {
        mDistinct = distinct;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mOrderBy = orderBy;
        mLimit = limit;
        mCancellationSignal = cancellationSignal;
    }

    public boolean getDistinct()
    {
        return mDistinct;
    }

    public String getSelection()
    {
        return mSelection;
    }

    public String[] getSelectionArgs()
    {
        return mSelectionArgs;
    }

    public String getGroupBy()
    {
        return mGroupBy;
    }

    public String getHaving()
    {
        return mHaving;
    }

    public String getOrderBy()
    {
        return mOrderBy;
    }

    public String getLimit()
    {
        return mLimit;
    }

    public CancellationSignal getCancellationSignal()
    {
        return mCancellationSignal;
    }
}
