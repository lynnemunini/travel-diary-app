package com.grayseal.traveldiaryapp.ui.main.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.grayseal.traveldiaryapp.R
import com.grayseal.traveldiaryapp.data.model.DiaryEntry
import com.grayseal.traveldiaryapp.data.model.Photo
import com.grayseal.traveldiaryapp.ui.main.adapter.DiaryListAdapter
import com.grayseal.traveldiaryapp.ui.main.eventbus.SearchResultEvent
import com.grayseal.traveldiaryapp.ui.main.view.DiaryActivity.Companion.ENTRY_ID_TAG_KEY
import com.grayseal.traveldiaryapp.ui.main.viewmodel.DiaryEntryViewModel
import com.grayseal.traveldiaryapp.ui.main.viewmodel.PhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@AndroidEntryPoint
@SuppressLint("NotifyDataSetChanged")
class MainDashboardActivity : AppCompatActivity(), DiaryListAdapter.OnEntryClickedListener {
    private lateinit var addDiaryEntryFab: FloatingActionButton
    private lateinit var diaryEntriesListRecyclerView: RecyclerView
    private lateinit var statusProgressBar: ProgressBar
    private lateinit var noDataView: View
    private lateinit var diaryListAdapter: DiaryListAdapter
    private val photoViewModel: PhotoViewModel by viewModels()
    private val diaryEntryViewModel: DiaryEntryViewModel by viewModels()
    private var entries: MutableList<DiaryEntry> = ArrayList()
    private var images: MutableList<Photo> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeResources()
        loadData()
    }

    override fun onStart() {
        super.onStart()
        try {
            EventBus.getDefault().register(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            EventBus.getDefault().unregister(this)

        } catch (ex: Exception) {
        }
    }

    private fun initializeResources() {
        addDiaryEntryFab = findViewById(R.id.add_entry_fab)
        statusProgressBar = findViewById(R.id.list_loading_progress)
        diaryEntriesListRecyclerView = findViewById(R.id.diary_recycler_view)
        noDataView = findViewById(R.id.diary_no_data_view)
        diaryListAdapter = DiaryListAdapter(applicationContext, entries, images, this)
        diaryEntriesListRecyclerView.adapter = diaryListAdapter
        diaryEntriesListRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        addDiaryEntryFab.setOnClickListener {
            startActivity(Intent(this@MainDashboardActivity, DiaryActivity::class.java))
            finish()
        }
    }

    fun emptySearchResult(isEmpty: Boolean) {
        if (isEmpty) {
            diaryEntriesListRecyclerView.visibility = View.GONE
        } else {
            diaryEntriesListRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun loadData() {
        statusProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            diaryEntryViewModel.getAllEntries().collect { diaryEntries ->
                entries.clear() // Clear the existing list
                statusProgressBar.visibility = View.GONE
                entries.addAll(diaryEntries) // Add the collected entries
                diaryListAdapter.notifyDataSetChanged()
                diaryListAdapter.updateSearchableList(entries)
                if (entries.isNotEmpty()) {
                    noDataView.visibility = View.GONE
                    diaryEntriesListRecyclerView.visibility = View.VISIBLE
                } else {
                    noDataView.visibility = View.VISIBLE
                    diaryEntriesListRecyclerView.visibility = View.GONE
                }
            }
        }
        lifecycleScope.launch {
            photoViewModel.getAllEntries().collect { photos ->
                images.clear()
                images.addAll(photos)
                diaryListAdapter.notifyDataSetChanged()
            }
        }
    }

    @Subscribe
    fun emptySearchResultEvent(event: SearchResultEvent) {
        if (event.isEmpty) {
            diaryEntriesListRecyclerView.visibility = View.GONE
        } else {
            diaryEntriesListRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onEntryClickedClicked(position: Int) {
        val entry = entries[position]
        val intent = Intent(applicationContext, DiaryActivity::class.java)
        intent.putExtra(ENTRY_ID_TAG_KEY, entry.id)
        startActivity(intent)
        finish()
    }
}