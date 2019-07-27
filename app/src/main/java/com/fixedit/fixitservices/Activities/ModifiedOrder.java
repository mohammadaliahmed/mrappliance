package com.fixedit.fixitservices.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fixedit.fixitservices.Adapters.ServicesBookedAdapter;
import com.fixedit.fixitservices.Models.OrderModel;
import com.fixedit.fixitservices.Models.ServicemanModel;
import com.fixedit.fixitservices.R;
import com.fixedit.fixitservices.Services.ChooseServiceOptions;
import com.fixedit.fixitservices.Services.ListOfSubServices;
import com.fixedit.fixitservices.Utils.NotificationAsync;
import com.fixedit.fixitservices.Utils.NotificationObserver;
import com.fixedit.fixitservices.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModifiedOrder extends AppCompatActivity implements NotificationObserver {

    RecyclerView recyclerview;

    ServicesBookedAdapter adapter;
    TextView date, time, buildingType;
    RelativeLayout next;
    TextView serviceType;
    ImageView back;
    String orderId;
    DatabaseReference mDatabase;
    private OrderModel orderModel;
    Button start;
    RelativeLayout accept;
    private ServicemanModel serviceman;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modified_order);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        orderId = getIntent().getStringExtra("orderId");
        accept = findViewById(R.id.accept);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        adapter = new ServicesBookedAdapter(this, new ArrayList<ServiceCountModel>());
//        recyclerview.setAdapter(adapter);

        back = findViewById(R.id.back);
//        next = findViewById(R.id.next);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        buildingType = findViewById(R.id.buildingType);
        serviceType = findViewById(R.id.serviceType);
        start = findViewById(R.id.start);
        getOrderFromDB();


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();


            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Confirm modified oder?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("countModelArrayList", orderModel.getNewCountModelList());
                hashMap.put("modifiedOrderConfirmed", true);
                mDatabase.child("Orders").child(orderId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDatabase.child("Orders").child(orderId).child("newCountModelList").removeValue();
                        NotificationAsync notificationAsync = new NotificationAsync(ModifiedOrder.this);
                        String notification_title = "Order change request accepted";
                        String notification_message = "Click to view";
                        notificationAsync.execute("ali", SharedPrefs.getAdminFcmKey(), notification_title, notification_message, "Modify", "" + orderId);
                        if (orderModel.isAssigned()) {
                            NotificationAsync notificationAsync1 = new NotificationAsync(ModifiedOrder.this);
                            String notification_title1 = "Order change request accepted";
                            String notification_message1 = "Click to view";

                            notificationAsync1.execute("ali", serviceman.getFcmKey(), notification_title1, notification_message1, "Modify", "" + orderId);
                        }
                        startActivity(new Intent(ModifiedOrder.this,MyOrders.class));
                        finish();
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getOrderFromDB() {
        mDatabase.child("Orders").child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    orderModel = dataSnapshot.getValue(OrderModel.class);
                    if (orderModel != null) {
                        serviceType.setText(orderModel.getServiceName());
                        date.setText(orderModel.getDate());
                        time.setText(orderModel.getChosenTime());
                        buildingType.setText(orderModel.getBuildingType());
                        adapter = new ServicesBookedAdapter(ModifiedOrder.this, orderModel.getNewCountModelList());
                        recyclerview.setAdapter(adapter);
                        getServicemenKey(orderModel.getAssignedTo());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getServicemenKey(String assignedTo) {
        mDatabase.child("Servicemen").child(assignedTo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    serviceman = dataSnapshot.getValue(ServicemanModel.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onSuccess(String chatId) {

    }

    @Override
    public void onFailure() {

    }
}
