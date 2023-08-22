package com.grayseal.traveldiaryapp.ui.main.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.grayseal.traveldiaryapp.R

class MainDashboardActivity : AppCompatActivity() {
    private lateinit var addDiaryEntryFab: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeResources()

    }

    private fun initializeResources() {
        addDiaryEntryFab = findViewById(R.id.add_entry_fab)
        addDiaryEntryFab.setOnClickListener {
            startActivity(Intent(this@MainDashboardActivity, DiaryActivity::class.java))
            finish()
        }
    }
}