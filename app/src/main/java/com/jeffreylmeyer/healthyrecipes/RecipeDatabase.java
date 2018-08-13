package com.jeffreylmeyer.healthyrecipes;
/*
    Healthy Recipes for Android 6+
    Created: 01-MAR-2018 by Jeffrey L Meyer
    http://github.com/jeffreylmeyer

    Released as open source for educational purposes, only.
    See LICENSE for details.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Objects;


@SuppressWarnings("UnusedReturnValue")
class RecipeDatabase {
    private final SQLiteDatabase database;
    private final ArrayList<Integer> idList = new ArrayList<>();

    // config constants
    private final static String RECIPE_TABLE = "recipes"; // name of table
    private final static String RECIPE_ID = "_id"; // id value for recipe
    private final static String RECIPE_NAME = "recipeName";  // recipe name
    private final static String RECIPE_CAT = "recipeCategory";  // recipe category
    private final static String RECIPE_INGR = "recipeIngredients";  // recipe ingredients
    private final static String RECIPE_INST = "recipeInstructions";  // recipe instructions

    public RecipeDatabase(Context context){

        DatabaseHelper dbHelper = new DatabaseHelper(context);
            database = dbHelper.getWritableDatabase();
    }

        // add recipe to database
        public long AddRecord(String _rName, String _rCat,
                                  String _rIng, String _rInst){
            long result;
            ContentValues values = new ContentValues();
            // _id is autoincrement, so not needed
            values.put(RECIPE_NAME, _rName);
            values.put(RECIPE_CAT, _rCat);
            values.put(RECIPE_INGR, _rIng);
            values.put(RECIPE_INST, _rInst);

            result = database.insert(RECIPE_TABLE, null, values);
            values.clear();
            return result;
        }

        // get all recipe records
        public Cursor QueryAll() {
            String[] cols = new String[] {RECIPE_ID, RECIPE_NAME, RECIPE_CAT, RECIPE_INGR, RECIPE_INST};
            Cursor mCursor = database.query(true, RECIPE_TABLE, cols,null
                    , null, null, null, RECIPE_ID, null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            return mCursor; // iterate to get each value.
        }

        // get a single recipe record (by id)
        // and return as a RecipeData object
        // should run in background
        public RecipeData GetRecord(int _id){
            // initialize a dummy record object
            RecipeData recipe = new RecipeData(_id,"ERROR1","ERROR1",
                            "ERROR1","ERROR1-QUERY FAIL");
            try {
                Cursor result = database.rawQuery("SELECT * " +
                                "FROM " + RECIPE_TABLE + " WHERE _id =" + _id, null);
                result.moveToFirst();
                while(!result.isAfterLast()) {
                    recipe.id = result.getInt(0);
                    recipe.recipeName = result.getString(1);
                    recipe.recipeCategory = result.getString(2);
                    recipe.recipeIngredients = result.getString(3);
                    recipe.recipeInstructions = result.getString(4);
                    result.moveToNext();
                }
                result.close();
            }catch(Exception ex){
                // fill with dummy data to prevent errors on return
              recipe.recipeName = "ERROR1";
              recipe.id = _id;
              recipe.recipeCategory = "NULL";
              recipe.recipeIngredients = "NULL";
              recipe.recipeInstructions = "NULL";
            }
            return recipe;
        }

        // get list of recipe ids (for pager)
        // should be run in background in AsyncTask
        public ArrayList<Integer> LoadIDList(String _cat){
            idList.clear();
            // fetch list of record ids (for pager activity)
            try {
                Cursor mCursor = database.rawQuery("SELECT _id FROM recipes WHERE recipeCategory='" + _cat + "'" ,
                        null);
                if (mCursor != null) {
                    mCursor.moveToFirst();
                    while(!mCursor.isAfterLast()){
                        idList.add(mCursor.getInt(0));
                        mCursor.moveToNext();
                    }
                }
                Objects.requireNonNull(mCursor).close();
            }catch(Exception ex){
                //
            }
            return idList;
        }

        // simple count of records in a category
        //
        public int GetRecipeCount(String _cat){
            int count = 0;
            try {
                Cursor mCursor = database.rawQuery(
                        "SELECT COUNT(*) FROM recipes WHERE recipeCategory='" + _cat + "'" ,
                        null);

                if (mCursor != null) {
                    mCursor.moveToFirst();
                    while(!mCursor.isAfterLast()) {
                        count += mCursor.getInt(0);
                        mCursor.moveToNext();
                    }
                }
                Objects.requireNonNull(mCursor).close();
            }catch(Exception ex){
                return 0;
            }
            return count;
        }

        // delete recipe
        public String Delete(int _recID){
            try {
                database.execSQL("DELETE FROM " + RECIPE_TABLE + " WHERE _id='" + _recID + "'");
            }catch(Exception e){
                return R.string.err + e.getMessage();
            }
            return "";
        }

        //-------------------------------------------------------
        // included for testing purposes only, to prepopulate with
        // some test data
    /*
    public void prePopulate(int qty){
            Cursor tc = QueryAll();
            if(tc.getCount() <= 0){
                // insert some test records
                String[] cats = new String[]{"Breads","Fruits","Vegetables","Soups"
                };
                int idx;
                for(String str : cats){
                    for(idx=0; idx < qty;idx++){
                        AddRecord("Test " + (idx+1) + " " +  str,
                                str, str + " Test Ingredients",str + " Test Instructions");
                    }
                }
            }
        }
        /**/
}
