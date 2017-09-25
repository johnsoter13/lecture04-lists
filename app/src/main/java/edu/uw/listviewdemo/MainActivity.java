package edu.uw.listviewdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText searchField = (EditText)findViewById(R.id.txt_search);
        final ImageButton searchButton = (ImageButton)findViewById(R.id.btn_download);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //downloadDinosaurData();

                String searchTerm = searchField.getText().toString();
                downloadMediaData(searchTerm);
            }
        });


        //model!
//        String[] data = new String[99];
//        for(int i=99; i>0; i--){
//            data[99-i] = i+ " bottles of beer on the wall";
//        }
        ArrayList<String> data = new ArrayList<String>(); //empty data; ArrayList so modifiable

        //controller
        adapter = new ArrayAdapter<String>(
                this, R.layout.list_item, R.id.txtItem, data);

        //supports ListView or GridView
        AdapterView listView = (AdapterView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String movie = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "You clicked on " + movie);
            }
        });
    }

    //download dinosaur names from dinoipsum
    private void downloadDinosaurData() {

        String urlString = "http://dinoipsum.herokuapp.com/api/?format=text&words=20&paragraphs=1";

        Request request = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.v(TAG, response);

                        String[] dinos = response.split(" "); //turn string into an array

                        adapter.clear();
                        for(String dino : dinos) {
                            adapter.add(dino);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                    }
                });

//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(request);

        RequestSingleton.getInstance(this).add(request);
    }

    //search for media from iTunes
    private void downloadMediaData(String searchTerm) {

        String urlString = "";
        try {
            urlString = "https://itunes.apple.com/search?term="+URLEncoder.encode(searchTerm, "UTF-8")+"&media=movie&limit=25";
        }catch(UnsupportedEncodingException uee){
            Log.e(TAG, uee.toString());
            return;
        }

        Request request = new JsonObjectRequest(Request.Method.GET, urlString, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        ArrayList<String> mediaTitles = new ArrayList<String>();

                        try {
                            //parse the JSON results
                            JSONArray results = response.getJSONArray("results");
                            for(int i=0; i<results.length(); i++){
                                JSONObject track = results.getJSONObject(i);
                                String title = track.getString("trackName");
                                String artist = track.getString("artistName");
                                mediaTitles.add(title+" ("+artist+")");
                                Log.v(TAG, mediaTitles.get(mediaTitles.size()-1));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter.clear();
                        for(String media : mediaTitles) {
                            adapter.add(media);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });

        RequestSingleton.getInstance(this).add(request);
    }

    protected static class RequestSingleton {
        //the single RequestQueue; static so it exists outside of the object
        private static RequestQueue requestQueue = null;

        private RequestSingleton(){} //private constructor; cannot instantiate directly

        //call this "factory" method to access the Volley RequestQueue
        public static RequestQueue getInstance(Context ctx) {
            //only create if it doesn't exist yet
            if(requestQueue == null){
                requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
            }

            return requestQueue; //return the single object
        }
    }

}
