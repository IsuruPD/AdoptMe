package com.s92066379.adoptme.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.s92066379.adoptme.R
import com.s92066379.adoptme.databinding.ActivityCreateListingBinding
import com.s92066379.adoptme.firebase.FirebaseListingHelper
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateListing : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gmap: GoogleMap
    private lateinit var searchView: SearchView
    private var currentMarker: Marker? = null
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraActivityResultLauncher: ActivityResultLauncher<Uri>
    private var imageUri: Uri? = null
    private lateinit var imgPetPicture: ImageView
    private lateinit var currentPhotoPath: String
    private lateinit var spinnerCategory: Spinner
    private lateinit var edtName: EditText
    private lateinit var edtBreed: EditText
    private lateinit var edtAge: EditText
    private lateinit var radioGroupVaccination: RadioGroup
    private lateinit var edtDescription: EditText
    private lateinit var edtContact: EditText
    private var selectedLatitude: Double = 0.0
    private var selectedLongitude: Double = 0.0
    private lateinit var btnSubmit: Button

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_listing)

        // Initialize Firebase App Check with Debug Provider
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())

        btnSubmit = findViewById(R.id.addListbtn)
        edtName = findViewById(R.id.edtTxtName)
        edtBreed = findViewById(R.id.edTxtBreed)
        edtAge = findViewById(R.id.edTxtAge)
        radioGroupVaccination = findViewById(R.id.RGroup)
        edtDescription = findViewById(R.id.edtTxtDescription)
        edtContact = findViewById(R.id.edTxtContact)

        btnSubmit.setOnClickListener {
            submitListing()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.id_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchView = findViewById(R.id.search_view)
        setupSearchView()

        val btnBackCreate: ImageView = findViewById(R.id.btnBackCreate)
        btnBackCreate.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
        }

        imgPetPicture = findViewById(R.id.imgPetPicture)
        setupImagePickers()

        imgPetPicture.setOnClickListener {
            showImagePickerDialog()
        }

        // Initialize the Spinner
        spinnerCategory = findViewById(R.id.drpdwnCategory)
        setupSpinner()
    }

    private fun setupSpinner() {
        val categories = arrayOf("Dogs", "Cats", "Birds", "Fish", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun setupImagePickers() {
        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data
            if (data != null && data.data != null) {
                imageUri = data.data
                Glide.with(this).load(imageUri).into(imgPetPicture)
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        cameraActivityResultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Glide.with(this).load(currentPhotoPath).into(imgPetPicture)
            } else {
                Toast.makeText(this, "Failed to take picture", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // Check camera permissions
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        ) {
                            dispatchTakePictureIntent()
                        } else {
                            requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        }
                    } else {
                        dispatchTakePictureIntent()
                    }
                }
                1 -> {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    imageActivityResultLauncher.launch(intent)
                }
            }
        }
        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.s92066379.adoptme.fileprovider",
                it
            )
            imageUri = photoURI
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraActivityResultLauncher.launch(photoURI)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchLocation(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        searchView.setOnClickListener {
            searchView.isIconified = false
        }
    }

    private fun searchLocation(location: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(location, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)
                addMarker(latLng, location)
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMarker(latLng: LatLng, title: String) {
        currentMarker?.remove()
        currentMarker = gmap.addMarker(MarkerOptions().position(latLng).title(title))
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gmap = googleMap
        val location = LatLng(6.9271, 79.8612)
        addMarker(location, "Colombo")
        gmap.uiSettings.isZoomControlsEnabled = true

        gmap.setOnMapClickListener { latLng ->
            addMarker(latLng, "")
        }
    }

    private fun submitListing() {
        val category = spinnerCategory.selectedItem.toString()
        val name = edtName.text.toString()
        val breed = edtBreed.text.toString()
        val age = edtAge.text.toString().toIntOrNull() ?: 0
        val vaccinationStatus = when (radioGroupVaccination.checkedRadioButtonId) {
            R.id.VacYes -> "Yes"
            R.id.VacNo -> "No"
            R.id.VacUnknown -> "Unknown"
            else -> "Unknown"
        }
        val description = edtDescription.text.toString()
        val contact = edtContact.text.toString().toIntOrNull() ?: 0

        imageUri?.let { uri ->
            FirebaseListingHelper().uploadImageAndSaveListing(
                uri,
                name,
                category,
                breed,
                age,
                vaccinationStatus,
                description,
                contact,
                selectedLatitude,
                selectedLongitude
            ) { success, message ->
                if (success) {
                    Toast.makeText(this, "Listing saved successfully!", Toast.LENGTH_SHORT).show()
                    // Redirect or finish activity
                } else {
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
    }
}
