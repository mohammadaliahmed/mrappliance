package com.appsinventiv.mrappliance.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.appsinventiv.mrappliance.Adapters.OrdersAdapter;
import com.appsinventiv.mrappliance.Adapters.TrackOrdersAdapter;
import com.appsinventiv.mrappliance.Models.OrderModel;
import com.appsinventiv.mrappliance.Models.RatingModel;
import com.appsinventiv.mrappliance.R;
import com.appsinventiv.mrappliance.Services.ChooseServiceOptions;
import com.appsinventiv.mrappliance.Services.ListOfSubServices;
import com.appsinventiv.mrappliance.UserManagement.LoginMenu;
import com.appsinventiv.mrappliance.Utils.CommonUtils;
import com.appsinventiv.mrappliance.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TrackOrder extends AppCompatActivity {
    RecyclerView recyclerview;
    TrackOrdersAdapter adapter;
    ArrayList<OrderModel> orderModelArrayList = new ArrayList<>();
    ImageView back;
    DatabaseReference mDatabase;
    int rating;

    String newOrderId;

    private float rated;
    private OrderModel orderModel;
    boolean dialogShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);


        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        back = findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerview = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(layoutManager);
        adapter = new TrackOrdersAdapter(this, orderModelArrayList, new TrackOrdersAdapter.TrackAdapterCallbacks() {

            @Override
            public void onViewQuote(OrderModel model) {
                Intent i = new Intent(TrackOrder.this, ViewQuotation.class);
                i.putExtra("orderId", "" + model.getOrderId());
                startActivity(i);

            }

            @Override
            public void onViewLogs(OrderModel model) {
                Intent i = new Intent(TrackOrder.this, ViewLogs.class);
                i.putExtra("orderId", "" + model.getOrderId());
                startActivity(i);
            }
        });
        recyclerview.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        getOrdersOfUser();


    }

    private void getOrdersOfUser() {
        mDatabase.child("Users").child(SharedPrefs.getUser().getUsername()).child("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    orderModelArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        getOrdersFromDb(snapshot.getKey());
                    }
//                    progress.setVisibility(View.GONE);
                } else {
//                    progress.setVisibility(View.GONE);

                    CommonUtils.showToast("No orders");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getOrdersFromDb(String key) {
        mDatabase.child("Orders").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    OrderModel model = dataSnapshot.getValue(OrderModel.class);
                    if (model != null) {

                        orderModelArrayList.add(model);
                        Collections.sort(orderModelArrayList, new Comparator<OrderModel>() {
                            @Override
                            public int compare(OrderModel listData, OrderModel t1) {
                                Long ob1 = listData.getTime();
                                Long ob2 = t1.getTime();

                                return ob2.compareTo(ob1);

                            }
                        });
                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
