package com.s92066379.adoptme.Categories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.s92066379.adoptme.R;
import com.s92066379.adoptme.activities.ViewDetails;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter <CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList name, category, breed, age, vacstatus, city, longitudes, latitudes, description, contact;
    int position;

    CustomAdapter(Context context, ArrayList name, ArrayList category, ArrayList breed, ArrayList age, ArrayList vacstatus,
                  ArrayList city, ArrayList longitudes, ArrayList latitudes, ArrayList description, ArrayList contact){
        this.context=context;
        this.name=name;
        this.category=category;
        this.breed=breed;
        this.age=age;
        this.vacstatus=vacstatus;
        this.city=city;
        this.longitudes=longitudes;
        this.latitudes=latitudes;
        this.description=description;
        this.contact=contact;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.listing_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.nameTxt.setText(String.valueOf(name.get(position)));
        holder.categoryTxt.setText(String.valueOf(category.get(position)));
        holder.breedTxt.setText(String.valueOf(breed.get(position)));
        holder.ageTxt.setText(String.valueOf(age.get(position)));

        holder.mainRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ViewDetails.class);
                intent.putExtra("name",String.valueOf(name.get(position)));
                intent.putExtra("category",String.valueOf(category.get(position)));
                intent.putExtra("breed",String.valueOf(breed.get(position)));
                intent.putExtra("age",String.valueOf(age.get(position)));
                ////////
                intent.putExtra("vacstatus", String.valueOf(vacstatus.get(position)));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("Database", "Count of records in adapter: " + name.size());
        return name.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameTxt, categoryTxt, breedTxt, ageTxt;
        LinearLayout mainRowLayout;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            nameTxt=itemView.findViewById(R.id.petName);
            categoryTxt=itemView.findViewById(R.id.listingNumber);
            breedTxt=itemView.findViewById(R.id.subTitle);
            ageTxt=itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(this);

            mainRowLayout=itemView.findViewById(R.id.mainRowLayout);
        }

        @Override
        public void onClick(View view) {
//            int position = getAbsoluteAdapterPosition();
//            if(position !=RecyclerView.NO_POSITION){
//
//
//            }
        }
    }
}
