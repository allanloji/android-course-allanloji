package com.allanlopez.veggie_os;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.allanlopez.veggie_os.adapters.FoodArrayAdapter;
import com.allanlopez.veggie_os.pojo.Food;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FoodListFragment extends ListFragment {
    private FoodArrayAdapter foodArrayAdapter;
    private RequestQueue mQueue;
    private ListView listView;
    private String[] commonFood = {"Apple", "Taco meat", "Donut", "Pizza", "Soda","Hotdog",
                                    "Mango", "Cheesecake", "Ham sandwich", "Apple salad" };
    private String apiUrl = "https://trackapi.nutritionix.com/v2";
    private String xappid = "8b9ab5be";
    private String xappkey = "48f0d8f99f2f1f990a69f309613c814d";


    public FoodListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_food_list, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        foodArrayAdapter = new FoodArrayAdapter(this.getActivity(), R.layout.fragment_food_list, new ArrayList<Food>());
        setListAdapter(foodArrayAdapter);
        mQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        for (int i = 0; i < commonFood.length; i++){
            jsonFood(apiUrl+ "/search/instant?query=" + commonFood[i], foodArrayAdapter);
        }


        listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Food food = foodArrayAdapter.getItem((int)id);
                Intent intent = new Intent(getActivity(), FoodDetailActivity.class);
                intent.putExtra("food_name", food.food_name);
                intent.putExtra("food_photo", food.photo);
                startActivity(intent);
            }
        });



    }




    /* Json WEB*/
    //-----GET Food
    private void jsonFood(String url, final FoodArrayAdapter adapter){
        adapter.clear();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("common");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONObject photo = jsonObject.getJSONObject("photo");
                    Food food = new Food();
                    food.food_name = jsonObject.getString("food_name");
                    food.serving_unit = jsonObject.getString("serving_unit");
                    food.serving_qty = jsonObject.getString("serving_qty");
                    food.photo = photo.getString("thumb");
                    adapter.add(food);
                    adapter.notifyDataSetChanged();
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
}
