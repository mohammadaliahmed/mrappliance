package com.appsinventiv.mrappliance.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.mrappliance.Adapters.LogsAdapter;
import com.appsinventiv.mrappliance.Models.InvoiceModel;
import com.appsinventiv.mrappliance.Models.LogsModel;
import com.appsinventiv.mrappliance.R;
import com.appsinventiv.mrappliance.Services.ChooseServiceOptions;
import com.appsinventiv.mrappliance.Services.ListOfSubServices;
import com.appsinventiv.mrappliance.UserManagement.LoginMenu;
import com.appsinventiv.mrappliance.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewLogs extends AppCompatActivity {

    ImageView back;
    TextView orderNumber;
    RecyclerView recyclerview;
    String orderId;
    DatabaseReference mDatabase;
    ArrayList<LogsModel> itemList = new ArrayList<>();
    LogsAdapter adapter;

    TextView noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        orderId = getIntent().getStringExtra("orderId");
        recyclerview = findViewById(R.id.recyclerview);
        back = findViewById(R.id.back);
        noData = findViewById(R.id.noData);
        orderNumber = findViewById(R.id.orderNumber);
        orderNumber.setText("Logs for order: " + orderId);
        adapter = new LogsAdapter(this, itemList);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerview.setAdapter(adapter);

        getLogsFromServer();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getLogsFromServer() {
        mDatabase.child("OrderLogs").child(SharedPrefs.getUser().getUsername()).child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LogsModel model = snapshot.getValue(LogsModel.class);
                        if (model != null) {
                            itemList.add(model);
                        }
                    }
                    Collections.sort(itemList, new Comparator<LogsModel>() {
                        @Override
                        public int compare(LogsModel listData, LogsModel t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();

                            return ob1.compareTo(ob2);

                        }
                    });
                    adapter.notifyDataSetChanged();
                    if (itemList.size() > 0) {
                        noData.setVisibility(View.GONE);
                    }


                } else {
                    noData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
