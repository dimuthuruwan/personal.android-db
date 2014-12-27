package com.example.database.application;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedHashSet;
import java.util.Set;

import com.example.database.R;
import com.example.database.database.Database;
import com.example.database.database.NamesTable;
import com.example.database.database.QueryArgsBuilder;
import com.example.database.database.Row;
import com.example.database.database.util.DBAdapter;
import com.example.database.domain.Name;

public class MainActivity extends ActionBarActivity
{

    ////////////////////
    // GUI references //
    ////////////////////

    /** dialog object used to let the user create new names */
    private AlertDialog mCreateDialog;

    /** dialog object used to let the user edit an existing name */
    private AlertDialog mEditDialog;

    /**
     * reference to EditText view for entering a first name on the CreateDialog
     */
    private EditText mCreateDialogFirstNameInput;

    /**
     * reference to EditText view for entering a last name on the CreateDialog
     */
    private EditText mCreateDialogLastNameInput;

    /**
     * reference to EditText view for entering a first name on the EditDialog
     */
    private EditText mEditDialogFirstNameInput;

    /** reference to EditText view for entering a last name on the EditDialog */
    private EditText mEditDialogLastNameInput;

    /** reference to the activity's one and only ListView */
    private ListView mListView;

    ////////////////////////
    // instance variables //
    ////////////////////////

    /** adapter for this activity's listview */
    private DBAdapter mDBAdapter;

    /** name being edited by the edit dialog */
    private Name nameToEdit;

    /** ModeCallback object used to handle mode and multiselect callbacks */
    private ModeCallback mModeCallback;

    ////////////////////////
    // activity callbacks //
    ////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeInstanceData();
        initializeGUIReferences();
        configureGUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.action_add:
                mCreateDialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////
    // interface methods //
    ///////////////////////

    public void insertName(Name name) {
        Database db = NamesTable.sInstance.getWritableDatabase(this);
        NamesTable.sInstance.insertOrUpdateRows(db, name.toRow());
        db.close();
        mDBAdapter.notifyDataSetChanged();
    }

    public void updateName() {
        Database db = NamesTable.sInstance.getWritableDatabase(this);
        NamesTable.sInstance.insertOrUpdateRows(db, nameToEdit.toRow());
        db.close();
        mDBAdapter.notifyDataSetChanged();
    }

    public void deleteNames(Set<Row> rows) {
        Database db = NamesTable.sInstance.getWritableDatabase(this);
        NamesTable.sInstance.deleteRows(db, rows.toArray(new Row[rows.size()]));
        db.close();
        mDBAdapter.notifyDataSetChanged();
    }



    /////////////////////
    // support methods //
    /////////////////////
    private void initializeInstanceData() {
        mDBAdapter = new DBAdapter(this, NamesTable.sInstance, new QueryArgsBuilder().getQueryArgs())
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                if(convertView == null)
                {
                    LayoutInflater inflater = getLayoutInflater();
                    convertView = inflater.inflate(android.R.layout.simple_list_item_activated_1, parent, false);
                }

                Name name = new Name(getItem(position));

                TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
                textView.setText(name.toString());
                return convertView;
            }
        };
        mModeCallback = new ModeCallback(this);
    }

    private void initializeGUIReferences() {
        LayoutInflater inflater = getLayoutInflater();

        // create the edit & create dialogs, and initialize the view references
        // on the each one
        View v = inflater.inflate(R.layout.dialog_edit, null);
        mCreateDialogFirstNameInput = (EditText) v.findViewById(R.id.first_name_edittext);
        mCreateDialogLastNameInput = (EditText) v.findViewById(R.id.last_name_edittext);
        mCreateDialog = buildCreateDialog(v);

        v = inflater.inflate(R.layout.dialog_edit, null);
        mEditDialogFirstNameInput = (EditText) v.findViewById(R.id.first_name_edittext);
        mEditDialogLastNameInput = (EditText) v.findViewById(R.id.last_name_edittext);
        mEditDialog = buildEditDialog(v);

        // other GUI references
        mListView = (ListView) findViewById(R.id.name_listview);
    }

    private void configureGUI() {
        mListView.setAdapter(mDBAdapter);
        mCreateDialog.setTitle("Create a new name");
        mEditDialog.setTitle("Edit an existing name");
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(mModeCallback);
        mListView.setOnItemClickListener(new MyOnItemClickListener());
        mDBAdapter.notifyDataSetChanged();

    }

    private AlertDialog buildCreateDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setPositiveButton(
                R.string.confirm,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        insertName(new Name(
                                mCreateDialogFirstNameInput.getText().toString(),
                                mCreateDialogLastNameInput.getText().toString()));
                        mCreateDialogFirstNameInput.setText("");
                        mCreateDialogLastNameInput.setText("");
                        mCreateDialogFirstNameInput.requestFocus();

                    }

                });
        builder.setNegativeButton(
                R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCreateDialogFirstNameInput.setText("");
                        mCreateDialogLastNameInput.setText("");
                        mCreateDialogFirstNameInput.requestFocus();

                    }

                });

        return builder.create();

    }

    private AlertDialog buildEditDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setPositiveButton(
                R.string.confirm,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nameToEdit.setFirstName(
                                mEditDialogFirstNameInput.getText().toString());
                        nameToEdit.setLastName(
                                mEditDialogLastNameInput.getText().toString());
                        updateName();
                        mEditDialogFirstNameInput.setText("");
                        mEditDialogLastNameInput.setText("");
                        mEditDialogFirstNameInput.requestFocus();

                    }

                });
        builder.setNegativeButton(
                R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mEditDialogFirstNameInput.setText("");
                        mEditDialogLastNameInput.setText("");
                        mEditDialogFirstNameInput.requestFocus();

                    }

                });

        return builder.create();

    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            nameToEdit = new Name(mDBAdapter.getItem(position));
            mEditDialogFirstNameInput.setText(nameToEdit.getFirstName());
            mEditDialogLastNameInput.setText(nameToEdit.getLastName());
            mEditDialog.show();
        }

    }

    private class ModeCallback implements ListView.MultiChoiceModeListener {

        /** array list of names that are currently selected */
        Set<Row> selectedNames = new LinkedHashSet<>();

        /** reference to application context */
        private Context appContext;

        public ModeCallback(Context appContext) {
            this.appContext = appContext;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_multiselect, menu);
            mode.setTitle("Select Items");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deleteNames(selectedNames);
                    mode.finish();
                    break;
                default:
                    Toast.makeText(appContext, "Clicked " + item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            selectedNames.clear();
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode,
                int position, long id, boolean checked) {
            if (checked) {
                selectedNames.add(mDBAdapter.getItem(position));
            } else {
                selectedNames.remove(mDBAdapter.getItem(position));
            }
            final int checkedCount = mListView.getCheckedItemCount();
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                case 1:
                    mode.setSubtitle("One item selected");
                    break;
                default:
                    mode.setSubtitle("" + checkedCount + " items selected");
                    break;
            }
        }

    }

}
