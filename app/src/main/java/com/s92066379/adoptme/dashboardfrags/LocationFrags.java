package com.s92066379.adoptme.dashboardfrags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.s92066379.adoptme.R;
import com.s92066379.adoptme.databinding.FragmentLocationFragsBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LocationFrags extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FragmentLocationFragsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment using data binding
        binding = FragmentLocationFragsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng Colombo = new LatLng(6.9271, 79.8612);
        mMap.addMarker(new MarkerOptions().position(Colombo).title("Colombo"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Colombo));
    }

}