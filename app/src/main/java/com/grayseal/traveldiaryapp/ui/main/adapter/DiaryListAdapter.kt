package com.grayseal.traveldiaryapp.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.grayseal.traveldiaryapp.R
import com.grayseal.traveldiaryapp.data.model.DiaryEntry
import com.grayseal.traveldiaryapp.data.model.Photo
import com.grayseal.traveldiaryapp.ui.main.eventbus.SearchResultEvent
import org.greenrobot.eventbus.EventBus
import java.util.Locale

@SuppressLint("NotifyDataSetChanged")
class DiaryListAdapter(
    private val context: Context,
    private val entryList: MutableList<DiaryEntry>,
    private val imagesList: MutableList<Photo>,
    private val onEntryClickedListener: OnEntryClickedListener
) : RecyclerView.Adapter<DiaryListAdapter.ViewHolder>() {
    private val searchableCopy: MutableList<DiaryEntry> = ArrayList()

    init {
        searchableCopy.addAll(entryList)
    }

    /**
     * Performs a search operation based on the given search term and updates the entry list accordingly.
     *
     * @param searchTerm The search term to be used for filtering entries.
     */
    fun search(searchTerm: String?) {
        // Clear the current entry list
        entryList.clear()

        if (searchTerm != null) {
            // Check if the search term is empty
            if (searchTerm.isEmpty()) {
                // Add all entries from the searchable copy since there's no filter
                entryList.addAll(searchableCopy)
            } else {
                // Iterate through each diary entry in the searchable copy
                for (diaryEntry in searchableCopy) {
                    // Check if the search term matches any part of the entry's data
                    if (diaryEntry.title.lowercase(Locale.getDefault()).contains(
                            searchTerm.lowercase(Locale.getDefault())
                        ) || diaryEntry.notes.lowercase(Locale.getDefault()).contains(
                            searchTerm.lowercase(Locale.getDefault())
                        ) || diaryEntry.location.lowercase(Locale.getDefault()).contains(
                            searchTerm.lowercase(Locale.getDefault())
                        )
                    ) {
                        if (entryList.isEmpty()) {
                            // If the entry list is empty, directly add the matched entry
                            entryList.add(diaryEntry)
                        } else {
                            var alreadyExists = false
                            // Check if the entry already exists in the entry list
                            for (entry in entryList) {
                                if (diaryEntry.id == entry.id) {
                                    alreadyExists = true
                                    break
                                }
                            }
                            // Add the entry to the list if it doesn't already exist
                            if (!alreadyExists) {
                                entryList.add(diaryEntry)
                            }
                        }
                    }
                }
            }
        }

        // Post an event to notify about the search result
        EventBus.getDefault().post(SearchResultEvent(entryList.isEmpty()))

        // Notify the adapter about the data change
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diary_listing_view_layout, parent, false)
        return ViewHolder(view, onEntryClickedListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diaryEntry = entryList[position]
        val date = diaryEntry.date
        val entryId = diaryEntry.id
        val entryImages = imagesList.filter { it.diaryEntryId == entryId }

        holder.titleTextView.text = diaryEntry.title
        holder.locationTextView.text = diaryEntry.location
        holder.descriptionTextView.text = diaryEntry.notes
        holder.dateTextView.text = date

        val imagesListAdapter = MiniDiaryEntryImagesListAdapter(context, entryImages)
        holder.imagesRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.imagesRecyclerView.adapter = imagesListAdapter
    }

    override fun getItemCount(): Int {
        return entryList.size
    }

    fun updateSearchableList(journals: List<DiaryEntry>?) {
        searchableCopy.addAll(journals!!)
    }

    inner class ViewHolder(itemView: View, onJournalClickedListener: OnEntryClickedListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val container: MaterialCardView
        val titleTextView: TextView
        val descriptionTextView: TextView
        val locationTextView: TextView
        val dateTextView: TextView
        val imagesRecyclerView: RecyclerView
        private val entryClickedListener: OnEntryClickedListener

        init {
            container = itemView.findViewById(R.id.item_listing_container)
            titleTextView = itemView.findViewById(R.id.item_title_text_view)
            descriptionTextView = itemView.findViewById(R.id.item_description_text_view)
            locationTextView = itemView.findViewById(R.id.item_location_text_view)
            dateTextView = itemView.findViewById(R.id.item_date_text_view)
            imagesRecyclerView = itemView.findViewById(R.id.entry_images_recycler_view)
            this.entryClickedListener = onJournalClickedListener
            container.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            this.entryClickedListener.onEntryClickedClicked(adapterPosition)
        }
    }

    interface OnEntryClickedListener {
        fun onEntryClickedClicked(position: Int)
    }
}