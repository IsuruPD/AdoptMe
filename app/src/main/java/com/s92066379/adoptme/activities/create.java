package com.s92066379.adoptme.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.s92066379.adoptme.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;

public class create extends AppCompatActivity implements OnMapReadyCallback {
    //private MapView mapView;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    EditText name,breed, age, description, contact;
    Spinner drpValue;
    RadioGroup rdogrp;
    RadioButton rdobtn;
    Button btnList;
    //DBHelper projectDb;
    String[] categoryArr;

    // Declare the latitude and longitude variables
    private double latitude;
    private double longitude;
    private Marker previousMarker;


    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        googleMap=gMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable the Place Autocomplete search bar
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Remove the previous marker if it exists
                if (previousMarker != null) {
                    previousMarker.remove();
                }
                MarkerOptions marker = new MarkerOptions().position(latLng);
                previousMarker=googleMap.addMarker(marker);

                latitude = latLng.latitude;
                longitude = latLng.longitude;
                // Obtaining the latitude and longitude from the map
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);
        //projectDb=new DBHelper(this);
        // Map integration
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLocation);
        //mapFragment.getMapAsync(this);


        //Assigning variables to inputs
        btnList=findViewById(R.id.addListbtn);
        name=findViewById(R.id.edtTxtName);
        breed=findViewById(R.id.edTxtBreed);
        age=findViewById(R.id.edTxtAge);
        description=findViewById(R.id.edtTxtDescription);
        contact=findViewById(R.id.edTxtContact);
        drpValue= (Spinner) findViewById(R.id.drpdwnCategory);
        rdogrp=findViewById(R.id.RGroup);

        //Map integration
        //mapView = findViewById(R.id.mapLocation);
        //mapView.onCreate(savedInstanceState);
        //mapView.getMapAsync( this);
        // Map integration
//        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLocation);
//        if (mapFragment == null) {
//            mapFragment = SupportMapFragment.newInstance();
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.mapLocation, mapFragment)
//                    .commit();
//        }
        mapFragment.getMapAsync(this);

        //Creating the drop down list
        //categoryArr =getResources().getStringArray(R.array.drpdwnArrCategory);
        Spinner s1 = (Spinner) findViewById(R.id.drpdwnCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,categoryArr);
        s1.setAdapter(adapter);
        /*s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                int index = arg0.getSelectedItemPosition();
                //Toast.makeText(getBaseContext(),"You have selected item : " + category[index],Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });*/

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameStr=name.getText().toString();
                String breedStr=breed.getText().toString();
                String ageStr=age.getText().toString();
                String desStr=description.getText().toString();
                String contactStr=contact.getText().toString();
                String categoryStr=drpValue.getSelectedItem().toString();

                // Access the latitude and longitude values
                double selectedLatitude = latitude;
                double selectedLongitude = longitude;
                String latitudeStr = String.valueOf(selectedLatitude);
                String longitudeStr = String.valueOf(selectedLongitude);

                int vacStatID=rdogrp.getCheckedRadioButtonId();
                rdobtn=findViewById(vacStatID);
                String vacstatus=rdobtn.getText().toString();


                if(nameStr.equals("")||breedStr.equals("")||desStr.equals("")||contactStr.equals("")){
                    Toast.makeText(create.this,"All fields required",Toast.LENGTH_SHORT).show();
                }else{
                    /*Boolean isListed= projectDb.insertListingDetails(nameStr,categoryStr,breedStr,ageStr,vacstatus,desStr,contactStr,latitudeStr,longitudeStr);
                    if(isListed==true){
                        new AlertDialog.Builder(create.this)
                                .setTitle("Success!")
                                .setMessage("Add another listing?")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Intent intent=new Intent(getApplicationContext(),create.class);
                                        startActivity(intent);
                                    }})
                                .setNegativeButton(android.R.string.cancel, null).show();}*/

                }
            }
        });
    }

}