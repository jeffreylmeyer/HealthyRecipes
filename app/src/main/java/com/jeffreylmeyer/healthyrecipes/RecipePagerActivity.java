package com.jeffreylmeyer.healthyrecipes;
/*
    Healthy Recipes for Android 6+
    Created: 01-MAR-2018 by Jeffrey L Meyer
    http://github.com/jeffreylmeyer

    Released as open source for educational purposes, only.
    See LICENSE for details.
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("unused")
public class RecipePagerActivity extends Common {
    private String _viewCategory = null;
    private TextView rNameV;
    private TextView rCatV;
    private TextView rIngV;
    private TextView rInstV;
    private TextView rNumRecV;

    // create an array list to hold the list of recipe ids (DB _id)
    // will be used to page through the recipes one at a time
    private ArrayList<Integer> recipeIDList = new ArrayList<>();
    private int currentID = 0; // current list index of recipeIDList(0..last)

    @Override
    protected void onDestroy(){
        rNumRecV = null;
        rNameV = null;
        rCatV = null;
        rIngV = null;
        rInstV = null;
        recipeIDList.clear();
        recipeIDList = null;
        _viewCategory = null;
        super.onDestroy();
        System.gc();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuID = R.menu.pager_menu;

        setContentView(R.layout.activity_recipe_pager);

        // setup pager activity toolbar
        Toolbar toolbar = findViewById(R.id.pagertoolbar); // uses pager_menu
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle(R.string.viewSubtitle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // give a back button for home
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_previous);
        // category passed from calling activity
        Intent myI = getIntent();
        _viewCategory = myI.getStringExtra("category"); // category sent from button handler

        // display category text on screen
        rCatV = findViewById(R.id.recipeCategory);
        rCatV.setText(_viewCategory);

        // get pointers to text views for displaying data
        rNameV = findViewById(R.id.recipeName);
        rIngV = findViewById(R.id.recipeIngredients);
        rInstV = findViewById(R.id.recipeInstructions);
        rNumRecV = findViewById(R.id.numRecipes);
        ImageButton prBtn = findViewById(R.id.prevButton);
        prBtn.setEnabled(false); // set < prev button disabled to start, because we'll load first
                                 // record to start
    }

    @Override
    protected void onResume(){
        super.onResume();
        // get the list of recipe ids
        rNameV.setText(R.string.rloading);
        getRecipeIDS();
    }

    private void idsLoaded(){
        currentID = 0;                   // set array index pointer (for button actions)
        loadRecipe(recipeIDList.get(0)); // should point to first _id stored in array (==1)
    }

    private void noneOnFile(){
        findViewById(R.id.prevButton).setEnabled(false); // disable < prev button
        findViewById(R.id.nextButton).setEnabled(false); // disable > next button
        currentID = 0;
    }

    private void updateRecipeData(RecipeData _rData){
        // set data into textviews
        if(_rData != null && !"".equals(_rData.recipeName) ) {
            rCatV.setText(_rData.recipeCategory); // not really needed, but good to have
            rNameV.setText(_rData.recipeName);
            rIngV.setText(_rData.recipeIngredients);
            rInstV.setText(_rData.recipeInstructions);
        }else{
            rNameV.setText(R.string.err);
            rIngV.setText(R.string.err);
            rInstV.setText(R.string.err);
        }
        // display how many recipes on file (e.g. 1 of 5)
        String tmp;
        tmp = "(" + (currentID +1) + " of " + recipeIDList.size() + ")";
        rNumRecV.setText(tmp);
    }

    //-------------------------------------------------------------------
    // load list of recipe ids into recipeIDList
    private void getRecipeIDS() {
        RecipeDatabase RDB = new RecipeDatabase(this);
        ArrayList<Integer> tmpArr = new ArrayList<>(1);
        try {
            recipeIDList.addAll(RDB.LoadIDList(_viewCategory));
            if (recipeIDList.size() <= 0) {
                Alert ad = new Alert(getResources().getString(R.string.notice),
                        getResources().getString(R.string.loadError),
                        RecipePagerActivity.this);
                ad.showToast(RecipePagerActivity.this);
                noneOnFile(); // if category empty return and disable < > buttons
            } else {
                idsLoaded(); // call idsLoaded() method to load first recipe in selected category
            }
        } catch (Exception e) {
            Alert ad = new Alert(getString(R.string.err), e.getMessage(),this);
            ad.show();
        }
    }

    // load recipe
    private void loadRecipe(int _id){
        RecipeData rDatatmp;
        RecipeDatabase RDB = new RecipeDatabase(RecipePagerActivity.this);
        rDatatmp = new RecipeData(_id,"ERROR",
                "ERROR","ERROR","ERROR");
        try {
            rDatatmp = RDB.GetRecord(_id);

            // check for a returned error from RDB
            if("ERROR1".equals(rDatatmp.recipeName)){
                throw new Exception("ERROR LOADING SQL");
            }
        }catch(Exception ex){
            rDatatmp.recipeName = "LOAD ERROR";
        }
        updateRecipeData(rDatatmp);
    }

    // load previous recipe from current category, if any
    // < button click
    public void previousRec(View v){
        String tmp;
        currentID--;
        if(currentID <= 0){
            currentID = 0; // prevent going out of bounds on array
            findViewById(R.id.prevButton).setEnabled(false); // disable < prev button
        }
        if(currentID <= recipeIDList.size()-1){
            findViewById(R.id.nextButton).setEnabled(true); // enable > next button
        }
        loadRecipe(recipeIDList.get(currentID)); // pull recipe _id from list
        tmp = "(" + (currentID +1) + " of " + recipeIDList.size() + ")";
        rNumRecV.setText(tmp);
    }

    // load next recipe from current category, if any
    // > button click
    public void nextRec(View v){
        String tmp;
        currentID++;
        if(currentID >= recipeIDList.size()-1){
            currentID = recipeIDList.size()-1; // prevent going out of bounds on array
            findViewById(R.id.nextButton).setEnabled(false); // disable > next button
        }
        if(currentID >0){
            findViewById(R.id.prevButton).setEnabled(true); // enable < prev button
        }
        loadRecipe(recipeIDList.get(currentID));
        tmp = "(" + (currentID +1) + " of " + recipeIDList.size() + ")";
        rNumRecV.setText(tmp);
    }
}
