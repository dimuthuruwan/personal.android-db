package com.example.database.database;

import android.os.CancellationSignal;

/**todo comment
 * provides a way to build a QueryArgs object.
 */
public class QueryArgsBuilder
{

    private boolean mDistinct = false;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mGroupBy;
    private String mHaving;
    private String mOrderBy;
    private String mLimit;
    private CancellationSignal mCancellationSignal;

    public QueryArgsBuilder setDistinct(boolean distinct)
    {
        mDistinct = distinct;
        return this;
    }

    public QueryArgsBuilder setSelection(String selection)
    {
        mSelection = selection;
        return this;
    }

    public QueryArgsBuilder setSelectionArgs(String ... selectionArgs)
    {
        mSelectionArgs = selectionArgs;
        return this;
    }

    public QueryArgsBuilder setGroupBy(String groupBy)
    {
        mGroupBy = groupBy;
        return this;
    }

    public QueryArgsBuilder setHaving(String having)
    {
        mHaving = having;
        return this;
    }

    public QueryArgsBuilder setOrderBy(String orderBy)
    {
        mOrderBy = orderBy;
        return this;
    }

    public QueryArgsBuilder setLimit(String limit)
    {
        mLimit = limit;
        return this;
    }

    public QueryArgsBuilder setCancellationSignal(
            CancellationSignal cancellationSignal)
    {
        mCancellationSignal = cancellationSignal;
        return this;
    }

    public QueryArgs getQueryArgs()
    {
        QueryArgs qa = new QueryArgs(mDistinct, mSelection, mSelectionArgs,
                mGroupBy, mHaving, mOrderBy, mLimit, mCancellationSignal);
        return qa;
    }
}
