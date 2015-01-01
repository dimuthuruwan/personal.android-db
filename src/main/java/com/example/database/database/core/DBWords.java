package com.example.database.database.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * defines SQLite reserved words as constant, and categorizes them.
 *
 * @author Eric Tsang
 */
public abstract class DBWords
{
    /** private constructor, we don't want to instantiate this. */
    private DBWords()
    {
    }

    /** Java data types */
    public enum JavaType
    {
        BOOLEAN("boolean"),
        BYTES("byte"),
        DOUBLE("double"),
        FLOAT("float"),
        INT("integer"),
        LONG("long"),
        SHORT("short"),
        STRING("string");

        private final String mStringValue;
        private JavaType(String stringValue) { mStringValue = stringValue; }
        public String toString() { return mStringValue; }
    }

    /** SQLite data types */
    public enum SQLiteType
    {
        TEXT(" TEXT"),
        REAL(" REAL"),
        INT(" INTEGER"),
        BLOB(" BLOB");

        private String mStringValue;
        private SQLiteType(String stringValue)
        {
            mStringValue = stringValue;
        }
        public String toString()
        {
            return mStringValue;
        }
    }

    /** SQLite data constraints */
    public enum Constraint
    {
        PRIMARY_KEY(" PRIMARY KEY"),
        NOT_NULL(" NOT NULL"),
        UNIQUE(" UNIQUE");

        private String mStringValue;
        private Constraint(String stringValue)
        {
            mStringValue = stringValue;
        }
        public String toString()
        {
            return mStringValue;
        }
    }

    /** SQLite statement openings */
    public enum Opening
    {
        CREATE_TABLE_IF_NOT_EXISTS("CREATE TABLE IF NOT EXISTS "),
        DROP_TABLE_IF_EXISTS("DROP TABLE IF EXISTS ");

        private String mStringValue;
        private Opening(String stringValue)
        {
            mStringValue = stringValue;
        }
        public String toString()
        {
            return mStringValue;
        }
    }

    /** SQLite query strings */
    public enum Query
    {
        ASC(" ASC"),
        DESC(" DESC");

        private String mStringValue;
        private Query(String stringValue)
        {
            mStringValue = stringValue;
        }
        public String toString()
        {
            return mStringValue;
        }
    }

    /** map of how JavaType enums correspond to SQLiteType enums */
    private static final Map<JavaType, SQLiteType> TYPE_MAP;
    static
    {
        TYPE_MAP = new LinkedHashMap<>();
        TYPE_MAP.put(JavaType.BOOLEAN, SQLiteType.INT);
        TYPE_MAP.put(JavaType.BYTES,   SQLiteType.BLOB);
        TYPE_MAP.put(JavaType.DOUBLE,  SQLiteType.REAL);
        TYPE_MAP.put(JavaType.FLOAT,   SQLiteType.REAL);
        TYPE_MAP.put(JavaType.INT,     SQLiteType.INT);
        TYPE_MAP.put(JavaType.LONG,    SQLiteType.INT);
        TYPE_MAP.put(JavaType.SHORT,   SQLiteType.INT);
        TYPE_MAP.put(JavaType.STRING,  SQLiteType.TEXT);
    }

    /**
     * returns the corresponding SQLite type given the JavaType enumeration
     *
     * @param  javaType JavaType enumeration to get the corresponding SQLiteType
     *   enumeration for
     *
     * @return the corresponding SQLiteType enumeration for {@code javaType}
     */
    public static SQLiteType getSQLiteType(JavaType javaType)
    {
        SQLiteType sqliteType = TYPE_MAP.get(javaType);

        if(sqliteType == null)
        {
            throw new RuntimeException("missing entry for mapping "
                    +"JavaType to SQLite.Type");
        }

        return sqliteType;
    }

    /**
     * returns true if {@code javaType} and {@code sqliteType} are logically
     *   equivalent.
     *
     * @param  javaType JavaType enumeration to check
     * @param  sqliteType SQLiteType enumeration to check
     *
     * @return true if {@code javaType} and {@code sqliteType} are logically
     *   equivalent.
     */
    public static boolean isLogicallyEquivilant(
            JavaType javaType, SQLiteType sqliteType)
    {
        return getSQLiteType(javaType) == sqliteType;
    }
}
