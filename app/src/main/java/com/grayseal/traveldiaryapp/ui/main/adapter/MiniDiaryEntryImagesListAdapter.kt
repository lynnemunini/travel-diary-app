package com.grayseal.traveldiaryapp.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.grayseal.traveldiaryapp.R
import com.grayseal.traveldiaryapp.data.model.Photo
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Adapter class for displaying a list of small diary entry images in a horizontal RecyclerView.
 *
 * @param context The context in which the adapter is used.
 * @param imageList The list of small diary entry images to be displayed.
 */
class MiniDiaryEntryImagesListAdapter(
    private val context: Context,
    private val imageList: List<Photo>
) : RecyclerView.Adapter<MiniDiaryEntryImagesListAdapter.ViewHolder>() {

    /**
     * Creates a new ViewHolder for displaying the item view.
     *
     * @param parent The parent ViewGroup in which the new ViewHolder will be added.
     * @param viewType The type of the new view.
     * @return A new ViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_image_view_layout, parent, false)
        return ViewHolder(view)
    }

    /**
     * Binds data to the ViewHolder's views.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val journalImage = imageList[position]
        val layoutParams = holder.parentContainer.layoutParams as RecyclerView.LayoutParams
        if (position == 0) {
            layoutParams.marginStart = 0  // Exclude margin adjustment for the first element
        } else {
            layoutParams.marginStart = -100  // Apply negative margin for other elements
        }
        holder.parentContainer.layoutParams = layoutParams
        handleLoadPicture(journalImage, holder.imageSmallView)
    }

    /**
     * Loads an image into an ImageView using Picasso.
     *
     * @param image The Photo instance representing the image.
     * @param imageView The ImageView to load the image into.
     */
    private fun handleLoadPicture(image: Photo?, imageView: ImageView) {
        if (image != null) {
            val filePath = image.absolutePath
            val file = File(filePath)
            Picasso.get().load(file).into(imageView)

        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    /**
     * ViewHolder class for holding views associated with each item in the RecyclerView.
     *
     * @param itemView The view representing the item.
     */
    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val containerSmall: View
        val imageSmallView: ImageView
        val parentContainer: View

        init {
            containerSmall = itemView.findViewById(R.id.item_image_container)
            imageSmallView = itemView.findViewById(R.id.item_small_image_view)
            parentContainer = itemView.findViewById(R.id.image_layout)
        }
    }
}