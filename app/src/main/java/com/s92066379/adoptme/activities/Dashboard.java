package com.s92066379.adoptme.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.s92066379.adoptme.R;
import com.s92066379.adoptme.adapters.ViewPagerAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Dashboard extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

    tabLayout=findViewById(R.id.tab_layout);
    viewPager2=findViewById(R.id.viewPager);
    viewPagerAdapter=new ViewPagerAdapter(this);
    viewPager2.setAdapter(viewPagerAdapter);

    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager2.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    });
    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            tabLayout.getTabAt(position).select();
        }
    });
    }
}