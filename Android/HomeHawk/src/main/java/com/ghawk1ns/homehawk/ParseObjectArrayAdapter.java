package com.ghawk1ns.homehawk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by guyhawkins on 2/17/14.
 */

public class ParseObjectArrayAdapter extends ArrayAdapter<ParseObject> {

    private final Context context;
    private final ArrayList<ParseObject> objects;
    private final String key;

    public ParseObjectArrayAdapter(Context context, ArrayList<ParseObject> objects, String key) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.context = context;
        this.objects = objects;
        this.key = key;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        TextView textView = (TextView) rowView.findViewById(android.R.id.text1);

        ParseObject obj = objects.get(position);
        try{
            String name = (String) obj.get(key);
            textView.setText(name);
        }
        catch (Exception e){
            textView.setText("Error: Could not find name");
        }

        return rowView;
    }

    public void addToList(ParseObject obj){
        objects.add(obj);
    }


}