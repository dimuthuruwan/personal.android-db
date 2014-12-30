package com.example.database.database.core;

import java.nio.ByteBuffer;

/**
 * {@code Column} instance represents a column that's part of a row in a table
 *   in a database. holds data, and has members that describe various database
 *   related properties about instances.
 *
 * @author Eric Tsang
 */
public class Column
{
    /**
     * the {@code ColumnFactory} that produced this instance.
     */
    private ColumnFactory mProducer;

    /**
     * data saved in this instance.
     */
    private byte[] mData;

    /**
     * instantiates a {@code Column} instance and sets it's producer pointer to
     *   the passed {@code columnFactory}.
     */
    Column(ColumnFactory producer)
    {
        mProducer = producer;
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
        return mProducer.getName();
    }

    /**
     * returns the SQLite data type that this instance holds.
     *
     * @return SQLite data type associated with this instance.
     */
    public SQLiteWords.Type getSQLiteType()
    {
        return mProducer.getSQLiteType();
    }

    /**
     * returns the Java data type that this instance holds.
     *
     * @return Java data type associated with this instance.
     */
    public ColumnFactory.JavaType getJavaType()
    {
        return mProducer.getJavaType();
    }

    /**
     * returns the SQLite data constraints associated with this instance.
     *
     * @return array on SQLite data constraints associated with this instance.
     */
    public SQLiteWords.Constraint[] getConstraits()
    {
        return mProducer.mConstraints;
    }

    /**
     * sets the value saved in this instance.
     *
     * pre-conditions:
     *
     * - passed data must be compatible with the type of data that this instance
     *   accepts.
     *
     * post-conditions:
     *
     * - passed data is saved into the column.
     *
     * @param data data to save into this instance.
     */
    public void setValue(boolean data)
    {
        if(getJavaType() != ColumnFactory.JavaType.BOOLEAN)
        {
            throw new IllegalStateException("Invalid data type for column; column is for "
                    +getJavaType()+" values");
        }

        ByteBuffer buff = ByteBuffer.allocate(1);
        buff.put((byte) ((data) ? 0x01 : 0x00));

        mData = buff.array();
    }

    public void setValue(byte ... data)
    {
        if(getJavaType() != ColumnFactory.JavaType.BYTES)
        {
            throw new IllegalStateException("Invalid data type for column; column is for "
                    +getJavaType()+" values");
        }

        mData = data;
    }

    public void setValue(double data)
    {
        if(getJavaType() != ColumnFactory.JavaType.DOUBLE)
        {
            throw new IllegalStateException("Invalid data type for column; column is for "
                    +getJavaType()+" values");
        }

        ByteBuffer buff = ByteBuffer.allocate(Double.SIZE / Byte.SIZE);
        buff.putDouble(data);
        mData = buff.array();
    }

    public void setValue(float data)
    {
        if(getJavaType() != ColumnFactory.JavaType.FLOAT)
        {
            throw new IllegalStateException("Invalid data type for column; column is for "
                    +getJavaType()+" values");
        }

        ByteBuffer buff = ByteBuffer.allocate(Float.SIZE / Byte.SIZE);
        buff.putFloat(data);
        mData = buff.array();
    }

    public void setValue(int data)
    {
        if(getJavaType() != ColumnFactory.JavaType.INT)
        {
            throw new IllegalStateException("Invalid data type for column; column is for "
                    +getJavaType()+" values");
        }

        ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        buff.putInt(data);
        mData = buff.array();
    }

    public void setValue(long data)
    {
        if(getJavaType() != ColumnFactory.JavaType.LONG)
        {
            throw new IllegalStateException("Invalid data type for column; column is for "
                    +getJavaType()+" values");
        }

        ByteBuffer buff = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
        buff.putLong(data);
        mData = buff.array();
    }

    public void setValue(short data)
    {
        if(getJavaType() != ColumnFactory.JavaType.SHORT)
        {
            throw new IllegalStateException("Invalid data type for column; column is for "
                    +getJavaType()+" values");
        }

        ByteBuffer buff = ByteBuffer.allocate(Short.SIZE / Byte.SIZE);
        buff.putShort(data);
        mData = buff.array();
    }

    public void setValue(String data)
    {
        if(getJavaType() != ColumnFactory.JavaType.STRING)
        {
            throw new IllegalStateException("Invalid data type for column; column is for "
                    +getJavaType()+" values");
        }

        try
        {
            mData = data.getBytes("UTF-8");
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * returns the saved value from this instance.
     *
     * pre-conditions:
     *
     * - must be requesting the from a data type that is compatible with the
     *   data saved in this instance.
     *
     * @return data that's saved in this instance.
     */
    public boolean getBoolean()
    {
        if(getJavaType() != ColumnFactory.JavaType.BOOLEAN)
        {
            throw new IllegalStateException("this column holds " + getJavaType()
                    + "values; cannot getBoolean()");
        }

        return (mData[0] == 0x01);
    }

    public byte[] getBytes()
    {
        if(getJavaType() != ColumnFactory.JavaType.BYTES)
        {
            throw new IllegalStateException("this column holds " + getJavaType()
                    + "values; cannot getBytes()");
        }

        return mData;
    }

    public double getDouble()
    {
        if(getJavaType() != ColumnFactory.JavaType.DOUBLE)
        {
            throw new IllegalStateException("this column holds " + getJavaType()
                    + "values; cannot getDouble()");
        }

        return ByteBuffer.wrap(mData).getDouble();
    }

    public float getFloat()
    {
        if(getJavaType() != ColumnFactory.JavaType.FLOAT)
        {
            throw new IllegalStateException("this column holds " + getJavaType()
                    + "values; cannot getFloat()");
        }

        return ByteBuffer.wrap(mData).getFloat();
    }

    public int getInt()
    {
        if(getJavaType() != ColumnFactory.JavaType.INT)
        {
            throw new IllegalStateException("this column holds " + getJavaType()
                    + "values; cannot getInt()");
        }

        return ByteBuffer.wrap(mData).getInt();
    }

    public long getLong()
    {
        if(getJavaType() != ColumnFactory.JavaType.LONG)
        {
            throw new IllegalStateException("this column holds " + getJavaType()
                    + "values; cannot getLong()");
        }

        return ByteBuffer.wrap(mData).getLong();
    }

    public short getShort()
    {
        if(getJavaType() != ColumnFactory.JavaType.SHORT)
        {
            throw new IllegalStateException("this column holds " + getJavaType()
                    + "values; cannot getShort()");
        }

        return ByteBuffer.wrap(mData).getShort();
    }

    public String getString()
    {
        if(getJavaType() != ColumnFactory.JavaType.STRING)
        {
            throw new IllegalStateException("this column holds " + getJavaType()
                    + "values; cannot getString()");
        }

        try
        {
            return new String(mData, "UTF-8");
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
