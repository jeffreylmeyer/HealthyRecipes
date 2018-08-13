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
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;


@SuppressWarnings("unused")
public class MainActivity extends Common {

    private TextView _textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        super.onCreate(savedInstanceState);
        menuID = R.menu.main_menu;
        setContentView(R.layout.activity_main);


        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);

        _textView1 = findViewById(R.id.textView1);
        Objects.requireNonNull(_textView1).setText("");

        final Button breadsC = findViewById(R.id.breadsBTN);
        Objects.requireNonNull(breadsC).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // load breads recipes
                displayRecipes(getResources().getString(R.string.breadTitle));
            }
        });
        final Button fruitsC = findViewById(R.id.fruitsBTN);
        Objects.requireNonNull(fruitsC).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // load fruits recipes
                displayRecipes(getResources().getString(R.string.fruitTitle));
            }
        });
        final Button vegC = findViewById(R.id.vegBTN);
        Objects.requireNonNull(vegC).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // load vegetables recipes
                displayRecipes(getResources().getString(R.string.vegTitle));
            }
        });
        final Button soupsC = findViewById(R.id.soupsBTN);
        Objects.requireNonNull(soupsC).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // load soups recipes
                displayRecipes(getResources().getString(R.string.soupTitle));
            }
        });
    }

    private void displayRecipes(String _cat){
        // check to make sure there are recipes in the category clicked on
        RecipeDatabase rdb = new RecipeDatabase(this);
        int cnt = rdb.GetRecipeCount(_cat);

        if(cnt >0) {

            Intent recipeView = new Intent(this, RecipePagerActivity.class);
            recipeView.putExtra("category", _cat);
            this.startActivity(recipeView);
        }else{
            Alert ad = new Alert(getResources().getString(R.string.notice),
                    getResources().getString(R.string.loadError),
                    this);
            ad.show();
        }
    }



    //------------------------------------------------------
    @Override
    protected void onDestroy(){
        super.onDestroy();
        _textView1 = null;
        System.gc();
    }
}
