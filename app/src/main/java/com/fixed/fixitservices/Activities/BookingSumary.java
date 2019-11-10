package com.fixed.fixitservices.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fixed.fixitservices.Adapters.ServicesBookedAdapter;
import com.fixed.fixitservices.R;
import com.fixed.fixitservices.Services.ChooseServiceOptions;
import com.fixed.fixitservices.Services.ListOfSubServices;
import com.fixed.fixitservices.Utils.CommonUtils;
import com.fixed.fixitservices.Utils.ConnectivityManager;

public class BookingSumary extends AppCompatActivity {

    RecyclerView recyclerview;

    ServicesBookedAdapter adapter;
    TextView date, time, buildingType;
    RelativeLayout next;
    TextView serviceType;
    ImageView back;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ServicesBookedAdapter(this, ListOfSubServices.orderList);
        recyclerview.setAdapter(adapter);

        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        buildingType = findViewById(R.id.buildingType);
        serviceType = findViewById(R.id.serviceType);
        serviceType.setText(ListOfSubServices.parentServiceModel.getName());

        date.setText(ChooseServiceOptions.daySelected.replace("\n", " "));
        time.setText(ChooseServiceOptions.timeSelected);
        buildingType.setText(ChooseServiceOptions.buildingType);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityManager.isNetworkConnected(BookingSumary.this)) {
                    startActivity(new Intent(BookingSumary.this, ChooseAddress.class));
                } else {
                    CommonUtils.showToast("Please check internet connection");
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
