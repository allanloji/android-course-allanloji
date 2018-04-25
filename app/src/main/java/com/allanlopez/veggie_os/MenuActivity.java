package com.allanlopez.veggie_os;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.allanlopez.veggie_os.adapters.CustomSuggestionAdapter;
import com.allanlopez.veggie_os.pojo.Food;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MaterialSearchBar searchBar;
    private List<Food> suggestions = new ArrayList<>();
    private CustomSuggestionAdapter customSuggestionsAdapter;
    private RequestQueue mQueue;

    private String apiUrl = "https://trackapi.nutritionix.com/v2/search/instant";
    private String xappid = "8b9ab5be";
    private String xappkey = "48f0d8f99f2f1f990a69f309613c814d";


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_food:
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.food, new FoodListFragment());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_ejercicio:
                    fragmentManager = getFragmentManager();
                     fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.food, new ExerciseListFragment());
                    fragmentTransaction.commit();
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.food, new FoodListFragment());
        fragmentTransaction.commit();

        mQueue = VolleySingleton.getInstance(this).getRequestQueue();

        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final CustomSuggestionAdapter customSuggestionsAdapter = new CustomSuggestionAdapter(inflater);
        List<Food> suggestions = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            Food food = new Food();
            food.food_name = "Hola";
            suggestions.add(food);
        }

        customSuggestionsAdapter.setSuggestions(suggestions);
        searchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + searchBar.getText());
                try {
                    jsonFood(apiUrl + "?query=" + searchBar.getText(),customSuggestionsAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // send the entered text to our filter and let it manage everything
                customSuggestionsAdapter.getFilter().filter(searchBar.getText());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

    }

    public void SearchResults(CustomSuggestionAdapter customSuggestionAdapter){


    }

    public void ClickFood(View view){
        Log.e("CLICK", "se hizo click");
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_about) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*----------------------JSON------------------------------*/
    /* Json WEB*/
    //-----GET Instant
    private void jsonFood(String url, final CustomSuggestionAdapter customSuggestionAdapter) throws JSONException {
        JSONObject postparams=new JSONObject();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    List<Food> suggestions = new ArrayList<>();
                    JSONArray jsonArray = response.getJSONArray("common");
                    int index = 0;
                    if(jsonArray.length() != 0) {
                        if(jsonArray.length() < 10){
                            index = jsonArray.length();
                        }else {
                            index = 10;
                        }
                        for (int i = 0; i < index; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Food food = new Food();
                            food.food_name = jsonObject.getString("food_name");
                            food.serving_qty = jsonObject.getString("serving_qty");
                            food.serving_unit = jsonObject.getString("serving_unit");
                            suggestions.add(food);
                        }

                        customSuggestionAdapter.setSuggestions(suggestions);
                        searchBar.setCustomSuggestionAdapter(customSuggestionAdapter);
                    }

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
