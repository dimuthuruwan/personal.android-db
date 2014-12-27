package com.example.database.database;

import android.content.Context;
import android.provider.BaseColumns;

import com.example.database.database.ColumnFactory.JavaType;

/**
 * contract describing table used for saving name data
 *
 * @author Eric Tsang
 */
public class NamesTable extends Table
{
    public static final NamesTable sInstance = new NamesTable();

    @Override
    protected String getName()
    {
        return Entry.TABLE_NAME;
    }

    @Override
    protected ColumnFactory[] getColumnFactories()
    {
        return new ColumnFactory[] {
                new ColumnFactory(this, Entry._ID,        JavaType.LONG,   Constraint.PRIMARY_KEY),
                new ColumnFactory(this, Entry.FIRST_NAME, JavaType.STRING, Constraint.NOT_NULL),
                new ColumnFactory(this, Entry.LAST_NAME,  JavaType.STRING, Constraint.NOT_NULL)
        };
    }

    @Override
    public Database getWritableDatabase(Context context)
    {
        return SQLiteDatabaseAdapter.getWritableDatabase(context);
    }

    /** contract describing table used for saving name data */
    public interface Entry extends BaseColumns
    {

        /**todo update comments
         * name of table in database.
         */
        public static final String TABLE_NAME = "Names";

        /**
         * column that contains the time that the message was sent.
         */
        public static final String FIRST_NAME = "FirstName";

        /**
         * column that contains the time that the message was received.
         */
        public static final String LAST_NAME = "LastName";
    }
}
