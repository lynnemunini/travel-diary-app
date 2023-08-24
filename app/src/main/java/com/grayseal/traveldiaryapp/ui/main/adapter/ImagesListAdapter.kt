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
 * Adapter class for displaying a list of images in a RecyclerView.
 *
 * @param context The context in which the adapter is used.
 * @param imageList The list of images to be displayed.
 */
class ImagesListAdapter(
    private val context: Context,
    private val imageList: List<Photo>,
) : RecyclerView.Adapter<ImagesListAdapter.ViewHolder>() {

    /**
     * Creates a new ViewHolder by inflating the item layout.
     *
     * @param parent The parent view group in which the ViewHolder will be contained.
     * @param viewType The view type of the new ViewHolder.
     * @return A new ViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_image_layout, parent, false)
        return ViewHolder(view)
    }

    /**
     * Binds data to the ViewHolder by loading and displaying the image.
     *
     * @param holder The ViewHolder instance.
     * @param position The position of the image in the list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noteImage = imageList[position]
        handleLoadPicture(noteImage, holder.imageView)
    }

    /**
     * Loads and displays an image in the ImageView.
     *
     * @param image The Photo instance representing the image to be loaded.
     * @param imageView The ImageView in which the image will be displayed.
     */
    private fun handleLoadPicture(image: Photo?, imageView: ImageView) {
        if (image != null) {
            val filePath = image.absolutePath
            val file = File(filePath)
            Picasso.get()
                .load(file)
                .into(imageView)
        }
    }

    /**
     * Returns the number of items in the adapter's data set.
     *
     * @return The number of items in the imageList.
     */
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
        private val container: View
        val imageView: ImageView

        init {
            imageView = itemView.findViewById(R.id.item_image_view)
            container = itemView.findViewById(R.id.item_image_container)
        }
    }
}
