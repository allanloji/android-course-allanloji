package com.allanlopez.veggie_os;

import android.graphics.Bitmap;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.LruCache;
import android.widget.TextView;

import com.allanlopez.veggie_os.pojo.Food;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FoodDetailActivity extends AppCompatActivity {
    private TextView name, calories, serving, total_fat, saturated_fat, cholesterol, sodium, carbohydrate
    , dietary, protein, potassium, walkMinutes, runMinutes, cycleMinutes;
    private NetworkImageView foodImg;
    private RequestQueue mQueue;
    private String foodName, foodPhoto;
    private String imgUrl;
    private String apiUrl = "https://trackapi.nutritionix.com/v2/natural/nutrients";
    private String xappid = "8b9ab5be";
    private String xappkey = "48f0d8f99f2f1f990a69f309613c814d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        name = (TextView) findViewById(R.id.foodName);
        foodImg = (NetworkImageView) findViewById(R.id.foodImage);
        calories = (TextView) findViewById(R.id.calories);
        serving = (TextView) findViewById(R.id.serving);
        total_fat = (TextView) findViewById(R.id.totalFat);
        saturated_fat = (TextView) findViewById(R.id.saturatedFat);
        cholesterol = (TextView) findViewById(R.id.cholesterol);
        sodium = (TextView) findViewById(R.id.sodium);
        carbohydrate = (TextView) findViewById(R.id.carbohidrates);
        dietary = (TextView) findViewById(R.id.dietary);
        protein = (TextView) findViewById(R.id.protein);
        potassium = (TextView) findViewById(R.id.potassium);
        walkMinutes = (TextView) findViewById(R.id.walkMinutes);
        runMinutes = (TextView) findViewById(R.id.runMinutes);
        cycleMinutes = (TextView) findViewById(R.id.cycleMinutes);

        mQueue = VolleySingleton.getInstance(this).getRequestQueue();

        foodName = (String) getIntent().getSerializableExtra( "food_name");
        foodPhoto = (String) getIntent().getSerializableExtra( "food_photo");

        try {
            jsonNutrients(apiUrl, foodName,foodPhoto);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /* Json WEB*/
    //-----POST Nutrients
    private void jsonNutrients(String url, final String food_name, final String photo) throws JSONException {
        JSONObject postparams=new JSONObject();
        postparams.put("query",food_name);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("foods");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Food food = new Food();
                    food.food_name = jsonObject.getString("food_name");
                    food.photo = photo;
                    food.serving_qty = jsonObject.getString("serving_qty");
                    food.serving_unit = jsonObject.getString("serving_unit");
                    food.serving_weight_grams = jsonObject.getString("serving_weight_grams");
                    food.nf_calories = jsonObject.getDouble("nf_calories");
                    food.nf_total_fat = jsonObject.getString("nf_total_fat");
                    food.nf_saturated_fat = jsonObject.getString("nf_saturated_fat");
                    food.nf_cholesterol = jsonObject.getString("nf_cholesterol");
                    food.nf_sodium = jsonObject.getString("nf_sodium");
                    food.nf_total_carbohydrate = jsonObject.getString("nf_total_carbohydrate");
                    food.nf_dietary_fiber = jsonObject.getString("nf_dietary_fiber");
                    food.nf_sugars = jsonObject.getString("nf_sugars");
                    food.nf_protein = jsonObject.getString("nf_protein");
                    food.nf_potassium = jsonObject.getString("nf_potassium");
                    name.setText(food.food_name);
                    imgUrl = food.photo;
                    name.setText(food.food_name.substring(0,1).toUpperCase() + food.food_name.substring(1));
                    calories.setText(food.nf_calories + "");
                    serving.setText(food.serving_qty + " " + food.serving_unit);
                    total_fat.setText(food.nf_total_fat);
                    saturated_fat.setText(food.nf_saturated_fat);
                    cholesterol.setText(food.nf_cholesterol);
                    sodium.setText(food.nf_sodium);
                    carbohydrate.setText(food.nf_total_carbohydrate);
                    dietary.setText(food.nf_dietary_fiber);
                    protein.setText(food.nf_protein);
                    potassium.setText(food.nf_potassium);
                    CalculateCalories((Double) food.nf_calories);
                    LoadImage(imgUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("x-app-id", xappid);
                headers.put("x-app-key", xappkey);
                return headers;
            }
        };
        mQueue.add(request);
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

    public void CalculateCalories(double calories){
        int walk = 0;
        int run = 0;
        int cycle = 0;
        walk = (int) ((calories*25) / 100);
        run = (int) ((calories*10) / 100);
        cycle = (int) ((calories*15) / 100);
        walkMinutes.setText(walk + " min");
        runMinutes.setText(run + " min");
        cycleMinutes.setText(cycle + " min");
    }

}
