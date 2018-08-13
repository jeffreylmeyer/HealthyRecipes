package com.jeffreylmeyer.healthyrecipes;
/*
    Healthy Recipes for Android 6+
    Created: 01-MAR-2018 by Jeffrey L Meyer
    http://github.com/jeffreylmeyer

    Released as open source for educational purposes, only.
    See LICENSE for details.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

@SuppressWarnings("CanBeFinal")
class Alert {
    private String msg;
    private final Context c;
    private String title;

    public Alert(String _t, String _txt, Context _context){
        msg = _txt;
        c = _context;
        title = _t;
    }

    // generic alert dialog for messages
    public void show() {

        // setup the alert builder
        AlertDialog alertDialog = new AlertDialog.Builder(c).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);


        // add a button
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // create and show the alert dialog
        alertDialog.show();
    }
    public void showToast(Context c){
        Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
    }
}
