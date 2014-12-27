package com.example.database.database;

/**
 * {@code ColumnFactory} produces columns that are associated with this factory.
 *   uses the flyweight pattern; produced {@code Column} instances are
 *   flyweights.
 *
 * @author Eric Tsang
 */
class ColumnFactory
{
    /**
     * pointer to {@code Table} that products belong to.
     */
    private final Table mTable;

    /**
     * name of products.
     */
    private final String mName;

    /**
     * Java data type that's being stored in products.
     */
    private final JavaType mJavaType;

    /**
     * SQLite data type that's being stored in products.
     */
    private final SQLite.Type mSQLiteType;

    /** describes the data types that can be saved in this instance. */
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

    /**
     * SQLite data constraints placed on products.
     */
    final SQLite.Constraint[] mConstraints;

    //////////////////
    // constructors //
    //////////////////

    /**
     * instantiates a {@code ColumnFactory} that makes {@code Column} instances
     *   that has or is associated with the the passed parameters.
     *
     * @param table reference to the table that produced {@code Columns} are
     *   associated with.
     * @param name name of produced {@code Columns}.
     * @param type data type that produced {@code Column} instances hold.
     * @param constraints data constraints associated with produced {@code
     *   Column} instances.
     */
    public ColumnFactory(Table table, String name,
            JavaType type, SQLite.Constraint ... constraints)
    {
        mTable = table;
        mName = name.trim();
        mJavaType = type;
        mConstraints = constraints;
        switch(mJavaType)
        {

            case BOOLEAN:
            mSQLiteType = SQLite.Type.INT;
            break;

            case BYTES:
            mSQLiteType = SQLite.Type.BLOB;
            break;

            case DOUBLE:
            mSQLiteType = SQLite.Type.REAL;
            break;

            case FLOAT:
            mSQLiteType = SQLite.Type.REAL;
            break;

            case INT:
            mSQLiteType = SQLite.Type.INT;
            break;

            case LONG:
            mSQLiteType = SQLite.Type.INT;
            break;

            case SHORT:
            mSQLiteType = SQLite.Type.INT;
            break;

            case STRING:
            mSQLiteType = SQLite.Type.TEXT;
            break;

            default:
            throw new RuntimeException("missing case statement for mapping JavaType to SQLite.Type");
        }
    }

    //////////////////////
    // public interface //
    //////////////////////

    /**
     * returns the {@code Table} instance that this instance is a part of.
     *
     * @return SQLite data type associated with this instance.
     */
    public Table getTable()
    {
        return mTable;
    }

    /**
     * returns the name of this instance.
     *
     * @return name of {@code Column} associated with this instance.
     */
    public String getName()
    {
        return mName;
    }

    /**
     * returns the SQLite data type that products hold.
     *
     * @return SQLite data type associated with products.
     */
    public SQLite.Type getSQLiteType()
    {
        return mSQLiteType;
    }

    /**
     * returns the Java data type that products hold.
     *
     * @return Java data type associated with products.
     */
    public JavaType getJavaType()
    {
        return mJavaType;
    }

    /**
     * returns a {@code Column} instance of the {@code type} and with the {@code
     *   constraints} passed through the {@code constructor}.
     *
     * @return {@code Column} instance of the {@code type} and with the {@code
     *   constraints} passed through the {@code constructor}.
     */
    public Column makeColumn()
    {
        return new Column(this);
    }
}
