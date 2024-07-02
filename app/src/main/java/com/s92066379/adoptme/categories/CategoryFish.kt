package com.s92066379.adoptme.categories;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.s92066379.adoptme.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
class CategoryFish  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_fish)
    }
}