package com.grayseal.traveldiaryapp.ui.main.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.grayseal.traveldiaryapp.R
import com.grayseal.traveldiaryapp.data.model.Photo
import com.squareup.picasso.Picasso
import java.io.File


class ImagesListAdapter(
    private val context: Context,
    private val imageList: List<Photo>,
) : RecyclerView.Adapter<ImagesListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_image_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noteImage = imageList[position]
        handleLoadPicture(noteImage, holder.imageView)
    }

    private fun handleLoadPicture(image: Photo?, imageView: ImageView) {
        if (image != null) {
            val filePath = image.absolutePath
            val file = File(filePath)
            Picasso.get()
                .load(file)
                .into(imageView)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

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