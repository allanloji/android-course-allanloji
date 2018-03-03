package com.allanlopez.veggie_os;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.LruCache;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class ExerciseDetailActivity extends AppCompatActivity {

    private TextView name, description, calories, time;
    private NetworkImageView exerciseImg;
    private RequestQueue mQueue;
    private String exerciseId;
    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);
        name = (TextView) findViewById(R.id.exerciseName);
        exerciseImg = (NetworkImageView) findViewById(R.id.exerciseImage);
        calories = (TextView) findViewById(R.id.exerciseCalories);
        description = (TextView) findViewById(R.id.exerciseDescription);
        time = (TextView) findViewById(R.id.timeExercise);

        mQueue = VolleySingleton.getInstance(this).getRequestQueue();

        exerciseId = (String) getIntent().getSerializableExtra( "id");

        fillExercise();


    }

    private String getJSON(){
        try {
            InputStream inputStream = this.getAssets().open("exercises.json");
            int s = inputStream.available();
            byte[] archivo = new byte[s];
            inputStream.read(archivo);
            inputStream.close();
            return new String(archivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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

    private void fillExercise(){
        try{
            JSONObject jsonObject = new JSONObject(getJSON());
            JSONArray jsonArray = jsonObject.getJSONArray("exercise");
            JSONObject jsonObject01 = jsonArray.getJSONObject(Integer.parseInt(exerciseId));
            name.setText(jsonObject01.getString("name"));
            calories.setText(jsonObject01.getString("calories"));
            imgUrl = jsonObject01.getString("imgUrl");
            description.setText(jsonObject01.getString("description"));
            time.setText(jsonObject01.getString("time"));

            LoadImage(imgUrl);

        }catch (JSONException e) {
            e.printStackTrace();
        }




    }
}
