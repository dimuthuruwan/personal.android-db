package com.example.database.database.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.example.database.database.core.DBWords.Opening;
import com.example.database.database.core.DBWords.Constraint;

/**
 * describes an SQLite {@code table}. has methods that can be used to manipulate
 *   the data of this table.
 */
public abstract class Table
{

    //////////////////////
    // public interface //
    //////////////////////

    /**
     * returns the name of this {@code Table} instance. this is the name that
     *   will be used by the database.
     *
     * @return name of this table.
     */
    public abstract Object getName();

    /**
     * returns a reference to a map of {@code ColumnFactory} instances used to
     *   produce the columns for this {@code Table} object.
     *
     * must include a table with the name _ID in the returned map.
     *
     * @return array of {@code ColumnFactory} instances used to produce the
     *   columns for this instance.
     */
    public abstract HashMap<String, ColumnFactory> getColumnFactories();

    /**
     * returns a reference to the {@code ColumnFactory} object with the name
     *   {@code columnName}.
     *
     * @param  columnName name of the {@code ColumnFactory} object to return.
     *
     * @return reference to the {@code ColumnFactory} object with the name
     *   {@code ColumnName}.
     */
    public final ColumnFactory getColumnFactory(String columnName)
    {
        return getColumnFactories().get(columnName);
    }

    /**
     * returns an array of column names of the columns in this table.
     *
     * @return array of column names of the columns in this table.
     */
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
     * returns the query that can be used to create this table in a database.
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
     * returns the query that can be used to remove this table from a database.
     */
    public final synchronized String getDropTableQuery()
    {
        return Opening.DROP_TABLE_IF_EXISTS+getName().toString();
    }

    ///////////////////////
    // package interface //
    ///////////////////////

    /**
     * returns a map of {@code Column} instances associated with this table.
     *
     * @return map of {@code Column} instances associated with this table.
     */
    private Map<String, Column> makeColumns()
    {
        Map<String, Column> columns = new LinkedHashMap<>(getColumnFactories().size());
        for(String columnName : getColumnFactories().keySet())
        {
            columns.put(columnName, getColumnFactories().get(columnName).makeColumn());
        }
        return columns;
    }
}
