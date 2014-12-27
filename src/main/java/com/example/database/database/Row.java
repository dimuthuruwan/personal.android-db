package com.example.database.database;
import android.provider.BaseColumns;

import java.util.HashMap;

/**
 * a {@code Row} that has {@code Column} instances and is associated with a
 *   single {@code Table} object.
 *
 * @author Eric Tsang
 */
public class Row
{
    /**
     * {@code Table} object that this instance is associated with.
     */
    private final Table mTable;

    /**
     * {@code Column} instances associated with this instance.
     */
    private final HashMap<String, Column> mColumns;

    //////////////////
    // constructors //
    //////////////////

    /**
     * instantiates a {@code Row} for the passed {@code Table}.
     *
     * @param  table reference to a {@code Table} that instance is to be
     *   a part of.
     */
    Row(Table table, Long id)
    {
        mTable = table;
        mColumns = mTable.getColumns();
        setId(id);
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
     * returns the {@code Table} that instance is associated with.
     *
     * @return {@code Table} object that instance is associated with.
     */
    public Table getTable()
    {
        return mTable;
    }

    /**
     * returns an array of {@code Column} instances that's part of this {@code
     *   Row}.
     *
     * @return {@code Column} instances that make up instance.
     */
    public HashMap<String, Column> getColumns()
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
