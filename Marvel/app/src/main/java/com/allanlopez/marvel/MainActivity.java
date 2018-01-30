package com.allanlopez.marvel;

import android.app.Activity;
import android.app.ListActivity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.allanlopez.marvel.adapters.ItuneArrayAdapter;
import com.allanlopez.marvel.adapters.MarvelArrayAdapter;
import com.allanlopez.marvel.pojo.Heroe;
import com.allanlopez.marvel.pojo.Itune;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private  ArrayAdapter<String> arrayAdapter;
    private ItuneArrayAdapter ituneItuneArrayAdapter;
    private MarvelArrayAdapter heroeArrayAdapter;
    private ListView listView;
    private RequestQueue mQueue;
    private SeekBar seekBar;
    private Button search;
    private TextView seekBarValue;
    private int offset = 0;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        search = (Button)findViewById(R.id.search);
        seekBarValue = (TextView) findViewById(R.id.seekBarValue);
        seekBar.setProgress(0);
        seekBar.setMax(900);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 100;
                progress = progress * 100;
                seekBarValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        listView = (ListView) findViewById(R.id.lista);
        /*
        ituneItuneArrayAdapter = new ItuneArrayAdapter(this, R.layout.itunes_layout, new ArrayList<Itune>());
        listView.setAdapter(ituneItuneArrayAdapter);

        new ProcesaJson(ituneItuneArrayAdapter).execute("https://itunes.apple.com/search?term=foals");*/


        /*heroeArrayAdapter = new MarvelArrayAdapter(this, R.layout.itunes_layout, new ArrayList<Heroe>());
        listView.setAdapter(heroeArrayAdapter);*/
        //new MarvelJson(heroeArrayAdapter).execute();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapter);
        mQueue = VolleySingleton.getInstance(this).getRequestQueue();
        jsonMarvel(getMarvelString(), adapter);
    }

    /*public class ProcesaJson extends AsyncTask<String, Integer, ArrayList<Itune>>{
        private ItuneArrayAdapter adapter;
        public ProcesaJson(ItuneArrayAdapter adapter){
            this.adapter = adapter;
        }
        @Override
        protected ArrayList<Itune> doInBackground(String... urls) {
            Json json = new Json();
            String jsonString = json.serviceCall(urls[0]);
            ArrayList<Itune> arrayList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject dato = jsonArray.getJSONObject(i);
                    Itune itune = new Itune();
                    itune.collectionName = dato.getString("collectionName");
                    itune.trackName = dato.getString("trackName");
                    itune.trackPrice = dato.getDouble("trackPrice");

                    arrayList.add(itune);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Itune> strings) {
            adapter.clear();
            adapter.addAll(strings);
            adapter.notifyDataSetChanged();
        }
    }*/


    private final String LOG_TAG = "MARVEL";

    private static char[] HEXCodes = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    private void jsonMarvel(String url, final ArrayAdapter<String> adapter){
        adapter.clear();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONArray jsonArray = data.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        adapter.add(jsonObject.getString("name"));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);
    }
    private String getMarvelString(){
        String ts = Long.toString(System.currentTimeMillis() / 1000);
        String apikey = "d9c9993a63b316379f898412b03435a8";
        String hash = md5(ts + "9441edf550393ac90d79cc4a5f40308c85ae9580" + "d9c9993a63b316379f898412b03435a8");
        ArrayList<Heroe> arrayList = new ArrayList<>();


            /*
                Conexión con el getway de marvel
            */
        final String CHARACTER_BASE_URL =
                "http://gateway.marvel.com/v1/public/characters";


            /*
                Configuración de la petición
            */
        String characterJsonStr = null;
        final String TIMESTAMP = "ts";
        final String API_KEY = "apikey";
        final String HASH = "hash";
        final String ORDER = "orderBy";

        Uri builtUri;
        builtUri = Uri.parse(CHARACTER_BASE_URL+"?").buildUpon()
                .appendQueryParameter(TIMESTAMP, ts)
                .appendQueryParameter(API_KEY, apikey)
                .appendQueryParameter(HASH, hash)
                .appendQueryParameter(ORDER, "name")
                .appendQueryParameter("offset", offset + "")
                .appendQueryParameter("limit", "100")
                .build();

       return (builtUri.toString());
    }



    public static String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            String hash = new String(hexEncode(digest.digest()));
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    /*
        Investiga y reporta qué hace esta aplicación
    */
    public static String hexEncode(byte[] bytes) {
        char[] result = new char[bytes.length*2];
        int b;
        for (int i = 0, j = 0; i < bytes.length; i++) {
            b = bytes[i] & 0xff;
            result[j++] = HEXCodes[b >> 4];
            result[j++] = HEXCodes[b & 0xf];
        }
        return new String(result);
    }

    public void changeOffset(View view){
        offset = Integer.parseInt( seekBarValue.getText().toString());
        jsonMarvel(getMarvelString(), adapter);
    }
}
