package com.jeffreylmeyer.healthyrecipes;
/*
    Healthy Recipes for Android 6+
    Created: 01-MAR-2018 by Jeffrey L Meyer
    http://github.com/jeffreylmeyer

    Released as open source for educational purposes, only.
    See LICENSE for details.
 */
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.Objects;

@SuppressWarnings("unused")
public class DeleteRecipeActivity extends Common {
    private RecipeDatabase rdb;
    private CustomAdapter adapter;

    private ListView lv;
    private Model[] modelItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_recipe);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // setup toolbar stuff
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle(R.string.delSubtitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // give a back button for home
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_previous);
        // connect to or create database
        rdb = new RecipeDatabase(this);

        // query all records and put in Cursor object
        Cursor recList = rdb.QueryAll();
        int recCnt = recList.getCount();

        // create listview to put recipes into
        lv = findViewById(R.id.recipeListView);
        modelItems = new Model[recCnt];

        int i = 0;
        recList.moveToFirst();
        // mark as unchecked initially
        while (!recList.isAfterLast()) {
            modelItems[i] = new Model(recList.getInt(0) + ": " + recList.getString(1), 0,
                    recList.getInt(0)); // name = col 1, _id = col 0
            // we need to store the DB _id in the listview so we can reference
            // it when deleting recipes, rather than wasting efficiency querying by name
            i++;
            recList.moveToNext();
        }

        adapter = new CustomAdapter(this, modelItems);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // toggle checkmark
                if (modelItems[position].getValue() == 0) {
                    modelItems[position].setValue(1);
                } else {
                    modelItems[position].setValue(0);
                }

                adapter.notifyDataSetChanged(); // found by trial & error, will check/uncheck
                // necessary because we're overriding the OnItemClickListener which
                // disables default checkbox toggle
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return true;
    }

    // method for delete onClick
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteRecipes();
                return true;
            //      case R.id.action_del_cancel:
            //          finish();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    // delete selected recipes (with confirmation first)
    // dialog listener calls the delete method
    private void deleteRecipes() {
        // confirm delete
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteRecipeActivity.this);
        builder.setMessage(R.string.delete_confirm_msg).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


    private final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    deleteSelectedRecipes();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    // delete selected recipes, if confirmed
    private void deleteSelectedRecipes() {
        int idx;
        int cnt = 0;
        // String delList = "";
        // get list of checkmarked recipes

        try {

            rdb = new RecipeDatabase(DeleteRecipeActivity.this);
            for (idx = 0; idx < modelItems.length; idx++) {
                if (modelItems[idx].getValue() == 1) {
                    rdb.Delete(modelItems[idx].getID());

                    modelItems[idx].setValue(-1);
                    cnt++;
                }
            }

        } catch (Exception ex) {
            Alert ad = new Alert(getResources().getString(R.string.err),
                    getResources().getString(R.string.delete_missing),
                    this);
            ad.show();
        }
        finish();
    }

    // selects all checkboxes by marking .value=1 to designate selected (checked)
    // and refreshing listview.
    public void selectAll(View v) {
        CheckBox sa = findViewById(R.id.selectAllCB);
        if (sa.isChecked()) {
            // select all checkboxes
            int idx;
            for (idx = 0; idx < modelItems.length; idx++) {
                modelItems[idx].setValue(1); // set as selected
            }
            sa.setText(R.string.uncheckall); // toggle mode of cb
        } else {
            // unselect all checkboxes
            int idx;
            for (idx = 0; idx < modelItems.length; idx++) {
                modelItems[idx].setValue(0); // set as unselected
            }
            sa.setText(R.string.checkall); // toggle mode of cb
        }
        adapter.notifyDataSetChanged(); // force update on listview
    }

    @Override
    protected void onDestroy() {
        try {
            lv.setAdapter(null);
            lv = null;
            adapter.clear();
            adapter = null;
            // not sure if necessary, but can't hurt
            for (int x = 0; x < modelItems.length; x++) {
                modelItems[x] = null;
            }
            modelItems = null;
        } catch (Exception ignore) {

        }
        super.onDestroy();
        System.gc();
    }

}
