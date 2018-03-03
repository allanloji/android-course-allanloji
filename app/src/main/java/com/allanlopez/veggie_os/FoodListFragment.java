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
import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class FoodListFragment extends ListFragment {
    private FoodArrayAdapter foodArrayAdapter;
    private RequestQueue mQueue;
    private ListView listView;

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
        foodArrayAdapter = getAdapter();
        setListAdapter(foodArrayAdapter);
        mQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Food food = foodArrayAdapter.getItem((int)id);
                Intent intent = new Intent(getActivity(), FoodDetailActivity.class);
                intent.putExtra("id", food.id);
                startActivity(intent);
            }
        });



    }

    private FoodArrayAdapter getAdapter(){
        FoodArrayAdapter adapter = new FoodArrayAdapter(
                getActivity(), R.layout.food_layout,
                new ArrayList<Food>());
        try {
            JSONObject jsonObject = new JSONObject(getJSON());
            JSONArray jsonArray = jsonObject.getJSONArray("food");
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject01 = jsonArray.getJSONObject(i);
                Food food = new Food();
                food.id = jsonObject01.getString("id");
                food.name = jsonObject01.getString("name");
                food.calories = jsonObject01.getString("calories");
                food.imgUrl = jsonObject01.getString("imgUrl");
                adapter.add(food);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return adapter;
    }

    /* Json Local */
    private String getJSON(){
        try {
            InputStream inputStream = getActivity().getAssets().open("food.json");
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
}
