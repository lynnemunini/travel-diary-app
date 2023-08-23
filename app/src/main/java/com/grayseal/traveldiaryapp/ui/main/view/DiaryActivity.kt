package com.grayseal.traveldiaryapp.ui.main.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grayseal.traveldiaryapp.R
import com.grayseal.traveldiaryapp.data.model.DiaryEntry
import com.grayseal.traveldiaryapp.data.model.Photo
import com.grayseal.traveldiaryapp.ui.main.adapter.ImagesListAdapter
import com.grayseal.traveldiaryapp.ui.main.viewmodel.DiaryEntryViewModel
import com.grayseal.traveldiaryapp.ui.main.viewmodel.PhotoViewModel
import com.grayseal.traveldiaryapp.utils.ProcessAndroidUri
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class DiaryActivity : AppCompatActivity() {

    private val photoViewModel: PhotoViewModel by viewModels()
    private val diaryEntryViewModel: DiaryEntryViewModel by viewModels()
    private lateinit var closeDiaryForm: ImageView
    private lateinit var saveButton: TextView
    private lateinit var dateTextView: TextView
    private lateinit var dateLayout: View
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
    private var currentDate: Calendar = Calendar.getInstance()
    private var selectedDate = currentDate.time
    private var diaryEntryID: String = UUID.randomUUID().toString()
    private var diaryEntry: DiaryEntry? = null
    private val imageFilesList: MutableList<Photo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_layout)
        initializeResources()
        loadData()
//        handleLoadImageFiles()
    }

    private fun initializeResources() {
        closeDiaryForm = findViewById(R.id.close_diary_form)
        saveButton = findViewById(R.id.save_fab)
        dateTextView = findViewById(R.id.date_text_view)
        dateLayout = findViewById(R.id.date_layout)
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
            SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(selectedDate)

        closeDiaryForm.setOnClickListener {
            onBackPressed()
        }
        saveButton.setOnClickListener {
            handleSaveDiaryEntry()
        }

        addImageView.setOnClickListener { launchImageCapture() }

        dateLayout.setOnClickListener {
            DatePickerDialog(
                this@DiaryActivity,
                { _, year, month, dayOfMonth ->
                    currentDate = Calendar.getInstance()
                    currentDate.set(Calendar.MONTH, month)
                    currentDate.set(Calendar.YEAR, year)
                    currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    selectedDate = currentDate.time
                    dateTextView.text = SimpleDateFormat(
                        "MMMM dd, yyyy",
                        Locale.getDefault()
                    ).format(selectedDate)
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH) + 1
            )
                .show()
        }
    }

    private fun loadData() {
        if (intent.getBooleanExtra(IS_VIEW_DETAILED, false)) {
            diaryEntryID = intent.getStringExtra(ENTRY_ID_TAG_KEY).toString()

            lifecycleScope.launch {
                diaryEntryViewModel.getEntryById(diaryEntryID).collect { fetchedDiaryEntry ->
                    diaryEntry = fetchedDiaryEntry
                    titleEditText.setText(fetchedDiaryEntry.title)
                    entryBodyEditText.setText(fetchedDiaryEntry.notes)
                    dateTextView.text = fetchedDiaryEntry.date
                }
            }
        }
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
                            Photo(
                                id = UUID.randomUUID().toString(),
                                diaryEntryId = diaryEntryID,
                                it
                            )
                        }

                    // Store the image
                    diaryEntryImage?.let { photoViewModel.addEntry(diaryEntryImage) }

                    capturedImagesContainerView.visibility = View.VISIBLE
                    diaryEntryImage?.let { imageFilesList.add(it) }
                    imagesListAdapter.notifyDataSetChanged()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun handleSaveDiaryEntry() {
        if (titleEditText.text.toString().isEmpty()) {
            Toast.makeText(applicationContext, "Title must not be empty!", Toast.LENGTH_LONG).show()
        } else if (entryBodyEditText.text.toString().isEmpty()) {
            Toast.makeText(applicationContext, "Enter notes!", Toast.LENGTH_LONG).show()
        } else {
            diaryEntry = DiaryEntry(
                diaryEntryID,
                titleEditText.text.toString(),
                dateTextView.text.toString(),
                "",
                entryBodyEditText.text.toString()
            )
            diaryEntry?.let { entry ->
                diaryEntryViewModel.addEntry(entry)
            }
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, MainDashboardActivity::class.java))
        finish()
    }

    companion object {
        private const val GET_CONTENT_FROM_FILE_REQUEST_CODE = 1203
        const val ENTRY_ID_TAG_KEY = "entry_id_key"
        const val IS_VIEW_DETAILED = "view_in_detailed"
    }
}