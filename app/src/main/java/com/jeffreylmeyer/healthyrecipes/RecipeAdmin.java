package com.jeffreylmeyer.healthyrecipes;
/*
    Healthy Recipes for Android 6+
    Created: 01-MAR-2018 by Jeffrey L Meyer
    http://github.com/jeffreylmeyer

    Released as open source for educational purposes, only.
    See LICENSE for details.
 */
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Objects;


@SuppressWarnings("unused")
public class RecipeAdmin extends Common implements View.OnClickListener {

    private Spinner sp = null; // dropdown list object
    private String catSelected = null; // holds name of the category selected from dropdown
    private View curV = null; // will hold id of currently focused form field
    // set pointers to the form fields
    private View nameTB =null;
    private View catSel=null;
    private View ingTB = null;
    private View instTB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        menuID = R.menu.admin_menu;

        setContentView(R.layout.activity_recipe_admin); // this

        // setup toolbar stuff
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(R.string.addSubtitle);
     
        View v = findViewById(R.id.saveBTN);
        //set event listener for Save button (Add)
        Objects.requireNonNull(v).setOnClickListener(this);
        View v2 = findViewById(R.id.saveBTN2);
        //set event listener for Save button2 (Add)
        Objects.requireNonNull(v2).setOnClickListener(this);
        // setup cat list items (from strings.xml category_array)
        sp = findViewById(R.id.categorylist);
        addItemList();
        addListenerOnSpinnerItemSelection();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // give a back button for home
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_action_previous);
        // set pointers
        nameTB = findViewById(R.id.recipeNameTB);
        catSel= sp;
        ingTB = findViewById(R.id.ingredientsTB);
        instTB=findViewById(R.id.instructionsTB);
        curV = nameTB; // set current field to recipename
        nameTB.setOnFocusChangeListener(focusListener);
        catSel.setOnFocusChangeListener(focusListener);
        ingTB.setOnFocusChangeListener(focusListener);
        instTB.setOnFocusChangeListener(focusListener);
    }

    @Override
    protected void onDestroy(){
        sp=null;
        nameTB = null;
        catSel = null;
        ingTB = null;
        instTB = null;
        curV = null;
        catSelected = null;
        super.onDestroy();
        System.gc();
    }

    private void addItemList(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sp.setAdapter(adapter);
    }

    private void addListenerOnSpinnerItemSelection() {
        Objects.requireNonNull(sp).setOnItemSelectedListener(new CustomOnItemSelectedListener()); // inner class
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.saveBTN || v.getId() == R.id.saveBTN2) {
            // make sure catSelected is not blank (should always have a value,
            // though, default = Breads)
            if(!"".equals(catSelected) ) {
                saveRecipe();
            }else{
                // inform category must be selected. It should have a default of Breads, so
                // this code should never be called, but here just in case the catSelected
                // gets nulled for some reason.
                Alert ad = new Alert(getResources().getString(R.string.err),
                        getResources().getString(R.string.reqCat) +
                        "\n" + getResources().getString(R.string.goBack) + "\n",
                        this);
                ad.show();
            }
        }
    }

    // save recipe in database
    private void saveRecipe(){
        boolean isError = false;
        String errMsg = "";
        String recipeName;
        String recipeIngredients;
        String recipeInstructions;
        EditText rName = findViewById(R.id.recipeNameTB); // get textbox handle
        EditText rIng = findViewById(R.id.ingredientsTB); // get textbox handle
        EditText rInst = findViewById(R.id.instructionsTB); // get textbox handle
        recipeName = Objects.requireNonNull(rName).getText().toString();

        if (recipeName != null && recipeName.length() > 30) {
                recipeName = recipeName.substring(0, 30); // get name text and chop it
        }
        recipeIngredients = Objects.requireNonNull(rIng).getText().toString(); // get textbox text
        recipeInstructions = Objects.requireNonNull(rInst).getText().toString(); // get textbox text

        // check fields are not blank

        if ("".equals(Objects.requireNonNull(recipeInstructions))) {
            errMsg += getResources().getString(R.string.reqInst) + "\n";
            isError = true;
            Objects.requireNonNull(instTB).requestFocus();
            Objects.requireNonNull(nameTB).clearFocus();
            Objects.requireNonNull(ingTB).clearFocus();
            curV = Objects.requireNonNull(instTB);
            Objects.requireNonNull(instTB).setBackgroundColor(getColor(R.color.errorHighlight));
        }else{
            Objects.requireNonNull(instTB).setBackgroundColor(getColor(R.color.fieldsNormal));
        }
        if ("".equals(Objects.requireNonNull(recipeIngredients))) {
            errMsg += getResources().getString(R.string.reqIng) + "\n";
            isError = true;
            Objects.requireNonNull(ingTB).requestFocus();
            Objects.requireNonNull(instTB).clearFocus();
            Objects.requireNonNull(nameTB).clearFocus();
            curV = Objects.requireNonNull(instTB);
            Objects.requireNonNull(ingTB).setBackgroundColor(getColor(R.color.errorHighlight));
        }else{
            Objects.requireNonNull(ingTB).setBackgroundColor(getColor(R.color.fieldsNormal));
        }

        if ("".equals(Objects.requireNonNull(recipeName))) {
            errMsg += getResources().getString(R.string.reqName) + "\n";
            isError = true;
            Objects.requireNonNull(nameTB).requestFocus();
            Objects.requireNonNull(instTB).clearFocus();
            Objects.requireNonNull(ingTB).clearFocus();
            curV = Objects.requireNonNull(nameTB);
            Objects.requireNonNull(nameTB).setBackgroundColor(getColor(R.color.errorHighlight));
        }else{
            Objects.requireNonNull(nameTB).setBackgroundColor(getColor(R.color.fieldsNormal));
        }

        // don't connect to db or try to add if no data (or missing data)
        if(!isError){
            // call AsyncTask to add recipe to database in background
            //bgAddTask(recipeName, catSelected, recipeIngredients, recipeInstructions);
            try {
                RecipeDatabase rdb = new RecipeDatabase(this);

                rdb.AddRecord(recipeName, catSelected, recipeIngredients, recipeInstructions);
            }catch (Exception e){
                Alert ad = new Alert(getResources().getString(R.string.err),
                        e.getMessage(), this);
                ad.show();
            }
        }else{
            // inform user there's blank fields
            Alert ad = new Alert(getResources().getString(R.string.err), errMsg +
                    "\n" + getResources().getString(R.string.goBack), this);
            ad.show();
        }
        // cleanup for .gc()

        if(!isError){
            finish();
        }
    }

    // inner class
    private class CustomOnItemSelectedListener
                implements android.widget.AdapterView.OnItemSelectedListener {
        // get selected dropdown item into catSelected string
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            catSelected = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }

    // method for "Next" onClick
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                // focus on next field
                selectNextField();
                return false;
            case R.id.homeAsUp:
                finish();
                return false;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    //-----------------------------------------------------------------------
    // methods to track which form field has focus, so "Next" button can switch
    // between them
    private final View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus){
                curV = v; // store field id that has focus
            } else {
                curV  = null;
            }
        }
    };
    // move to next form field, or if finished, close keypad
    // https://stackoverflow.com/questions/3400028/close-virtual-keyboard-on-button-press
    private void selectNextField(){
        if (curV == Objects.requireNonNull(nameTB)) {
            catSel.performClick();
            curV = catSel;
        }else if(curV == catSel){
            Objects.requireNonNull(ingTB).requestFocus();
            curV = Objects.requireNonNull(ingTB);
        } else if (curV == Objects.requireNonNull(ingTB)) {
            Objects.requireNonNull(instTB).requestFocus();
            curV = Objects.requireNonNull(instTB);
        } else if (curV == Objects.requireNonNull(instTB)) {
            findViewById(R.id.saveBTN).requestFocus();
            curV = findViewById(R.id.saveBTN);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // overrides Common.deleteRecipe() because it needs to exit the current activity
    // from here
    @Override
    public void deleteRecipe(View v){
        Intent adminD = new Intent(this,DeleteRecipeActivity.class);
        this.startActivity(adminD);
        finish();
    }
}
