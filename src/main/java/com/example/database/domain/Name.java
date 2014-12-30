package com.example.database.domain;

import com.example.database.database.core.NamesTable;
import com.example.database.database.core.Row;

/**
 * Created by etsang on 16/10/14.
 *
 * when _ID = -1, and passed into a NameDataSource,
 * a new Name will be inserted into the database. if the Name object has an
 * ID, then the NameDataSource will try to update the corresponding name with
 *  the data in the Name object.
 */
public class Name {

    ////////////////////////
    // instance variables //
    ////////////////////////

    private final Long id;
    private String firstName;
    private String lastName;

    //////////////////
    // constructors //
    //////////////////

    public Name(String firstName, String lastName)
    {
        this.id = null;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Name(Row row)
    {
        this.id = row.getId();
        this.firstName = row.getColumn(NamesTable.Entry.FIRST_NAME).getString();
        this.lastName = row.getColumn(NamesTable.Entry.LAST_NAME).getString();
    }

    //////////////////////
    // public interface //
    //////////////////////

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public Row toRow()
    {
        Row ret = NamesTable.sInstance.makeRow();
        ret.setId(id);
        ret.getColumn(NamesTable.Entry.FIRST_NAME).setValue(getFirstName());
        ret.getColumn(NamesTable.Entry.LAST_NAME).setValue(getLastName());
        return ret;
    }

    public String toString()
    {
        return getLastName() + ", " + getFirstName();
    }

}
