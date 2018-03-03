package com.allanlopez.veggie_os;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.widget.TextView;

import com.allanlopez.veggie_os.pojo.Food;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class FoodDetailActivity extends AppCompatActivity {
    private TextView name, description, calories;
    private NetworkImageView foodImg;
    private RequestQueue mQueue;
    private String foodId;
    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        name = (TextView) findViewById(R.id.foodName);
        foodImg = (NetworkImageView) findViewById(R.id.foodImage);
        calories = (TextView) findViewById(R.id.calories);
        description = (TextView) findViewById(R.id.foodDescription);

        mQueue = VolleySingleton.getInstance(this).getRequestQueue();

        foodId = (String) getIntent().getSerializableExtra( "id");

        fillFood();


    }

    private String getJSON(){
        try {
            InputStream inputStream = this.getAssets().open("food.json");
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
        foodImg.setImageUrl(url, imageLoader);
    }

    private void fillFood(){
        try{
            JSONObject jsonObject = new JSONObject(getJSON());
            JSONArray jsonArray = jsonObject.getJSONArray("food");
            JSONObject jsonObject01 = jsonArray.getJSONObject(Integer.parseInt(foodId));
            name.setText(jsonObject01.getString("name"));
            calories.setText(jsonObject01.getString("calories"));
            imgUrl = jsonObject01.getString("imgUrl");
            description.setText(jsonObject01.getString("description"));

            LoadImage(imgUrl);

        }catch (JSONException e) {
            e.printStackTrace();
        }




    }
}
