package com.grayseal.traveldiaryapp.ui.main.view

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
import com.grayseal.traveldiaryapp.ui.main.adapter.LocationAdapter
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

            /**
             * TextWatcher implementation to observe changes in the location name input field.
             */
            locationNameEditText.addTextChangedListener(object : TextWatcher {
                /**
                 * This method is called after the text within the location name input field has changed.
                 * It triggers the Google Places API to retrieve autocomplete predictions based on the input.
                 *
                 * @param s The editable text after the change.
                 */
                override fun afterTextChanged(s: Editable?) {
                    // Get the input query from the editable text
                    val query = s.toString()

                    // Display the loading progress bar
                    loadingProgressBar.visibility = View.VISIBLE

                    // Build a FindAutocompletePredictionsRequest for the Google Places API
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(query)
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .build()

                    // Use the PlacesClient to fetch autocomplete predictions
                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            // Create a list to hold autocomplete prediction strings
                            val predictionList: MutableList<String> = ArrayList()

                            // Iterate through the response's autocomplete predictions
                            for (prediction in response.autocompletePredictions) {
                                // Add each prediction's full text to the list
                                predictionList.add(prediction.getFullText(null).toString())
                            }

                            // Clear and update the location list with the predictions
                            locationList.clear()
                            locationList.addAll(predictionList)

                            // Hide the loading progress bar and notify the location adapter of data change
                            loadingProgressBar.visibility = View.GONE
                            locationAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { exception ->
                            // Handle failure by displaying an error toast with the exception message
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
                    // No action needed before text changes
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // No action needed while text is changing
                }
            })

        }
    }

    /**
     * Loads data related to the diary entry when viewing it in detailed mode.
     */
    private fun loadData() {
        // Check if the activity was launched in detailed view mode
        if (intent.getBooleanExtra(IS_VIEW_DETAILED, false)) {
            // Retrieve the diary entry ID from the intent's extra
            diaryEntryID = intent.getStringExtra(ENTRY_ID_TAG_KEY).toString()

            // Use a lifecycleScope to launch coroutine for retrieving diary entry details
            lifecycleScope.launch {
                // Collect and observe changes in the diary entry with the specified ID
                diaryEntryViewModel.getEntryById(diaryEntryID).collect { fetchedDiaryEntry ->
                    // Update UI elements with the fetched diary entry details
                    diaryEntry = fetchedDiaryEntry
                    titleEditText.setText(fetchedDiaryEntry.title)
                    entryBodyEditText.setText(fetchedDiaryEntry.notes)
                    dateTextView.text = fetchedDiaryEntry.date
                    locationTextView.text = fetchedDiaryEntry.location
                    locationName = fetchedDiaryEntry.location
                }
            }

            // Use a lifecycleScope to launch coroutine for retrieving associated photos
            lifecycleScope.launch {
                // Collect and observe changes in all photo entries
                photoViewModel.getAllEntries().collect { photos ->
                    // Filter photos based on the current diary entry ID
                    val filteredPhotos = photos.filter { it.diaryEntryId == diaryEntryID }

                    // Clear the existing list and update UI to show captured images
                    imageFilesList.clear()
                    imageFilesList.addAll(filteredPhotos)
                    imagesListAdapter.notifyDataSetChanged()
                    capturedImagesContainerView.visibility = View.VISIBLE
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

    /**
     * Callback method triggered when an activity launched for result returns with a result.
     *
     * @param requestCode The integer request code originally supplied when launching the activity.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can carry data returned by the child activity.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Hide the progress bar indicating image capture process is done
        progressBar.visibility = View.GONE

        // Call the superclass implementation of the method
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the returned result is from the image capture request
        if (requestCode == GET_CONTENT_FROM_FILE_REQUEST_CODE) {
            if (data == null) {
                // If no data was returned, display a message indicating image capture failure
                Toast.makeText(applicationContext, "Image capture failed!", Toast.LENGTH_LONG)
                    .show()
            } else {
                try {
                    // Retrieve the image file URI from the returned intent
                    imageFile = data.data?.let { ProcessAndroidUri.from(applicationContext, it) }

                    // Check if the image file exists and has a valid length
                    if (imageFile?.length()!! < 1) return

                    // Create a Photo object to store the captured image information
                    val diaryEntryImage = imageFile?.absolutePath?.let {
                        Photo(
                            id = UUID.randomUUID().toString(),
                            diaryEntryId = diaryEntryID,
                            it
                        )
                    }

                    // Store the image using the PhotoViewModel
                    diaryEntryImage?.let { photoViewModel.addEntry(diaryEntryImage) }

                    // Show the captured images container and update the list
                    capturedImagesContainerView.visibility = View.VISIBLE
                    diaryEntryImage?.let { imageFilesList.add(it) }
                    imagesListAdapter.notifyDataSetChanged()
                } catch (e: IOException) {
                    // Handle any exceptions that occur during image processing
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