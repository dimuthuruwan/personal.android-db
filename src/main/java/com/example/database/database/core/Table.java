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
    //////////////////////
    // public interface //
    //////////////////////

    public final ColumnFactory getColumnFactory(String columnName)
    {
        return getColumnFactories().get(columnName);
    }

    public final String[] getColumnNames()
    {
        Set<String> columnNames = getColumnFactories().keySet();
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
        q.append(getName());
        boolean firstIteration = true;
        for(String columnName : getColumnFactories().keySet())
        {
            q.append((firstIteration) ? "(" : ",");
            q.append(columnName);
            q.append(getColumnFactories().get(columnName).getSQLiteType());
            for(Constraint constraint : getColumnFactories().get(columnName).mConstraints)
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
        return Opening.DROP_TABLE_IF_EXISTS+getName().toString();
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
    abstract Object getName();

    /**
     * returns an array of {@code ColumnFactory} instances used to produce the
     *   columns for this instance.
     *
     * must include a table with the name _ID in the returned array.
     *
     * @return array of {@code ColumnFactory} instances used to produce the
     *   columns for this instance.
     */
    abstract HashMap<String, ColumnFactory> getColumnFactories();

    /**
     * returns an array of {@code Column} instances associated with this table.
     *
     * @return array of {@code Column} instances associated with this table.
     */
    final Map<String, Column> makeColumns()
    {
        Map<String, Column> columns = new LinkedHashMap<>(getColumnFactories().size());
        for(String columnName : getColumnFactories().keySet())
        {
            columns.put(columnName, getColumnFactories().get(columnName).makeColumn());
        }
        return columns;
    }
}
