package com.fixedit.fixitservices.Services;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fixedit.fixitservices.AddToCartCallbacks;
import com.fixedit.fixitservices.Models.ServiceCountModel;
import com.fixedit.fixitservices.R;
import com.fixedit.fixitservices.Utils.CommonUtils;
import com.fixedit.fixitservices.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListOfSubServices extends AppCompatActivity {
    RecyclerView recyclerview;
    SubServiceListAdapter adapter;
    private ArrayList<SubServiceModel> itemList = new ArrayList<>();
    private ArrayList<ServiceCountModel> userCartServiceList = new ArrayList<>();

    public static ArrayList<ServiceCountModel> orderList = new ArrayList<>();
    DatabaseReference mDatabase;
    public static String parentService;
    TextView serviceName;
    ImageView back;

    RelativeLayout next;
    private boolean canGoNext = true;
    public static ServiceModel parentServiceModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sub_services);
        recyclerview = findViewById(R.id.recyclerview);
        serviceName = findViewById(R.id.serviceName);
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        orderList = new ArrayList<>();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        parentService = getIntent().getStringExtra("parentService");
        serviceName.setText(parentService);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canGoNext) {
                    startActivity(new Intent(ListOfSubServices.this, ChooseServiceOptions.class));
                } else {
                    CommonUtils.showToast("Please select one service");
                }
            }
        });


        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new SubServiceListAdapter(this, itemList, userCartServiceList, new AddToCartCallbacks() {
            @Override
            public void addedToCart(final SubServiceModel services, int quantity) {
                mDatabase.child("Users").child(SharedPrefs.getUser().getUsername())
                        .child("Cart").child(services.getName())
                        .setValue(new ServiceCountModel(services, quantity, System.currentTimeMillis()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void deletedFromCart(final SubServiceModel services) {
                mDatabase.child("Users").child(SharedPrefs.getUser().getUsername())
                        .child("Cart").child(services.getName()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        getUserCartProductsFromDB();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }

            @Override
            public void quantityUpdate(SubServiceModel services, int quantity) {
                mDatabase.child("Users").child(SharedPrefs.getUser().getUsername())
                        .child("Cart").child(services.getName()).child("quantity").setValue(quantity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });

        adapter.setUpdateActivityUI(new SubServiceListAdapter.UpdateActivityUI() {
            @Override
            public void updateNow(ArrayList<ServiceCountModel> list) {

                if (list.size() > 0) {
                    canGoNext = true;
                    next.setBackground(getResources().getDrawable(R.drawable.button_green_bg));
                } else {
                    canGoNext = false;
                    next.setBackground(getResources().getDrawable(R.drawable.button_grey_bg));

                }
            }
        });
        recyclerview.setAdapter(adapter);

        getDataFromDB();
        getUserCartProductsFromDB();
        getParentServiceFromDB();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void getParentServiceFromDB() {
        mDatabase.child("Services").child(parentService).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    parentServiceModel = dataSnapshot.getValue(ServiceModel.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserCartProductsFromDB() {
        if(SharedPrefs.getUser()!=null) {
            mDatabase.child("Users").child(SharedPrefs.getUser().
                    getUsername()).child("Cart").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        userCartServiceList.clear();
                        ListOfSubServices.orderList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ServiceCountModel product = snapshot.getValue(ServiceCountModel.class);
                            if (product != null) {
                                if (!userCartServiceList.contains(product)) {
                                    userCartServiceList.add(product);

                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        ListOfSubServices.orderList.clear();
                        userCartServiceList.clear();
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void getDataFromDB() {
        mDatabase.child("SubServices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SubServiceModel model = snapshot.getValue(SubServiceModel.class);
                        if (model != null) {
                            if (model.getParentService().contains(parentService)) {
                                itemList.add(model);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    itemList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
