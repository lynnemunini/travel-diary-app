package com.grayseal.traveldiaryapp.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grayseal.traveldiaryapp.R
import com.grayseal.traveldiaryapp.data.model.DiaryEntry
import com.grayseal.traveldiaryapp.data.model.Photo
import com.grayseal.traveldiaryapp.ui.main.adapter.ImagesListAdapter
import com.grayseal.traveldiaryapp.utils.ProcessAndroidUri
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class DiaryActivity : AppCompatActivity() {

    private lateinit var closeDiaryForm: ImageView
    private lateinit var saveButton: TextView
    private lateinit var dateTextView: TextView

    //    private lateinit var dateLayout: View
    private lateinit var titleEditText: EditText
    private lateinit var entryBodyEditText: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var addImageView: View
    private lateinit var addLocationView: View
    private lateinit var containerView: View
    private lateinit var capturedImagesRecyclerView: RecyclerView
    private lateinit var imagesListAdapter: ImagesListAdapter
    private lateinit var capturedImagesContainerView: View
    private var imageFile: File? = null
    private lateinit var currentDate: Calendar
    private var diaryEntryID: UUID = UUID.randomUUID()
    private var diaryEntry: DiaryEntry? = null
    private val imageFilesList: MutableList<Photo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_layout)
        initializeResources()
    }

    private fun initializeResources() {
        closeDiaryForm = findViewById(R.id.close_diary_form)
        saveButton = findViewById(R.id.save_fab)
        dateTextView = findViewById(R.id.date_text_view)
        titleEditText = findViewById(R.id.note_title_edit_text)
        entryBodyEditText = findViewById(R.id.note_body_edit_text)
        addImageView = findViewById(R.id.add_image_view)
        progressBar = findViewById(R.id.progress_bar)
        addLocationView = findViewById(R.id.add_location)
        containerView = findViewById(R.id.form_container_view)
        capturedImagesContainerView = findViewById(R.id.form_capture_photos_card_view)
        capturedImagesRecyclerView = findViewById(R.id.captured_images_recycler_view)
        currentDate = Calendar.getInstance()
        capturedImagesContainerView.visibility = View.GONE
        imagesListAdapter = ImagesListAdapter(applicationContext, imageFilesList)
        capturedImagesRecyclerView.adapter = imagesListAdapter
        capturedImagesRecyclerView.layoutManager = GridLayoutManager(applicationContext, 3)
        dateTextView.text =
            SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(currentDate.time)

        closeDiaryForm.setOnClickListener {
            onBackPressed()
        }
        saveButton.setOnClickListener {
        }

        addImageView.setOnClickListener { launchImageCapture() }
    }

    private fun launchImageCapture() {
        handleChoosePhotoFromFiles()
    }

    private fun handleChoosePhotoFromFiles() {
        progressBar.visibility = View.VISIBLE
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser =
            Intent.createChooser(intent, "Choose Picture")
        startActivityForResult(chooser, GET_CONTENT_FROM_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        progressBar.visibility = View.GONE
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_CONTENT_FROM_FILE_REQUEST_CODE) {
            if (data == null) {
                Toast.makeText(applicationContext, "Image capture failed!", Toast.LENGTH_LONG)
                    .show()
            } else {
                try {
                    imageFile = data.data?.let { ProcessAndroidUri.from(applicationContext, it) }
                    if (imageFile?.length()!! < 1) return
                    val diaryEntryImage =
                        imageFile?.absolutePath?.let {
                            Photo(id = UUID.randomUUID(), diaryEntryId = diaryEntryID, it)
                        }

                    // TODO: Store the diaryEntry Image

                    capturedImagesContainerView.visibility = View.VISIBLE
                    diaryEntryImage?.let { imageFilesList.add(it) }
                    imagesListAdapter.notifyDataSetChanged()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, MainDashboardActivity::class.java))
        finish()
    }

    companion object {
        private const val GET_CONTENT_FROM_FILE_REQUEST_CODE = 1203
    }
}