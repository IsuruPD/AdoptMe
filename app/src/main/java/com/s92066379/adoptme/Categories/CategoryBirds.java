package com.s92066379.adoptme.Categories;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.s92066379.adoptme.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoryBirds extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_birds);
    }
}