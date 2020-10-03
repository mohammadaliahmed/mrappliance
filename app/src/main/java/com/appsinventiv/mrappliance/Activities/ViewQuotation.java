package com.appsinventiv.mrappliance.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.appsinventiv.mrappliance.Models.LogsModel;
import com.appsinventiv.mrappliance.Models.OrderModel;
import com.appsinventiv.mrappliance.R;
import com.appsinventiv.mrappliance.Utils.CommonUtils;
import com.appsinventiv.mrappliance.Utils.NotificationAsync;
import com.appsinventiv.mrappliance.Utils.NotificationObserver;
import com.appsinventiv.mrappliance.Utils.SharedPrefs;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class ViewQuotation extends AppCompatActivity implements NotificationObserver {
    Button reject, accept;
    ImageView image;
    String orderId;
    DatabaseReference mDatabase;
    private String fcmKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quotation);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        image = findViewById(R.id.image);
        reject = findViewById(R.id.reject);
        accept = findViewById(R.id.accept);
        orderId = getIntent().getStringExtra("orderId");
        image.setOnTouchListener(new ImageMatrixTouchHandler(this));


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert("accepted");
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert("rejected");


            }
        });

        getOrderDataFromServer();
        getDataFromServer();

    }

    private void showAlert(final String accepted) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to " + (accepted.equalsIgnoreCase("accepted") ? "accept" : "reject") + " this quotation?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendNotification(accepted);
                String key = mDatabase.push().getKey();
                mDatabase.child("OrderLogs").child(SharedPrefs.getUser().getUsername()).child("" + orderId).child(key).setValue(new LogsModel(
                        key, "Quotation " + accepted, System.currentTimeMillis()
                ));

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getOrderDataFromServer() {
        mDatabase.child("Orders").child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    OrderModel model = dataSnapshot.getValue(OrderModel.class);
                    if (model != null) {
                        if (model.getAssignedTo() != null) {
                            getStaffDataFromServer(model.getAssignedTo());
                        } else {
                            CommonUtils.showToast("Order is still pending");
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getStaffDataFromServer(String assignedTo) {
        mDatabase.child("Servicemen").child(assignedTo).child("fcmKey").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    fcmKey = dataSnapshot.getValue(String.class);
                    if (fcmKey != null) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String type) {
        NotificationAsync notificationAsync = new NotificationAsync(this);
        String notification_title = "Customer " + type + " the quotation";
        String notification_message = "Click to view";
        notificationAsync.execute("ali", fcmKey, notification_title, notification_message, type + "Quotation", "" + orderId);
        finish();


    }


    private void getDataFromServer() {
        mDatabase.child("Quotations").child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String imgUrl = dataSnapshot.getValue(String.class);
                    if (imgUrl != null) {
                        Glide.with(ViewQuotation.this).load(imgUrl).into(image);
                    }
                } else {
                    CommonUtils.showToast("No Quotation available");
                    finish();
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
