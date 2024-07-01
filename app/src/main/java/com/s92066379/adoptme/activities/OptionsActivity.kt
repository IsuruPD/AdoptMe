package com.s92066379.adoptme.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.s92066379.adoptme.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OptionsActivity : AppCompatActivity() {
    private lateinit var findV: ImageView
    private lateinit var postV: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_board)

        findV = findViewById(R.id.image_view)
        postV = findViewById(R.id.imageView4)

        findV.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
        }

        postV.setOnClickListener {
            startActivity(Intent(this, CreateListing::class.java))
        }
    }
}