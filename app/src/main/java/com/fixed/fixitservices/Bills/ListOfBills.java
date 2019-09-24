package com.fixed.fixitservices.Bills;

import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.fixed.fixitservices.Models.InvoiceModel;
import com.fixed.fixitservices.Models.OrderModel;
import com.fixed.fixitservices.R;
import com.fixed.fixitservices.Utils.CommonUtils;
import com.fixed.fixitservices.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListOfBills extends AppCompatActivity {
    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    ArrayList<InvoiceModel> itemList = new ArrayList<>();
    BillsAdapter adapter;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_bills);

        this.setTitle("Transaction History");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerView);
        back = findViewById(R.id.back);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new BillsAdapter(this, itemList, new BillsAdapter.BillsCallback() {
            @Override
            public void onDelete(InvoiceModel model) {
            }
        });

        recyclerView.setAdapter(adapter);
        getDataFromDB();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void getBillsFromDB(String id) {
        mDatabase.child("Invoices").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    InvoiceModel model = dataSnapshot.getValue(InvoiceModel.class);
                    if (model != null) {
                        itemList.add(model);
                        Collections.sort(itemList, new Comparator<InvoiceModel>() {
                            @Override
                            public int compare(InvoiceModel listData, InvoiceModel t1) {
                                Long ob1 = listData.getTime();
                                Long ob2 = t1.getTime();

                                return ob2.compareTo(ob1);

                            }
                        });
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getDataFromDB() {
        mDatabase.child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        OrderModel model = snapshot.getValue(OrderModel.class);
                        if (model != null) {
                            if (model.getUser().getUsername().equalsIgnoreCase(SharedPrefs.getUser().getUsername())) {
                                if (model.getBillNumber() != 0) {
                                    getBillsFromDB(model.getBillNumber() + "");
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
