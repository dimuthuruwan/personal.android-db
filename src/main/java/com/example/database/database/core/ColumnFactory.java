package com.example.database.database.core;

import com.example.database.database.core.DBWords.JavaType;

/**
 * {@code ColumnFactory} produces columns that are associated with this factory.
 *   uses the flyweight pattern; produced {@code Column} instances are
 *   flyweights.
 *
 * @author Eric Tsang
 */
public class ColumnFactory
{

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
    private final DBWords.SQLiteType mSQLiteType;

    /**
     * SQLite data constraints placed on products.
     */
    final DBWords.Constraint[] mConstraints;

    //////////////////
    // constructors //
    //////////////////

    /**
     * instantiates a {@code ColumnFactory} that makes {@code Column} instances
     *   that has or is associated with the the passed parameters.
     *
     * @param name name of produced {@code Columns}.
     * @param type data type that produced {@code Column} instances hold.
     * @param constraints data constraints associated with produced {@code
     *   Column} instances.
     */
    public ColumnFactory(String name, JavaType type,
                         DBWords.Constraint ... constraints)
    {
        mName = name.trim();
        mJavaType = type;
        mConstraints = constraints;
        mSQLiteType = DBWords.getSQLiteType(mJavaType);
    }

    //////////////////////
    // public interface //
    //////////////////////

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
    public DBWords.SQLiteType getSQLiteType()
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
