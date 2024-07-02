package com.s92066379.adoptme.dashboardfrags;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.s92066379.adoptme.categories.CategoryCats;
import com.s92066379.adoptme.categories.CategoryBirds;
import com.s92066379.adoptme.categories.CategoryFish;
import com.s92066379.adoptme.categories.CategoryOther;
import com.s92066379.adoptme.categories.CategoryPups;
import com.s92066379.adoptme.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoriesFrag extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ImageView imageViewDog = getView().findViewById(R.id.imageViewdog);
        imageViewDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentd = new Intent(getActivity(), CategoryPups.class);
                startActivity(intentd);
            }
        });
        ImageView imageViewCat = getView().findViewById(R.id.imageViewcat);
        imageViewCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentc = new Intent(getActivity(), CategoryCats.class);
                startActivity(intentc);
            }
        });
        ImageView imageViewBird = getView().findViewById(R.id.imageViewbird);
        imageViewBird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentb = new Intent(getActivity(), CategoryBirds.class);
                startActivity(intentb);
            }
        });
        ImageView imageViewFish = getView().findViewById(R.id.imageViewfish);
        imageViewFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentf = new Intent(getActivity(), CategoryFish.class);
                startActivity(intentf);
            }
        });
        ImageView imageViewOther = getView().findViewById(R.id.imageViewother);
        imageViewOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intento = new Intent(getActivity(), CategoryOther.class);
                startActivity(intento);
            }
        });
    }
}
