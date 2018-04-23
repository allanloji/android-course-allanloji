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
import com.allanlopez.veggie_os.adapters.FoodArrayAdapter;
import com.allanlopez.veggie_os.pojo.Exercise;
import com.allanlopez.veggie_os.pojo.Food;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExerciseListFragment extends ListFragment {

    private ExerciseArrayAdapter exerciseArrayAdapter;
    private RequestQueue mQueue;
    private ListView listView;
    private String[] commonExercises = {"30 min yoga", "20 squats", "10 pullups", "1km walking",
            "1 hour swimming","2km running", "20 pushups", "20 min crossfit", "20 min basquetball",
            "30 min weight lifting"};
    private String apiUrl = "https://trackapi.nutritionix.com/v2";
    private String xappid = "8b9ab5be";
    private String xappkey = "48f0d8f99f2f1f990a69f309613c814d";

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
        exerciseArrayAdapter = new ExerciseArrayAdapter(this.getActivity(),
                R.layout.fragment_exercise_list, new ArrayList<Exercise>());
        setListAdapter(exerciseArrayAdapter);
        mQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        listView = getListView();
        for (int i = 0; i < commonExercises.length; i++){
            try {
                jsonExercise(apiUrl+ "/natural/exercise", commonExercises[i], exerciseArrayAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Exercise exercise = exerciseArrayAdapter.getItem((int)id);
                Intent intent = new Intent(getActivity(), ExerciseDetailActivity.class);
                intent.putExtra("exercise", exercise);
                startActivity(intent);
            }
        });



    }


    /* Json WEB*/
    //-----GET Exercise
    private void jsonExercise(String url, String exercise, final ExerciseArrayAdapter adapter) throws JSONException {
        adapter.clear();

        JSONObject postparams=new JSONObject();
        postparams.put("query",exercise);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("exercises");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONObject photo = jsonObject.getJSONObject("photo");
                    Exercise exercise = new Exercise();
                    exercise.tag_id = jsonObject.getString("tag_id");
                    exercise.name = jsonObject.getString("name");
                    exercise.name = exercise.name.substring(0,1).toUpperCase() + exercise.name.substring(1);
                    exercise.duration_min = jsonObject.getString("duration_min");
                    exercise.met = jsonObject.getString("met");
                    exercise.nf_calories = jsonObject.getString("nf_calories");
                    exercise.photo = photo.getString("thumb");
                    adapter.add(exercise);
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
