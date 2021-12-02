package com.raulmacias.searchablespinnerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.raulmacias.searchablespinner.SearchableSpinner

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var listNames = listOf<String>("Hello", "World","GoodBye","Hell")

        val searchableSpinner = findViewById<SearchableSpinner>(R.id.searchableSpinner)
        searchableSpinner.adapter = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item, listNames)
        searchableSpinner.setDialogTitle("Searcheable Spinner")
    }
}