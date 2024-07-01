package com.s92066379.adoptme.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.s92066379.adoptme.dashboardfrags.CategoriesFrag;
import com.s92066379.adoptme.dashboardfrags.LocationFrags;


public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
        case 0:
            return new CategoriesFrag();
        case 1:
            return new LocationFrags();
        default:
            return new CategoriesFrag();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
