package com.grayseal.traveldiaryapp.ui.main.view

import LocationAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
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
    private lateinit var locationDialog: AlertDialog
    private lateinit var locationTextView: TextView
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
    private val locationList: MutableList<String> = ArrayList()
    private var locationName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(applicationContext, "")
        setContentView(R.layout.activity_diary_layout)
        initializeResources()
        loadData()
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
        locationTextView = findViewById(R.id.location)
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

        // Location Dialog
        val dialogView = layoutInflater.inflate(R.layout.add_location_dialog, null)
        val locationNameEditText =
            dialogView.findViewById<EditText>(R.id.search_edit_text)
        val locationRecyclerView =
            dialogView.findViewById<RecyclerView>(R.id.location_recycler_view)
        val loadingProgressBar = dialogView.findViewById<ProgressBar>(R.id.loading_progress_bar)
        val closePopUpView = dialogView.findViewById<ImageView>(R.id.close_pop_up_view)

        closePopUpView.setOnClickListener {
            locationDialog.dismiss()
        }

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        locationDialog = builder.create()
        locationDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        addLocationView.setOnClickListener {
            locationDialog.show()

            val locationAdapter =
                LocationAdapter(locationList, object : LocationAdapter.OnLocationClickedListener {
                    override fun onLocationClicked(position: Int) {
                        locationTextView.text = locationList[position]
                        locationName = locationList[position]
                        locationDialog.dismiss()
                    }
                })
            locationRecyclerView.layoutManager = LinearLayoutManager(this)
            locationRecyclerView.adapter = locationAdapter

            val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
            locationRecyclerView.addItemDecoration(dividerItemDecoration)

            // Initialize the Places SDK client
            val placesClient: PlacesClient = Places.createClient(this)

            locationNameEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString()
                    loadingProgressBar.visibility = View.VISIBLE

                    // Make a FindAutocompletePredictionsRequest to Google Places API
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(query)
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .build()

                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            val predictionList: MutableList<String> = ArrayList()
                            for (prediction in response.autocompletePredictions) {
                                predictionList.add(prediction.getFullText(null).toString())
                            }

                            locationList.clear()
                            locationList.addAll(predictionList)
                            loadingProgressBar.visibility = View.GONE
                            locationAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { exception ->
                            // Handle failure, if any
                            Toast.makeText(
                                applicationContext,
                                "$exception",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
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
                    locationTextView.text = fetchedDiaryEntry.location
                }
            }

            lifecycleScope.launch {
                photoViewModel.getAllEntries().collect { photos ->
                    photos.filter { it.diaryEntryId == diaryEntryID }
                    imageFilesList.clear()
                    capturedImagesContainerView.visibility = View.VISIBLE
                    imageFilesList.addAll(photos)
                    imagesListAdapter.notifyDataSetChanged()
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
                    imageFile =
                        data.data?.let { ProcessAndroidUri.from(applicationContext, it) }
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
            Toast.makeText(applicationContext, "Title must not be empty!", Toast.LENGTH_LONG)
                .show()
        } else if (entryBodyEditText.text.toString().isEmpty()) {
            Toast.makeText(applicationContext, "Enter notes!", Toast.LENGTH_LONG).show()
        } else {
            diaryEntry = DiaryEntry(
                diaryEntryID,
                titleEditText.text.toString(),
                dateTextView.text.toString(),
                locationName,
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