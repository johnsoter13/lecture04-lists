package edu.uw.listviewdemo

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.util.LruCache
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import org.json.JSONException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var adapter: ArrayAdapter<String> //will be assigned later (but will not be null!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchField = findViewById<EditText>(R.id.txt_search)
        val searchButton = findViewById<ImageButton>(R.id.btn_download)

        searchButton.setOnClickListener {
            downloadDinosaurData();

            //val searchTerm = searchField.text.toString()
            //downloadMediaData(searchTerm)
        }

        //model!
        val data = mutableListOf<String>()
        for(i in 99 downTo 1){
            data.add(i.toString() + " bottles of beer on the wall")
        }

        //controller
        adapter = ArrayAdapter(this, R.layout.list_item, R.id.txtItem, data)

        //supports ListView or GridView
        val listView = findViewById<AdapterView<ArrayAdapter<String>>>(R.id.list_view) //wow
        listView.setAdapter(adapter)


        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as String
            Toast.makeText(this, "You clicked on $item", Toast.LENGTH_SHORT).show()
        }


        //load an image off the internet
        val imageView = findViewById<NetworkImageView>(R.id.img_remote)
        imageView.setImageUrl("https://dinoxp.com/wp-content/uploads/2016/02/dinort.png",
                VolleyService.getInstance(this).imageLoader)
    }

    //download dinosaur names from dinoipsum
    private fun downloadDinosaurData() {

        val urlString = "http://dinoipsum.herokuapp.com/api/?format=text&words=20&paragraphs=1"

        val request = StringRequest(Request.Method.GET, urlString,
                Response.Listener { response ->
                    Log.v(TAG, response)

                    val dinos = response.split(" ") //turn string into an array

                    adapter.clear()
                    for (dino in dinos) {
                        adapter.add(dino)
                    }
                }, Response.ErrorListener { error -> Log.e(TAG, error.toString()) })

        //        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //        requestQueue.add(request);

        VolleyService.getInstance(this).add(request)
    }

    //search for media from iTunes
    private fun downloadMediaData(searchTerm: String) {

        var urlString = ""
        try {
            urlString = "https://itunes.apple.com/search?term=" + URLEncoder.encode(searchTerm, "UTF-8") + "&media=movie&limit=25"
        } catch (uee: UnsupportedEncodingException) {
            Log.e(TAG, uee.toString())
            return
        }

        val request = JsonObjectRequest(Request.Method.GET, urlString, null,
                Response.Listener { response ->
                    val mediaTitles = ArrayList<String>()

                    try {
                        //parse the JSON results
                        val results = response.getJSONArray("results")
                        for (i in 0 until results.length()) {
                            val track = results.getJSONObject(i)
                            val title = track.getString("trackName")
                            val artist = track.getString("artistName")
                            mediaTitles.add("$title ($artist)")
                            Log.v(TAG, mediaTitles[mediaTitles.size - 1])
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    adapter.clear()
                    for (media in mediaTitles) {
                        adapter.add(media)
                    }
                }, Response.ErrorListener { error -> Log.e(TAG, error.toString()) })

        VolleyService.getInstance(this).add(request)
    }

    //A class to manage the Volley requestQueue as a singleton
    private class VolleyService private constructor(ctx: Context) { //private constructor; cannot instantiate directly
        companion object {
            private var instance: VolleyService? = null //the single instance of this singleton

            //call this "factory" method to access the Singleton
            fun getInstance(ctx: Context): VolleyService {
                //only create the singleton if it doesn't exist yet
                if (instance == null) {
                    instance = VolleyService(ctx)
                }

                return instance as VolleyService //force casting
            }
        }

        //from Kotlin docs
        val requestQueue: RequestQueue by lazy {
            Volley.newRequestQueue(ctx.applicationContext) //return the context-based requestQueue
        }

        //from Kotlin docs
        val imageLoader: ImageLoader by lazy {
            ImageLoader(requestQueue,
                    object : ImageLoader.ImageCache {
                        private val cache = LruCache<String, Bitmap>(20)
                        override fun getBitmap(url: String): Bitmap? {
                            return cache.get(url)
                        }
                        override fun putBitmap(url: String, bitmap: Bitmap) {
                            cache.put(url, bitmap)
                        }
                    })
        }

        //convenience wrapper method
        fun <T> add(req: Request<T>) {
            requestQueue.add(req)
        }

    }
}
