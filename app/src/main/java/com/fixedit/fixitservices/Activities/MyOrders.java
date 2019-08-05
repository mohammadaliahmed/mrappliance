package com.fixedit.fixitservices.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fixedit.fixitservices.Adapters.OrdersAdapter;
import com.fixedit.fixitservices.Adapters.ServicesBookedAdapter;
import com.fixedit.fixitservices.Models.OrderModel;
import com.fixedit.fixitservices.Models.RatingModel;
import com.fixedit.fixitservices.R;
import com.fixedit.fixitservices.Services.ChooseServiceOptions;
import com.fixedit.fixitservices.Services.ListOfSubServices;
import com.fixedit.fixitservices.Utils.CommonUtils;
import com.fixedit.fixitservices.Utils.SharedPrefs;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyOrders extends AppCompatActivity {

    RecyclerView recyclerview;
    OrdersAdapter adapter;
    ArrayList<OrderModel> orderModelArrayList = new ArrayList<>();
    ImageView back;
    DatabaseReference mDatabase;
    String orderId;
    int rating;

    String newOrderId;

    private float rated;
    private OrderModel orderModel;
    boolean dialogShown = false;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        back = findViewById(R.id.back);

        orderId = getIntent().getStringExtra("orderId");
        rating = getIntent().getIntExtra("rating", 0);

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
        adapter = new OrdersAdapter(this, orderModelArrayList, new OrdersAdapter.AdapterCallbacks() {
            @Override
            public void onRating(OrderModel model) {
                orderId = "" + model.getOrderId();
                rating = 1;
                getOrderDetailsFromDB();
            }
        });
        recyclerview.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

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

        if (orderId != null && rating == 1) {
            getOrderDetailsFromDB();

        }

    }

    private void getOrderDetailsFromDB() {
        mDatabase.child("Orders").child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    orderModel = dataSnapshot.getValue(OrderModel.class);
                    if (orderModel != null) {
                        CommonUtils.showToast("Please rate the service");
                        if (!dialogShown) {
                            showRatingDialog(orderModel.getServiceName());
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog(String serviceName) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.rating_dialog);

        final TextView title = dialog.findViewById(R.id.title);

        title.setText("Rate your experience with " + serviceName);
        final EditText comments = dialog.findViewById(R.id.comments);
        Button cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button submit = dialog.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CommonUtils.showToast("submit");
                if (rated == 0) {
                    CommonUtils.showToast("Please rate the service");
                } else {
                    dialogShown = true;
                    sendDataToDb(comments.getText().toString());
                    dialog.dismiss();
                }
            }
        });

        AppCompatRatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
//                    CommonUtils.showToast("" + rating);
                    rated = rating;
                }
            }
        });


        dialog.show();

    }

    private void sendDataToDb(String s) {
        RatingModel model = new RatingModel(
                orderId,
                s,
                SharedPrefs.getUser().getUsername(),
                SharedPrefs.getUser().getFullName(),
                orderModel.getAssignedTo(),
                orderId,
                orderModel.getServiceName(),
                System.currentTimeMillis(),
                rated


        );
        newOrderId = orderId;
        orderId = null;
        mDatabase.child("Ratings").child(newOrderId).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                rating = 0;
                orderId = null;
                CommonUtils.showToast("Thanks for rating");
                HashMap<String, Object> map = new HashMap<>();
                map.put("rated", true);
                map.put("rating", rated);
                mDatabase.child("Orders").child(newOrderId).updateChildren(map);
                rating = 0;
                orderId = null;
                finish();
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
