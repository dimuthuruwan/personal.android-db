package com.example.database.database.core;

import android.provider.BaseColumns;

import java.util.Map;

/**
 * a {@code Row} that has {@code Column} instances and is associated with a
 *   single {@code Table} object.
 *
 * @author Eric Tsang
 */
public class Row
{
    /**
     * {@code Column} instances associated with this instance.
     */
    private final Map<String, Column> mColumns;

    //////////////////
    // constructors //
    //////////////////

    /**
     * instantiates a {@code Row} for the passed {@code Table}.
     *
     * @param  columns reference to the {@code Column} that instances that
     *   are part of this row.
     */
    public Row(Map<String, Column> columns)
    {
        mColumns = columns;
    }

    //////////////////////
    // public interface //
    //////////////////////

    /**
     * sets the if of this instance. this is set once this instance has been
     *   inserted into a table.
     *
     * @param id id number to assign to this {@code Row} instance.
     */
    public void setId(Long id)
    {
        getColumn(BaseColumns._ID).setValue((id == null) ? -1 : id);
    }

    /**
     * returns the id of this instance in the table; null if it doesn't exist in
     *   the table yet.
     *
     * @return id of this instance in the table; null if it doesn't have an id
     *   yet.
     */
    public Long getId()
    {
        long id = getColumn(BaseColumns._ID).getLong();
        return (id == -1) ? null : id;
    }

    /**
     * returns an array of {@code Column} instances that's part of this {@code
     *   Row}.
     *
     * @return {@code Column} instances that make up instance.
     */
    public Map<String, Column> getColumns()
    {
        return mColumns;
    }

    /**
     * returns the {@code Column} object associated with this {@code Row} that
     *   has the a column name that matches {@code columnName}. returns {@code
     *   null} if it doesn't exist.
     *
     * @param columnName name of desired {@code Column} object to retrieve.
     *
     * @return {@code Column} object that has the name {@code columnName}.
     */
    public Column getColumn(String columnName)
    {
        return mColumns.get(columnName);
    }
}
