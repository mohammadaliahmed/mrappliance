package com.appsinventiv.mrappliance.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appsinventiv.mrappliance.R;
import com.appsinventiv.mrappliance.Services.ChooseServiceOptions;
import com.appsinventiv.mrappliance.Services.ListOfSubServices;

public class OrderPlaced extends AppCompatActivity {

    Button home;

    long orderId;
    long estimatedCost;
    long estimatedTime;
    TextView orderDetails, orderidtext;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ChooseServiceOptions.buildingType = null;
        ChooseServiceOptions.timeSelected = null;
        ListOfSubServices.orderList = null;
        ListOfSubServices.parentService = null;
        Intent i = new Intent(OrderPlaced.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        home = findViewById(R.id.home);
        orderDetails = findViewById(R.id.orderDetails);
        orderidtext = findViewById(R.id.orderidtext);

        orderId = getIntent().getLongExtra("orderId", 0);
        estimatedCost = getIntent().getLongExtra("estimatedCost", 0);
        estimatedTime = getIntent().getLongExtra("estimatedTime", 0);

        orderidtext.setText("Order Id: " + orderId);
        orderDetails.setText(
                "Call out charges: AED" + estimatedCost);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseServiceOptions.buildingType = null;
                ChooseServiceOptions.timeSelected = null;
                ListOfSubServices.orderList = null;
                ListOfSubServices.parentService = null;
                Intent i = new Intent(OrderPlaced.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });


    }
}
