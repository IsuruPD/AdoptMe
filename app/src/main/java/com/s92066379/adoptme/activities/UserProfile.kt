package com.s92066379.adoptme.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.s92066379.adoptme.R
import com.s92066379.adoptme.databinding.ActivityProfileManagementBinding
import com.s92066379.adoptme.util.Resource
import com.s92066379.adoptme.viewmodel.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class UserProfile: AppCompatActivity() {

    private lateinit var binding: ActivityProfileManagementBinding
    private val viewModel by viewModels<EditProfileViewModel>()
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraActivityResultLauncher: ActivityResultLauncher<Uri>
    private var originalImageUrl: String? = null
    private var imageUri: Uri? = null
    private lateinit var currentPhotoPath: String
    private lateinit var imgUserProfileView: ImageView
    private lateinit var imgEditImageProfileView: ImageView
    private lateinit var imgEditIconProfileView: ImageView

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
        binding = ActivityProfileManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imgUserProfileView = findViewById(R.id.imgUserProfileView)
        imgEditImageProfileView = findViewById(R.id.imgEditImageProfileView)
        imgEditIconProfileView = findViewById(R.id.imgEditIconProfileView)

        setupImagePickers()
        imgUserProfileView.setOnClickListener {
            showImagePickerDialog()
        }
        imgEditImageProfileView.setOnClickListener {
            showImagePickerDialog()
        }
        imgEditIconProfileView.setOnClickListener {
            showImagePickerDialog()
        }

        binding.btnBackProfileView.setOnClickListener {
            finish()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        val user = it.data
                        if (user != null) {
                            binding.txtEmailProfileView.text = user.email
                            binding.profileFirstNameEdt.setText(user.firstname)
                            binding.profileLastNameEdt.setText(user.lastname)
                            binding.profileContactEdt.setText(user.phoneNumber)
                            originalImageUrl = user.imagePath

                            Glide.with(this@UserProfile)
                                .load(user.imagePath)
                                .error(android.graphics.drawable.ColorDrawable(android.graphics.Color.DKGRAY))
                                .into(binding.imgUserProfileView)
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@UserProfile, it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.updateStatus.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        Toast.makeText(this@UserProfile, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        viewModel.resetUpdateStatus()
                        val intent = Intent(this@UserProfile, ProfileActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@UserProfile, it.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.btnUpdateProfile.setOnClickListener {
            val firstName = binding.profileFirstNameEdt.text.toString().trim()
            val lastName = binding.profileLastNameEdt.text.toString().trim()
            val contact = binding.profileContactEdt.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(this, "Name fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateUserDetails(firstName, lastName, contact, imageUri, originalImageUrl)
        }
    }

    private fun setupImagePickers() {
        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data
            if (data != null && data.data != null) {
                imageUri = data.data
                Glide.with(this).load(imageUri).into(imgUserProfileView)
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        cameraActivityResultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Glide.with(this).load(currentPhotoPath).into(imgUserProfileView)
            } else {
                Toast.makeText(this, "Failed to take picture", Toast.LENGTH_SHORT).show()
                Glide.with(this).load(android.graphics.drawable.ColorDrawable(android.graphics.Color.DKGRAY)).into(imgUserProfileView)
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
}