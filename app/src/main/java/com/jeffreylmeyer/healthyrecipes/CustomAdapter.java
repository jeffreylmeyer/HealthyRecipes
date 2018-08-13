package com.jeffreylmeyer.healthyrecipes;
/*
    Healthy Recipes for Android 6+
    Created: 01-MAR-2018 by Jeffrey L Meyer
    http://github.com/jeffreylmeyer

    Released as open source for educational purposes, only.
    See LICENSE for details.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

@SuppressWarnings("CanBeFinal")
class CustomAdapter extends ArrayAdapter<Model>{
    private Model[] modelItems;
    private final Context context;

    public CustomAdapter(Context context,Model[] resource){
        super(context, R.layout.recipe_list_row,resource);
        this.context=context;
        this.modelItems=resource;
    }

    @NonNull
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        if(modelItems.length < 1){return convertView;}

        LayoutInflater inflater=((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.recipe_list_row, parent, false);

        TextView name = convertView.findViewById(R.id.rowTextView);
        CheckBox cb = convertView.findViewById(R.id.rowCheckBox);
        name.setText(modelItems[position].getName());

        switch (modelItems[position].getValue()) {
            case 1:
                cb.setChecked(true);
                break;
            case 0:
                cb.setChecked(false);
                break;
            default:
                cb.setEnabled(false);
                cb.setChecked(false);
                break;
        }
        return convertView;
    }
}