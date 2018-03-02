package com.allanlopez.veggie_os.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.allanlopez.veggie_os.R;
import com.allanlopez.veggie_os.VolleySingleton;
import com.allanlopez.veggie_os.pojo.Exercise;
import com.allanlopez.veggie_os.pojo.Food;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by n16h7 on 3/2/2018.
 */

public class ExerciseArrayAdapter extends ArrayAdapter<Exercise> {
    private ArrayList<Exercise> arrayList;
    private Context context;

    public ExerciseArrayAdapter(@NonNull Context context, int resource, @NonNull List<Exercise> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Exercise exercise = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.food_layout, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.exerciseName);
        NetworkImageView networkImageView = (NetworkImageView) convertView.findViewById(R.id.exerciseImage);
        textView.setText(exercise.name);
        RequestQueue requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        ImageLoader imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {

                    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);
                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);

                    }
                });
        networkImageView.setImageUrl(exercise.imgUrl, imageLoader);

        return convertView;
    }
}
