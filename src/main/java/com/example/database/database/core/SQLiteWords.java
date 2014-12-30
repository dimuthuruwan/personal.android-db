package com.example.database.database.core;

/**
 * defines SQLite reserved words as constant, and categorizes them.
 *
 * @author Eric Tsang
 */
interface SQLiteWords
{

    /** SQLite data types */
    enum Type
    {
        TEXT(" TEXT"),
        REAL(" REAL"),
        INT(" INTEGER"),
        BLOB(" BLOB");

        private String mSrtingValue;
        private Type(String srtingValue)
        {
            mSrtingValue = srtingValue;
        }
        public String toString()
        {
            return mSrtingValue;
        }
    }

    /** SQLite data constraints */
    enum Constraint
    {
        PRIMARY_KEY(" PRIMARY KEY"),
        NOT_NULL(" NOT NULL"),
        UNIQUE(" UNIQUE");

        private String mSrtingValue;
        private Constraint(String srtingValue)
        {
            mSrtingValue = srtingValue;
        }
        public String toString()
        {
            return mSrtingValue;
        }
    }

    /** SQLite statement openings */
    enum Opening
    {
        CREATE_TABLE_IF_NOT_EXISTS("CREATE TABLE IF NOT EXISTS "),
        DROP_TABLE_IF_EXISTS("DROP TABLE IF EXISTS ");

        private String mSrtingValue;
        private Opening(String srtingValue)
        {
            mSrtingValue = srtingValue;
        }
        public String toString()
        {
            return mSrtingValue;
        }
    }

    /** SQLite query strings */
    enum Query
    {
        ASC(" ASC"),
        DESC(" DESC");

        private String mSrtingValue;
        private Query(String srtingValue)
        {
            mSrtingValue = srtingValue;
        }
        public String toString()
        {
            return mSrtingValue;
        }
    }
}
