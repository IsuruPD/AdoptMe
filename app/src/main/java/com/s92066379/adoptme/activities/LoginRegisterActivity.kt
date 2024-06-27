package com.s92066379.adoptme.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.s92066379.adoptme.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
    }
}