package com.allanlopez.veggie_os;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.allanlopez.veggie_os.adapters.ExerciseArrayAdapter;
import com.allanlopez.veggie_os.pojo.Exercise;
import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExerciseListFragment extends ListFragment {

    private ExerciseArrayAdapter exerciseArrayAdapter;
    private RequestQueue mQueue;
    private ListView listView;

    public ExerciseListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exercise_list, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        exerciseArrayAdapter = getAdapter();
        setListAdapter(exerciseArrayAdapter);
        mQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Exercise exercise = exerciseArrayAdapter.getItem((int)id);
                Intent intent = new Intent(getActivity(), ExerciseDetailActivity.class);
                intent.putExtra("id", exercise.id);
                startActivity(intent);
            }
        });



    }

    private ExerciseArrayAdapter getAdapter(){
        ExerciseArrayAdapter adapter = new ExerciseArrayAdapter(
                getActivity(), R.layout.exercise_layout,
                new ArrayList<Exercise>());
        try {
            JSONObject jsonObject = new JSONObject(getJSON());
            JSONArray jsonArray = jsonObject.getJSONArray("exercise");
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject01 = jsonArray.getJSONObject(i);
                Exercise exercise = new Exercise();
                exercise.id = jsonObject01.getString("id");
                exercise.name = jsonObject01.getString("name");
                exercise.nf_calories = jsonObject01.getString("calories");
                exercise.photo = jsonObject01.getString("imgUrl");
                exercise.duration_min = jsonObject01.getString("time");
                adapter.add(exercise);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return adapter;
    }

    /* Json Local */
    private String getJSON(){
        try {
            InputStream inputStream = getActivity().getAssets().open("exercises.json");
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
