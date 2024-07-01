package com.s92066379.adoptme.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.s92066379.adoptme.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ViewDetails extends AppCompatActivity {

    TextView nametxt, categorytxt, breedtxt, agetxt, vacstatustxt;
    String nameStr, categoryStr, breedStr, ageStr, vacstatusStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        nametxt=findViewById(R.id.nameid);
        categorytxt=findViewById(R.id.categoryid);
        breedtxt=findViewById(R.id.breedid);
        agetxt=findViewById(R.id.ageid);
        vacstatustxt=findViewById(R.id.vacstatusid);

        getAndSetIntentData();
    }

    public void getAndSetIntentData(){
        if(getIntent().hasExtra("name") && getIntent().hasExtra("category") &&
                getIntent().hasExtra("breed") && getIntent().hasExtra("age")){

            nameStr=getIntent().getStringExtra("name");
            categoryStr=getIntent().getStringExtra("category");
            breedStr=getIntent().getStringExtra("breed");
            ageStr=getIntent().getStringExtra("age");
            vacstatusStr=getIntent().getStringExtra("vacstatus");

            nametxt.setText(nameStr);
            categorytxt.setText(categoryStr);
            breedtxt.setText(breedStr);
            agetxt.setText(ageStr);
            vacstatustxt.setText(vacstatusStr);
        }else{
            Toast.makeText(this,"No available records!",Toast.LENGTH_SHORT).show();
        }
    }
}