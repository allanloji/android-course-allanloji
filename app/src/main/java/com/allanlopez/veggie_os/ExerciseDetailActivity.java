package com.allanlopez.veggie_os;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.widget.TextView;

import com.allanlopez.veggie_os.pojo.Exercise;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExerciseDetailActivity extends AppCompatActivity {

    private TextView name, calories, time, met;
    private NetworkImageView exerciseImg;
    private RequestQueue mQueue;
    private String imgUrl;
    private Exercise exercise;
    private String apiUrl = "https://trackapi.nutritionix.com/v2";
    private String xappid = "8b9ab5be";
    private String xappkey = "48f0d8f99f2f1f990a69f309613c814d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);
        name = (TextView) findViewById(R.id.foodName);
        exerciseImg = (NetworkImageView) findViewById(R.id.foodImage);
        calories = (TextView) findViewById(R.id.exerciseCalories);
        time = (TextView) findViewById(R.id.exerciseTime);
        met = (TextView) findViewById(R.id.met);

        mQueue = VolleySingleton.getInstance(this).getRequestQueue();

        exercise = (Exercise) getIntent().getSerializableExtra( "exercise");

        fillExercise(exercise);


    }



    private  void LoadImage(String url){

        ImageLoader imageLoader = new ImageLoader(mQueue,
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
        exerciseImg.setImageUrl(url, imageLoader);
    }

    private void fillExercise(Exercise exercise){
        name.setText(exercise.name);
        calories.setText(exercise.nf_calories);
        imgUrl = exercise.photo;
        time.setText(exercise.duration_min);
        met.setText(exercise.met);

        LoadImage(imgUrl);
    }

    public void MET(View view){
        Uri uri = Uri.parse("https://en.wikipedia.org/wiki/Metabolic_equivalent");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
