package com.s92066379.adoptme.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.s92066379.adoptme.databinding.ActivityUserProfileBinding
import com.s92066379.adoptme.util.Resource
import com.s92066379.adoptme.viewmodel.UserOptionsVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val viewModel by viewModels<UserOptionsVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtEditProfileUserOptions.setOnClickListener {
            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)
        }

        binding.layoutModule1.setOnClickListener {
            val intent = Intent(this, MyListings::class.java)
            startActivity(intent)
        }

        binding.Logout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.btnBackProfileOptions.setOnClickListener {
            onBackPressed()
        }

        fetchUserDetails()
    }

    override fun onResume() {
        super.onResume()
        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        val user = it.data
                        if (user != null) {
                            Glide.with(this@ProfileActivity)
                                .load(user.imagePath)
                                .error(ColorDrawable(Color.DKGRAY))
                                .into(binding.imgProfileUserOptions)
                            binding.txtNameUserOptions.text = "${user.firstname} ${user.lastname}"
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@ProfileActivity, it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.logout()
                val intent = Intent(this, LoginRegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
