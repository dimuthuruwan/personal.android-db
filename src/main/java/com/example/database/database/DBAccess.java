package com.example.database.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.database.database.core.Column;
import com.example.database.database.core.ColumnFactory;
import com.example.database.database.core.NamesTable;
import com.example.database.database.core.DBWords.JavaType;
import com.example.database.database.core.Row;
import com.example.database.domain.Name;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DBAccess is used to get information from the database.
 */
public class DBAccess extends SQLiteOpenHelper
{
    public static final String TAG = DBAccess.class.getSimpleName();

    /**
     * if DB schema changes, you must implement an onUpgrade, and change the db
     *   version. DB version >= 1.
     */
    public static final int DATABASE_VERSION = 1;

    /** name of file used to save the database data */
    public static final String DATABASE_NAME = "EverythingDatabase.db";

    /**
     * singleton instance of the database.
     */
    private static DBAccess sInstance;

    //////////////////
    // constructors //
    //////////////////

    private DBAccess(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //////////////////////
    // public interface //
    //////////////////////

    /**
     * passed into some query methods; once a row has been parsed from the
     *   cursor that results from the query, callbacks of the passed instance
     *   will be invoked to pass the row information.
     */
    public interface OnRowLoadedListener
    {
        public void onRowLoaded(Row r);
    }

    public static void getAllNames(Context context, OnRowLoadedListener listener)
    {
        SQLiteDatabase db = getInstance(context).getWritableDatabase();
        Cursor cursor = db.query(
                NamesTable.Entry.TABLE_NAME,
                NamesTable.sInstance.getColumnNames(),
                null,
                null,
                null,
                null,
                null);

        cursorToRows(cursor, listener, NamesTable.sInstance.getColumnFactories());
        cursor.close();
        db.close();
    }

    public static long insertName(Context context, Name name)
    {
        // construct the ContentValues object
        ContentValues values = new ContentValues();
        Row row = name.toRow();
        Map<String, Column> columns = row.getColumns();
        for(String columnName : columns.keySet())
        {
            if(!columnName.equals(BaseColumns._ID))
            {
                put(values, columns.get(columnName));
            }
        }

        // do the query
        SQLiteDatabase db = getInstance(context).getWritableDatabase();
        long newRowId = db.insert(
                NamesTable.Entry.TABLE_NAME,
                null,
                values);
        db.close();

        return newRowId;
    }

    public static int updateName(Context context, Name name)
    {
        // construct the ContentValues object
        ContentValues values = new ContentValues();
        Row row = name.toRow();
        Map<String, Column> columns = row.getColumns();
        for(String columnName : columns.keySet())
        {
            if(!columnName.equals(BaseColumns._ID))
            {
                put(values, columns.get(columnName));
            }
        }

        // do the query
        SQLiteDatabase db = getInstance(context).getWritableDatabase();
        int rowsAffected = db.update(
                NamesTable.Entry.TABLE_NAME,
                values,
                BaseColumns._ID+"=?",
                new String[]{String.valueOf(name.toRow().getId())});
        db.close();

        return rowsAffected;
    }

    public static int deleteNames(Context context, Row ... rows)
    {
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

        // do the query
        SQLiteDatabase db = getInstance(context).getWritableDatabase();
        // execute the SQL delete statement
        int rowsAffected = db.delete(
                NamesTable.Entry.TABLE_NAME,
                selection.toString(),
                selectionArgs);
        db.close();

        return rowsAffected;
    }

    ///////////////////////
    // private interface //
    ///////////////////////

    /**
     * parses the data out of the {@code cursor} using the information in the
     *   {@code columnFactories}. as the row data gets parsed out of the {@code
     *   cursor}, callbacks of the {@code listener} will be invoked. once all
     *   the rows have been loaded, the function also returns an array of all
     *   the rows.
     *
     * @param  cursor Cursor object obtained from a database query.
     * @param  listener will have its method invoked asynchronously as rows are
     *   parsed out of the {@code cursor}.
     * @param  columnFactories map of column names as {@code String} objects,
     *   and {@code ColumnFactory} objects. this map is used to resolve which
     *   {@code ColumnFactory} should be used to produce the columns for the
     *   columns in {@code cursor}.
     *
     * @return array of {@code Row} objects loaded from the {@code cursor}.
     */
    private static Row[] cursorToRows(Cursor cursor, OnRowLoadedListener listener,
                                      Map<String, ColumnFactory> columnFactories)
    {
        // in order to use getType properly, the cursor must be at a row;
        // we call this method because we use cursor.getType later
        cursor.moveToFirst();

        // verify that the column types correspond with the java types.
        for(String columnName : cursor.getColumnNames())
        {
            try
            {
                ColumnFactory columnFactory = columnFactories.get(columnName);
                int columnIndex = cursor.getColumnIndex(columnFactory.getName());
                if(!verifyType(columnFactory.getJavaType(), cursor.getType(columnIndex)))
                {
                    throw new IllegalArgumentException("Incompatible types or missing columns: "
                            +"factory column type: "+columnFactory.getJavaType()+", "
                            +"cursor column type: "+cursor.getType(columnIndex));
                }
            }
            catch(IllegalArgumentException e)
            {
                StringBuilder details = new StringBuilder();
                boolean first;

                first = true;
                for(String cursorColumnName : cursor.getColumnNames())
                {
                    details.append((first) ? "; cursor column names: " : ", ");
                    details.append(cursorColumnName);
                    first = false;
                }

                first = true;
                for(String factoryColumnName : columnFactories.keySet())
                {
                    details.append((first) ? "; column factory names: " : ", ");
                    details.append(factoryColumnName);
                    first = false;
                }

                throw new IllegalArgumentException(e.toString()+" : "+details.toString());
            }
            catch(Exception e)
            {
                if(cursor.getCount() == 0)
                {
                    Log.e(TAG, e.toString());
                    return new Row[0];
                }
                else
                {
                    throw e;
                }
            }
        }

        // cursor.moveToFirst() was called earlier; we don't need to call it again.

        // extract the column data into rows
        Row[] rows = new Row[cursor.getCount()];
        for(int i = 0; !cursor.isAfterLast(); ++i)
        {
            // create the row instance, and inject column data
            Map<String, Column> columns = new LinkedHashMap<>();
            for(String columnName : cursor.getColumnNames())
            {
                ColumnFactory columnFactory = columnFactories.get(columnName);
                Column column = columnFactory.makeColumn();
                columns.put(column.getName(), column);
                get(cursor, column);
            }

            rows[i] = new Row(columns);

            cursor.moveToNext();

            // if a callback listener was passed, invoke it
            if(listener != null)
            {
                listener.onRowLoaded(rows[i]);
            }
        }
        cursor.close();
        return rows;
    }

    /**
     * retrieves the singleton instance of DBAccess.
     *
     * @param  context {@code Context} object of the application.
     *
     * @return the singleton instance of {@code DBAccess}.
     */
    private static DBAccess getInstance(Context context)
    {
        if(sInstance == null)
        {
            sInstance = new DBAccess(context);
        }
        return sInstance;
    }

    /**
     * verifies that {@code javaType} and {@code cursorFieldType} are logically
     *   compatible. returns true if they are logically compatible; false
     *   otherwise.
     *
     * @param  javaType the {@code JavaType} to verify.
     * @param  cursorFieldType the integer representing the type of a column
     *   obtained through {@code Cursor.getType()} to verify.
     *
     * @return true if the passed types are logically compatible; false
     *   otherwise.
     */
    private static boolean verifyType(JavaType javaType, int cursorFieldType)
    {
        switch(javaType)
        {

            case BOOLEAN:
                return cursorFieldType == Cursor.FIELD_TYPE_INTEGER;

            case BYTES:
                return cursorFieldType == Cursor.FIELD_TYPE_BLOB;

            case DOUBLE:
                return cursorFieldType == Cursor.FIELD_TYPE_FLOAT;

            case FLOAT:
                return cursorFieldType == Cursor.FIELD_TYPE_FLOAT;

            case INT:
                return cursorFieldType == Cursor.FIELD_TYPE_INTEGER;

            case LONG:
                return cursorFieldType == Cursor.FIELD_TYPE_INTEGER;

            case SHORT:
                return cursorFieldType == Cursor.FIELD_TYPE_INTEGER;

            case STRING:
                return cursorFieldType == Cursor.FIELD_TYPE_STRING;

            default:
                throw new RuntimeException("default case! maybe there is missing case statement " +
                        "for the case: "+javaType);
        }
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

    /////////////////////////////////////
    // SQLiteOpenHelper implementation //
    /////////////////////////////////////

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NamesTable.sInstance.getCreateTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion) {
            default:
                db.execSQL(NamesTable.sInstance.getDropTableQuery());
                onCreate(db);
        }
    }
}
