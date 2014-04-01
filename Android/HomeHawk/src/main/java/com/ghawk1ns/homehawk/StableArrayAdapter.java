package com.ghawk1ns.homehawk;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.parse.ParseObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by guyhawkins on 2/17/14.
 */
class StableArrayAdapter extends ArrayAdapter<ParseObject> {

    HashMap<ParseObject, Integer> mIdMap = new HashMap<ParseObject, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId,
                              List<ParseObject> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        ParseObject item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
