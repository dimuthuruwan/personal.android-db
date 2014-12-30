package com.example.database.database.core;

import android.provider.BaseColumns;

import com.example.database.database.core.ColumnFactory.JavaType;

import java.util.HashMap;

/**
 * contract describing table used for saving name data
 *
 * @author Eric Tsang
 */
public class NamesTable extends Table
{
    public static final NamesTable sInstance = new NamesTable();

    @Override
    String initializeName()
    {
        return Entry.TABLE_NAME;
    }

    @Override
    HashMap<String, ColumnFactory> initializeColumnFactories()
    {
        HashMap<String, ColumnFactory> toReturn = new HashMap<>(3);
        toReturn.put(Entry._ID, new ColumnFactory(Entry._ID, JavaType.LONG, Constraint.PRIMARY_KEY));
        toReturn.put(Entry.FIRST_NAME, new ColumnFactory(Entry.FIRST_NAME, JavaType.STRING, Constraint.NOT_NULL));
        toReturn.put(Entry.LAST_NAME, new ColumnFactory(Entry.LAST_NAME,  JavaType.STRING, Constraint.NOT_NULL));
        return toReturn;
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
