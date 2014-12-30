package com.example.database.database.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * describes an SQLite {@code table}. has methods that can be used to manipulate
 *   the data of this table.
 */
public abstract class Table implements SQLiteWords
{
    /**
     * name of this table. used in queries involving this table.
     */
    private final String mName;

    /**
     * list of {@code ColumnFactory} instances used to produce the {@code
     *   Column} instances for the {@code Row} instances of this table.
     */
    private final HashMap<String, ColumnFactory> mColumnFactories;

    //////////////////
    // constructors //
    //////////////////

    /**
     * instantiates a {@code Table} instance.
     */
    public Table()
    {
        mName = initializeName();
        mColumnFactories = initializeColumnFactories();
    }

    //////////////////////
    // public interface //
    //////////////////////

    public final ColumnFactory getColumnFactory(String columnName)
    {
        return mColumnFactories.get(columnName);
    }

    public final String[] getColumnNames()
    {
        Set<String> columnNames = mColumnFactories.keySet();
        return columnNames.toArray(new String[columnNames.size()]);
    }

    /**
     * returns a {@code Row} instance which has the same columns as this those
     *   in this table.
     *
     * @return {@code Row} instance that is associated with this table.
     */
    public final Row makeRow()
    {
        return new Row(makeColumns());
    }

    /**
     * creates this table in the {@code db}.
     *
     * pre-conditions:
     *
     * - {@code db} must be writable.
     *
     * post-conditions:
     *
     * - the table described by this instance is created in the {@code db}.
     */
    public final synchronized String getCreateTableQuery()
    {
        // build the query
        StringBuilder q = new StringBuilder();
        q.append(Opening.CREATE_TABLE_IF_NOT_EXISTS);
        q.append(mName);
        boolean firstIteration = true;
        for(String columnName : mColumnFactories.keySet())
        {
            q.append((firstIteration) ? "(" : ",");
            q.append(columnName);
            q.append(mColumnFactories.get(columnName).getSQLiteType());
            for(Constraint constraint : mColumnFactories.get(columnName).mConstraints)
            {
                q.append(constraint);
            }
            firstIteration = false;
        }
        q.append(")");

        return q.toString();
    }

    /**
     * deletes this table from the {@code db}.
     *
     * pre-conditions:
     *
     * - {@code db} must be writable.
     *
     * post-conditions:
     *
     * - the table is deleted from {@code db}.
     */
    public final synchronized String getDropTableQuery()
    {
        return Opening.DROP_TABLE_IF_EXISTS+mName;
    }

    public final Map<String, ColumnFactory> getColumnFactories()
    {
        return new HashMap<>(mColumnFactories);
    }

    ///////////////////////
    // package interface //
    ///////////////////////

    /**
     * returns the name of this {@code Table} instance. this is the name that
     *   will be used by the database.
     *
     * @return name of this table.
     */
    abstract String initializeName();

    /**
     * returns an array of {@code ColumnFactory} instances used to produce the
     *   columns for this instance.
     *
     * must include a table with the name _ID in the returned array.
     *
     * @return array of {@code ColumnFactory} instances used to produce the
     *   columns for this instance.
     */
    abstract HashMap<String, ColumnFactory> initializeColumnFactories();

    /**
     * returns an array of {@code Column} instances associated with this table.
     *
     * @return array of {@code Column} instances associated with this table.
     */
    final Map<String, Column> makeColumns()
    {
        Map<String, Column> columns = new LinkedHashMap<>(mColumnFactories.size());
        for(String columnName : mColumnFactories.keySet())
        {
            columns.put(columnName, mColumnFactories.get(columnName).makeColumn());
        }
        return columns;
    }
}
