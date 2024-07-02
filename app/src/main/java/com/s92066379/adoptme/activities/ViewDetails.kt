package com.s92066379.adoptme.activities

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.s92066379.adoptme.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewDetails : AppCompatActivity() {

    private lateinit var nametxt: TextView
    private lateinit var breedtxt: TextView
    private lateinit var datetxt: TextView
    private lateinit var agetxt: TextView
    private lateinit var vacstatustxt: TextView
    private lateinit var desctxt: TextView
    private lateinit var nameStr: String
    private lateinit var categoryStr: String
    private lateinit var breedStr: String
    private lateinit var ageStr: String
    private lateinit var vacstatusStr: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_details)

        nametxt = findViewById(R.id.name)
        breedtxt = findViewById(R.id.breed)
        datetxt = findViewById(R.id.timestamp)
        agetxt = findViewById(R.id.age)
        vacstatustxt = findViewById(R.id.vaccination)
        desctxt = findViewById(R.id.description)


        getAndSetIntentData()
    }

    private fun getAndSetIntentData() {
        if (intent.hasExtra("name") && intent.hasExtra("category") &&
            intent.hasExtra("breed") && intent.hasExtra("age") && intent.hasExtra("vacstatus")
        ) {
            nameStr = intent.getStringExtra("name") ?: ""
            categoryStr = intent.getStringExtra("category") ?: ""
            breedStr = intent.getStringExtra("breed") ?: ""
            ageStr = intent.getStringExtra("age") ?: ""
            vacstatusStr = intent.getStringExtra("vacstatus") ?: ""

            nametxt.text = nameStr
            breedtxt.text = breedStr
            agetxt.text = ageStr
            vacstatustxt.text = vacstatusStr
        } else {
            Toast.makeText(this, "No available records!", Toast.LENGTH_SHORT).show()
        }
    }
}
