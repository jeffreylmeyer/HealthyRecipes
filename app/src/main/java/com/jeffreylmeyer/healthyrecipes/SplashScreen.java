package com.jeffreylmeyer.healthyrecipes;
/*
    Healthy Recipes for Android 6+
    Created: 01-MAR-2018 by Jeffrey L Meyer
    http://github.com/jeffreylmeyer

    Released as open source for educational purposes, only.
    See LICENSE for details.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

// This is not a timed delay splash screen. This one simply loads
// a temporary background and launches the MainActivity
// So the time the splash screen shows is the time it takes
// to load the main activity.

// This is the recommended way of doing a splash screen

public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        Intent i = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(i);

        // close this activity
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        System.gc();
    }
}