package gmail.et.surplus.namedatabase.database.contract;

import android.provider.BaseColumns;

/**
 * Created by etsang on 18/10/14.
 */
public final class NameContract implements SQLDataTypes {

    /** empty, private, do nothing constructor to prevent instantiation */
    private NameContract() {}

    /** contract describing table used for saving name data */
    public final class NameEntry implements BaseColumns {

        /** name of table in database */
        public static final String TABLE_NAME = "Names";

        /**
         * columns that contains the first name of a name object
         * column data type: TEXT
         */
        public static final String COLUMN_NAME_FIRST_NAME = "FirstName";

        /**
         * columns that contains the last name of a name object
         * column data type: TEXT
         */
        public static final String COLUMN_NAME_LAST_NAME = "LastName";

    }

    public static final String SQL_CREATE =
            "CREATE TABLE " + NameEntry.TABLE_NAME + " (" +
                    NameEntry._ID + TYPE_INT + " PRIMARY KEY" + COMMA_SEP +
                    NameEntry.COLUMN_NAME_FIRST_NAME + TYPE_TEXT + COMMA_SEP +
                    NameEntry.COLUMN_NAME_LAST_NAME + TYPE_TEXT +
            " )";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + NameEntry.TABLE_NAME;

}
