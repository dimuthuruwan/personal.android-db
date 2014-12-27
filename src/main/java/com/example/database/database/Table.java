package com.example.database.database;

import android.accounts.OperationCanceledException;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * describes an SQLite {@code table}. has methods that can be used to manipulate
 *   the data of this table.
 */
public abstract class Table implements SQLite
{
    /**
     * name of this table. used in queries involving this table.
     */
    public final String mName;

    /**
     * list of {@code ColumnFactory} instances used to produce the {@code
     *   Column} instances for the {@code Row} instances of this table.
     */
    private final ColumnFactory[] mColumnFactories;

    //////////////////
    // constructors //
    //////////////////

    /**
     * instantiates a {@code Table} instance.
     */
    public Table()
    {
        mName = getName();
        mColumnFactories = getColumnFactories();
    }

    //////////////////////
    // public interface //
    //////////////////////

    public interface OnRowLoadedListener
    {
        public void onRowLoaded(Row r);
    }

    /**
     * returns a {@code Row} instance which has the same columns as this those
     *   in this table.
     *
     * @return {@code Row} instance that is associated with this table.
     */
    public final Row makeRow()
    {
        return new Row(this, null);
    }

    /**
     * returns the {@code Database} object that this table resides in.
     *
     * @param context application {@code context} used to get the {@code
     *   Database} object
     *
     * @return {@code Database} object that this table resides in.
     */
    public abstract Database getWritableDatabase(Context context);

    /**
     * selects the specified rows from this instance and returns them as an
     *   array of {@code Row} instances
     *
     * pre-conditions:
     *
     * - {@code db} must be open.
     *
     * @param  db database to perform the query on.
     * @param  qa has parameters used to perform the selection query.
     *
     * @return array of {@code Row} instances returned by this instance.
     */
    public synchronized final Row[] selectRows(Database db, QueryArgs qa,
            OnRowLoadedListener listener)
    {
        // validate preconditions
        if(!db.isOpen())
            throw new IllegalArgumentException("{@code db} must be open.");

        // specify which columns should be returned from the query
        String[] projection = new String[mColumnFactories.length];
        for(int i = 0; i < mColumnFactories.length; ++i)
        {
            projection[i] = mColumnFactories[i].getName();
        }

        try
        {
            // do the database query
            Cursor c = db.query(
                    qa.getDistinct(),           // should rows be distinct or not
                    mName,                      // The table to query
                    projection,                 // The columns to return
                    qa.getSelection(),          // The columns for the WHERE clause
                    qa.getSelectionArgs(),      // The values for the WHERE clause
                    qa.getGroupBy(),            // don't group the rows
                    qa.getHaving(),             // don't filter by row groups
                    qa.getOrderBy(),            // The sort order
                    qa.getLimit(),              // maximum number of entries to retrieve
                    qa.getCancellationSignal()  // cancellation signal for the query
            );

            // extract data from query and return it
            Row[] rows = new Row[c.getCount()];
            c.moveToFirst();
            for(int i = 0; !c.isAfterLast(); ++i)
            {
                // create the row instance, and inject column data
                rows[i] = new Row(this, c.getLong(c.getColumnIndex(BaseColumns._ID)));
                HashMap<String, Column> columns = rows[i].getColumns();
                for(String columnName : columns.keySet())
                {
                    get(c, columns.get(columnName));
                }
                c.moveToNext();

                // if a callback listener was passed, invoke it
                if(listener != null)
                {
                    listener.onRowLoaded(rows[i]);
                }
            }
            return rows;
        }
        catch(OperationCanceledException e)
        {
            return new Row[0];
        }
    }


    /**
     * inserts or updated the {@code rows} into the {@code db}.
     *
     * pre-conditions:
     *
     * - {@code db} must be writable.
     *
     * post-conditions:
     *
     * - all {@code rows} are inserted or updated in the {@code db} depending if
     *   the {@code row} instance had an id or not.
     *
     * @param db database to insert or update {@code rows}.
     * @param rows {@code rows} to isert into or update in the {@code db}.
     */
    public synchronized final void insertOrUpdateRows(Database db, Row ... rows)
    {
        ArrayList<Row> rowsToInsert = new ArrayList<>();
        ArrayList<Row> rowsToUpdate = new ArrayList<>();

        for(Row row : rows)
        {
            if(row.getId() == null)
            {
                rowsToInsert.add(row);
            }
            else
            {
                rowsToUpdate.add(row);
            }
        }

        insertRows(db, rowsToInsert.toArray(new Row[rowsToInsert.size()]));
        updateRows(db, rowsToUpdate.toArray(new Row[rowsToUpdate.size()]));
    }

    /**
     * inserts all passed {@code rows} into the {@code db}
     *
     * pre-conditions:
     *
     * - {@code db} instance must be writable.
     *
     * - all {@code rows} must not have an id.
     *
     * post-conditions:
     *
     * - all {@code rows} are inserted into the {@code db}.
     *
     * @param db database to insert {@code rows} into
     * @param rows {@code rows} to insert into the {@code db}
     */
    public synchronized final void insertRows(Database db, Row ... rows)
    {
        // do the insert
        for(Row row : rows)
        {
            insertRow(db, row);
        }
    }

    /**
     * updates all passed {@code rows} in the {@code db}
     *
     * pre-conditions:
     *
     * - {@code db} instance must be writable.
     *
     * - all {@code rows} must have an id.
     *
     * post-conditions:
     *
     * - all {@code rows} in the {@code db} are updated.
     *
     * @param db database to update
     * @param rows {@code rows} to update in the {@code db}
     */
    public synchronized final int updateRows(Database db, Row ... rows)
    {
        int rowsAffected = 0;
        for(Row row : rows)
        {
            // get columns
            Set<String> columnNames = row.getColumns().keySet();
            Column[] columns = new Column[columnNames.size()];
            int i = 0;
            for(String columnName : columnNames)
            {
                columns[i++] = row.getColumns().get(columnName);
            }

            // build query args
            QueryArgs qa = new QueryArgsBuilder()
                    .setSelection(BaseColumns._ID+"=?")
                    .setSelectionArgs(String.valueOf(row.getId()))
                    .getQueryArgs();

            // do the update
            rowsAffected += updateRows(db, qa, columns);
        }
        return rowsAffected;
    }

    /**
     * updates the passed {@code row} in the {@code db}.
     *
     * pre-conditions:
     *
     * - {@code db} instance must be writable.
     *
     * post-conditions:
     *
     * - row in the database described by {@code row} is updated.
     *
     * @param db writable {@code SQLiteDatabase} instance to do the update on.
     * @param qa describes which row in the {@code db} to update. only the {@code selection} and {@code selectionArgs} parameters are used.
     * @param columns describes which values to update, and what to update them to in selected rows it to.
     */
    public synchronized final int updateRows(Database db, QueryArgs qa, Column ... columns)
    {
        // validate preconditions
        if(!db.isOpen() && !db.isReadOnly())
            throw new IllegalArgumentException("{@code db} instance must be "
                    +"writable.");

        // create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        for(Column column : columns)
        {
            put(values, column);
        }

        // do the update
        return db.update(
                mName,                  // name of the table being updated
                values,                 // new values
                qa.getSelection(),      // where clause
                qa.getSelectionArgs()); // where clause parameters
    }

    /**
     * deletes the {@code row} from the {@code db}.
     *
     * pre-conditions:
     *
     * - {@code db} must be a writable database.
     *
     * - all {@code rows} must have {@code non-null} ids.
     *
     * post-conditions:
     *
     * - passed {@code rows} are deleted from {@code db}.
     *
     * @param db instance to delete {@code rows} from.
     * @param rows list of {@code rows} to delete from {@code db}.
     */
    public synchronized final void deleteRows(Database db, Row ... rows)
    {
        // validate preconditions
        if(!db.isOpen() && !db.isReadOnly())
            throw new IllegalArgumentException("{@code db} instance must be "
                    +"writable.");

        // define "where" part of query; build a list of "?" placeholders
        StringBuilder selection = new StringBuilder();
        selection.append(BaseColumns._ID);
        selection.append(" IN (");
        for(int i = 0; i < rows.length; ++i)
        {
            selection.append((i == 0) ? "?" : ",?");
        }
        selection.append(")");

        // specify arguments in placeholder order (while validating pre-
        // conditions)
        String[] selectionArgs = new String[rows.length];
        for (int i = 0; i < rows.length; ++i) {
            if(rows[i].getId() == null)
            {
                throw new IllegalArgumentException("all {@code rows} must have "
                        +"{@code non-null} ids.");
            }
            selectionArgs[i] = String.valueOf(rows[i].getId());
        }

        // execute the SQL delete statement
        deleteRows(
                db,                         // name of table to delete from
                new QueryArgsBuilder()      // delete command arguments
                        .setSelection(selection.toString())
                        .setSelectionArgs(selectionArgs)
                        .getQueryArgs());
    }

    /**
     * deletes the {@code row} from the {@code db}.
     *
     * pre-conditions:
     *
     * - {@code db} must be a writable database.
     *
     * post-conditions:
     *
     * - specified data is deleted from {@code db}.
     *
     * @param db instance to delete {@code rows} from.
     * @param qa arguments used for the delete command. only the {@code selection}, and {@code selectionArgs} parameters are used.
     */
    public synchronized final void deleteRows(Database db, QueryArgs qa)
    {
        // validate preconditions
        if(!db.isOpen() && !db.isReadOnly())
            throw new IllegalArgumentException("{@code db} instance must be "
                    +"writable.");

        // execute the SQL delete statement
        db.delete(
                mName,                  // name of table to delete from
                qa.getSelection(),      // where clause with "?" placeholders
                qa.getSelectionArgs()); // replaces "?" placeholders
    }

    /////////////////////////
    // protected interface //
    /////////////////////////

    /**
     * returns the name of this {@code Table} instance. this is the name that
     *   will be used by the database.
     *
     * @return name of this table.
     */
    protected abstract String getName();

    /**
     * returns an array of {@code ColumnFactory} instances used to produce the
     *   columns for this instance.
     *
     * must include a table with the name _ID in the returned array.
     *
     * @return array of {@code ColumnFactory} instances used to produce the
     *   columns for this instance.
     */
    protected abstract ColumnFactory[] getColumnFactories();

    ///////////////////////
    // package interface //
    ///////////////////////

    /**
     * returns an array of {@code Column} instances associated with this table.
     *
     * @return array of {@code Column} instances associated with this table.
     */
    final HashMap<String, Column> getColumns()
    {
        int size = mColumnFactories.length;
        HashMap<String, Column> columns = new HashMap<>(size);
        for(ColumnFactory columnFactory : mColumnFactories)
        {
            columns.put(columnFactory.getName(), columnFactory.makeColumn());
        }
        return columns;
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
     *
     * @param db database to create the table in.
     */
    final synchronized void createTable(Database db)
    {
        // build the query
        StringBuilder q = new StringBuilder();
        q.append(Opening.CREATE_TABLE_IF_NOT_EXISTS);
        q.append(mName);
        for(int i = 0; i < mColumnFactories.length; ++i)
        {
            q.append((i == 0) ? "(" : ",");
            q.append(mColumnFactories[i].getName());
            q.append(mColumnFactories[i].getSQLiteType());
            for(Constraint constraint : mColumnFactories[i].mConstraints)
            {
                q.append(constraint);
            }
        }
        q.append(")");

        // execute the query
        db.execSQL(q.toString());
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
     *
     * @param db database to delete the table from.
     */
    final synchronized void deleteTable(Database db)
    {
        db.execSQL(Opening.DROP_TABLE_IF_EXISTS+mName);
    }

    ///////////////////////
    // private interface //
    ///////////////////////

    /**
     * inserts the passed {@code row} in the {@code db}.
     *
     * pre-conditions:
     *
     * - {@code db} instance must be writable.
     *
     * - id member of the {@code row} must be {@code null}.
     *
     * post-conditions:
     *
     * - id member of the {@code row} is updated accordingly.
     *
     * @param db writable {@code SQLiteDatabase} instance to do the insert on.
     * @param row instance to be inserted into the database.
     */
    private void insertRow(Database db, Row row)
    {
        // validate preconditions
        if(!db.isOpen() && !db.isReadOnly())
            throw new IllegalArgumentException("{@code db} instance must be "
                    +"writable.");
        if(row.getId() != null)
            throw new IllegalArgumentException("id member of the {@code row} "
                    +"must be {@code null}");

        // create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        HashMap<String, Column> columns = row.getColumns();
        for(String columnName : columns.keySet())
        {
            if(!columnName.equals(BaseColumns._ID))
            {
                put(values, columns.get(columnName));
            }
        }

        // insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(
                mName,      // table to insert info into
                null,       // null column hack
                values);    // insertion values

        // set id of DAO object
        row.setId(newRowId);
    }

    /**
     * puts the values from {@code column} into {@code values} with the name of
     *   {@code column} as the key.
     *
     * @param values {@code ContentValues} instance to put the key-value pair
     *   from {@code column} into.
     * @param column specifies the key and value to put into {@code values}.
     */
    private static void put(ContentValues values, Column column)
    {
        switch(column.getJavaType())
        {

            case BOOLEAN:
            values.put(column.getName(), column.getBoolean());
            break;

            case BYTES:
            values.put(column.getName(), column.getBytes());
            break;

            case DOUBLE:
            values.put(column.getName(), column.getDouble());
            break;

            case FLOAT:
            values.put(column.getName(), column.getFloat());
            break;

            case INT:
            values.put(column.getName(), column.getInt());
            break;

            case LONG:
            values.put(column.getName(), column.getLong());
            break;

            case SHORT:
            values.put(column.getName(), column.getShort());
            break;

            case STRING:
            values.put(column.getName(), column.getString());
            break;
        }
    }

    /**
     * loads the value from {@code cursor} into {@code column} where the column
     *   name in {@code cursor} matches {@code column}'s column name.
     *
     * @param cursor {@code Cursor} object to extract values from.
     * @param column {@code Column} object to put data into.
     */
    private static void get(Cursor cursor, Column column)
    {
        int i = cursor.getColumnIndex(column.getName());

        switch(column.getJavaType())
        {

            case BOOLEAN:
                column.setValue(cursor.getInt(i) == 1);
                break;

            case BYTES:
                column.setValue(cursor.getBlob(i));
                break;

            case DOUBLE:
                column.setValue(cursor.getDouble(i));
                break;

            case FLOAT:
                column.setValue(cursor.getFloat(i));
                break;

            case INT:
                column.setValue(cursor.getInt(i));
                break;

            case LONG:
                column.setValue(cursor.getLong(i));
                break;

            case SHORT:
                column.setValue(cursor.getShort(i));
                break;

            case STRING:
                column.setValue(cursor.getString(i));
                break;
        }
    }
}
