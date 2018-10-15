package edu.uw.listviewdemo

import android.app.DownloadManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var adapter: ArrayAdapter<String> //late init means it will be defined later, specifying that this code is null but won't be later

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchField = findViewById<EditText>(R.id.txt_search)
        val searchButton = findViewById<ImageButton>(R.id.btn_download)
        searchButton.setOnClickListener {
            Log.v(TAG, "Clicked!")

            val url = "http://dinoipsum.herkuapp.com/api/?format=text&words=20&paragraphs=1"

            val requests = StringRequest(Request.Method.GET, url,
                    Response.Listener<String> {
                        Log.v(TAG, it)

                        val dinoArray = it.split(" ")
                        adapter.clear()
                        for(dino in dinoArray) {
                            adapter.add(dino)
                        }

                    },
                    Response.ErrorListener {
                        Log.e(TAG, it.toString())
                    })
            val requestQueue: RequestQueue = Volley.newRequestQueue(this.applicationContext)

            requestQueue.add(requests)
        }
         // model
        val data = mutableListOf<String>()
        for(i in 1 until 99) {
            data.add("$i bottles of beer on the wall")
        }

        // view


        // controller
        adapter = ArrayAdapter<String>(
                this,
                R.layout.list_item,
                R.id.txt_item,
                data

        )

        val listView = findViewById<ListView>(R.id.list_view)
        listView.setOnItemClickListener { parent, view, position, id ->
            val stringClicked = parent.getItemAtPosition(position)
            Toast.makeText(this, "you clicked $stringClicked", Toast.LENGTH_SHORT)

        }
        listView.adapter = adapter
    }
}
