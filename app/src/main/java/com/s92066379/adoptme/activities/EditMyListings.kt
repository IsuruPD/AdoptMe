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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.s92066379.adoptme.R
import com.s92066379.adoptme.data.Listing
import com.s92066379.adoptme.firebase.FirebaseListingHelper
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditMyListings : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gmap: GoogleMap
    private lateinit var searchView: SearchView
    private var currentMarker: Marker? = null
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraActivityResultLauncher: ActivityResultLauncher<Uri>
    private var imageUri: Uri? = null
    private lateinit var imgPetPicture: ImageView
    private lateinit var imgAddPicture: ImageView
    private lateinit var imgAddIconView: ImageView
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
    private lateinit var btnDelete: Button
    private lateinit var listingId: String

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
        setContentView(R.layout.activity_edit_my_listings)

        // Retrieve the document ID from the Intent
        listingId = intent.getStringExtra("Listing_ID") ?: ""

        // Fetch listing details
        if (listingId.isNotEmpty()) {
            fetchListingDetails(listingId)
        } else {
            Toast.makeText(this, "Invalid listing ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Initialize Firebase App Check with Debug Provider
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())

        btnSubmit = findViewById(R.id.updateListbtn)
        btnDelete = findViewById(R.id.deleteListbtn)
        edtName = findViewById(R.id.edtTxtName)
        edtBreed = findViewById(R.id.edTxtBreed)
        edtAge = findViewById(R.id.edTxtAge)
        radioGroupVaccination = findViewById(R.id.RGroup)
        edtDescription = findViewById(R.id.edtTxtDescription)
        edtContact = findViewById(R.id.edTxtContact)

        btnSubmit.setOnClickListener {
            updateListing()
        }

        btnDelete.setOnClickListener {
            confirmDeleteListing()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.id_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchView = findViewById(R.id.search_view)
        setupSearchView()

        val btnBackCreate: ImageView = findViewById(R.id.btnBackCreate)
        btnBackCreate.setOnClickListener {
            val intent = Intent(this, OptionsActivity::class.java)
            startActivity(intent)
        }

        imgPetPicture = findViewById(R.id.imgPetPicture)
        imgAddPicture = findViewById(R.id.imgAddPicture)
        imgAddIconView = findViewById(R.id.imgAddIconView)
        setupImagePickers()

        imgPetPicture.setOnClickListener {
            showImagePickerDialog()
        }
        imgAddPicture.setOnClickListener {
            showImagePickerDialog()
        }
        imgAddIconView.setOnClickListener {
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
                gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                addMarker(latLng, address.featureName)
            }
        }
    }

    private fun addMarker(latLng: LatLng, title: String?) {
        currentMarker?.remove()
        val markerOptions = MarkerOptions().position(latLng).title(title)
        currentMarker = gmap.addMarker(markerOptions)
        selectedLatitude = latLng.latitude
        selectedLongitude = latLng.longitude
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gmap = googleMap
        val defaultLocation = LatLng(0.0, 0.0)
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 1f))
        gmap.setOnMapClickListener { latLng ->
            addMarker(latLng, "Selected Location")
        }
    }

    private fun fetchListingDetails(listingId: String) {
        FirebaseFirestore.getInstance().collection("general_listings").document(listingId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val listing = document.toObject(Listing::class.java)
                    listing?.let {
                        populateFields(it)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching listing details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun populateFields(listing: Listing) {
        edtName.setText(listing.name)
        edtBreed.setText(listing.breed)
        edtAge.setText(listing.age.toString())
        edtDescription.setText(listing.description)
        edtContact.setText(listing.contact.toString())
        selectedLatitude = listing.latitude
        selectedLongitude = listing.longitude

        when (listing.vaccinationStatus) {
            "Yes" -> radioGroupVaccination.check(R.id.VacYes)
            "No" -> radioGroupVaccination.check(R.id.VacNo)
            "Unknown" -> radioGroupVaccination.check(R.id.VacUnknown)
        }

        Glide.with(this).load(listing.imageUrl).into(imgPetPicture)

        val latLng = LatLng(listing.latitude, listing.longitude)
        addMarker(latLng, listing.name)
    }

    private fun updateListing() {
        val name = edtName.text.toString()
        val category = spinnerCategory.selectedItem.toString()
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
            FirebaseListingHelper().uploadImageAndUpdateListing(
                listingId,
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
                    Toast.makeText(this, "Listing updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            FirebaseListingHelper().updateListingWithoutImage(
                listingId,
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
                    Toast.makeText(this, "Listing updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun confirmDeleteListing() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to delete this listing?")
            .setPositiveButton("Yes") { _, _ ->
                deleteListing()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteListing() {
        FirebaseListingHelper().deleteListing(listingId) { success, message ->
            if (success) {
                Toast.makeText(this, "Listing deleted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
