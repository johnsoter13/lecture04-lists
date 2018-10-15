package edu.uw.listviewdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchField = findViewById<EditText>(R.id.txt_search)
        val searchButton = findViewById<ImageButton>(R.id.btn_download)
        searchButton.setOnClickListener {
            Log.v(TAG, "Clicked!")
        }



    }
}
