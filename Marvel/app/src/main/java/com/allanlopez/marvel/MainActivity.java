package com.allanlopez.marvel;

import android.app.Activity;
import android.app.ListActivity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.allanlopez.marvel.adapters.ItuneArrayAdapter;
import com.allanlopez.marvel.pojo.Itune;

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
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*listView = findViewById(R.id.lista);
        ituneItuneArrayAdapter = new ItuneArrayAdapter(this, R.layout.itunes_layout, new ArrayList<Itune>());
        listView.setAdapter(ituneItuneArrayAdapter);

        new ProcesaJson(ituneItuneArrayAdapter).execute("https://itunes.apple.com/search?term=foals");*/

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapter);
        new MarvelJson(adapter).execute();

    }

    public class ProcesaJson extends AsyncTask<String, Integer, ArrayList<Itune>>{
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
    }


    private final String LOG_TAG = "MARVEL";

    private static char[] HEXCodes = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};


    public class MarvelJson extends AsyncTask< String, Integer,ArrayList<String>>{
        private ArrayAdapter<String> adapter;

        public MarvelJson(ArrayAdapter<String> adapter){
            this.adapter = adapter;
        }
        @Override
        protected ArrayList<String> doInBackground(String... urls) {

            /*
            Investiga y reporta qué es md5?



            */
            String ts = Long.toString(System.currentTimeMillis() / 1000);
            String apikey = "1681a9eefcf8fbf43de66c59727718da";
            String hash = md5(ts + "ede49375699321e3736436b53011574333433f40" + "1681a9eefcf8fbf43de66c59727718da");
            ArrayList<String> arrayList = new ArrayList<>();


            /*
                Conexión con el getway de marvel
            */
            final String CHARACTER_BASE_URL =
                    "http://gateway.marvel.com/v1/public/characters";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

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
                    .build();

            try {

            /*
                Ejecución de la conexión
            */
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                Log.d(LOG_TAG, "Response: " + urlConnection.getResponseCode() + " - " + urlConnection.getResponseMessage());

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }


                /*
                    JSON Obtenido
                */
                characterJsonStr = buffer.toString();

                arrayList.add(characterJsonStr);


                /*

                    Procesa el JSON y muestra el nombre de cada Marvel Character obtenido
                */


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            Log.v(LOG_TAG,arrayList.get(0));

            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            adapter.clear();
            adapter.addAll(strings);
            adapter.notifyDataSetChanged();
        }
    }


    /*
        Investiga y reporta qué es md5:

    */
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
}
