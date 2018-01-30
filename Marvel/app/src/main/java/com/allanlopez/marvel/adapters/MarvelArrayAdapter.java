package com.allanlopez.marvel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.allanlopez.marvel.R;
import com.allanlopez.marvel.pojo.Heroe;
import com.allanlopez.marvel.pojo.Itune;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by n16h7 on 1/29/2018.
 */

public class MarvelArrayAdapter extends ArrayAdapter<Heroe> {

    private ArrayList<Heroe> arrayList;

    public MarvelArrayAdapter(Context context, int resource, List<Heroe> objects) {
        super(context, resource, objects);
        arrayList = (ArrayList<Heroe>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Heroe heroe = arrayList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.itunes_layout, parent, false);
        }

        TextView collectionName = (TextView) convertView.findViewById(R.id.collectionName);
        TextView trackName = (TextView) convertView.findViewById(R.id.trackName);
        TextView trackPrice = (TextView) convertView.findViewById(R.id.trackPrice);
        collectionName.setText(heroe.heroeName);


        return convertView;
    }
}
