<h1>AndroidDatabase</h1>

<p>this is a simple android application that stores names in an extensible (ish?) database framework.</p>

<h3>what the app does</h3>

<p>using this app, you can:</p>

<ul>
    <li>create & insert names to the database by pressing the add button on the action bar</li>
    <li>update a name by clicking on it</li>
    <li>delete names by long clickig them to multiselect them, and then pressing the delete button</li>
</ul>

<h3>how to extend the database framework</h3>

<ol>
    <li>create a subclass of the Table class (i.e.: <a href="https://github.com/A00841554/AndroidDatabase/blob/master/src/main/java/com/example/database/database/NamesTable.java">the NameTable class<a>). here, you can:
        <ul>
            <li>specify the column names of your table</li>
            <li>specify each column's constraints</li>
            <li>specify the datatype stored in the columns</li>
            <li>override the getWritableDatabase method to return the appropriate Database class subclass (SQLiteDatabaseAdapter or ContentProdiverAdapter)</li>
        </ul>
    </li>
    <li>update the DBAccess class as needed:
        <ul>
            <li>update DATABASE_VERSION as needed</li>
            <li>update the onCreate method</li>
            <li>update the onUpgrade method</li>
        </ul>
    </li>
    <li>interact with the database through the Table subclasses.</li>
</ol>
