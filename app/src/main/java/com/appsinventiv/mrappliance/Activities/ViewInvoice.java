package com.appsinventiv.mrappliance.Activities;

import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.mrappliance.Adapters.InvoiceListAdapter;
import com.appsinventiv.mrappliance.Models.InvoiceModel;
import com.appsinventiv.mrappliance.R;
import com.appsinventiv.mrappliance.Utils.CommonUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewInvoice extends AppCompatActivity {
    TextView billNumber, orderNumber, date, dayChosen, timeChosen,
            customerName, mobileNumber, address, comments, totalTime, total;
    RecyclerView recycler;
    DatabaseReference mDatabase;
    String invoiceId;
    InvoiceModel model;

    InvoiceListAdapter adapter;
    RelativeLayout wholeLayout;

    TextView serviceName, buildingType, materialBill;
    TextView couponCode, discount;
    LinearLayout couponArea;

    TextView materialPercent, tax, perHourCost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invoice);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        invoiceId = getIntent().getStringExtra("invoiceId");
        this.setTitle("Bill Number: " + invoiceId);
        materialPercent = findViewById(R.id.materialPercent);
        tax = findViewById(R.id.tax);

        materialBill = findViewById(R.id.materialBill);
        couponCode = findViewById(R.id.couponCode);
        discount = findViewById(R.id.discount);
        couponArea = findViewById(R.id.couponArea);
        buildingType = findViewById(R.id.buildingType);
        serviceName = findViewById(R.id.serviceName);


        billNumber = findViewById(R.id.billNumber);
        perHourCost = findViewById(R.id.perHourCost);
        orderNumber = findViewById(R.id.orderNumber);
        date = findViewById(R.id.date);
        dayChosen = findViewById(R.id.dayChosen);
        timeChosen = findViewById(R.id.timeChosen);
        customerName = findViewById(R.id.customerName);
        mobileNumber = findViewById(R.id.mobileNumber);
        address = findViewById(R.id.address);
        comments = findViewById(R.id.comments);
        totalTime = findViewById(R.id.totalTime);
        total = findViewById(R.id.total);
        wholeLayout = findViewById(R.id.wholeLayout);
        recycler = findViewById(R.id.recycler);

        getDataFromServer();
    }

    private void getDataFromServer() {
        mDatabase.child("Invoices").child(invoiceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    model = dataSnapshot.getValue(InvoiceModel.class);
                    if (model != null) {
//                        billNumber.setText("Bill Number: " + model.getInvoiceId());
                        orderNumber.setText("Order Number: " + model.getOrder().getOrderId());
                        date.setText("Date: " + CommonUtils.getFormattedDate(model.getOrder().getTime()));
                        dayChosen.setText("Day: " + model.getOrder().getDate());
                        timeChosen.setText("Time: " + model.getOrder().getChosenTime());
                        customerName.setText("Customer Name: " + model.getOrder().getUser().getFullName());
                        mobileNumber.setText("Cell Number: " + model.getOrder().getUser().getMobile());
                        address.setText("Customer Address: " + model.getOrder().getUser().getAddress());
                        comments.setText("Comments: " + model.getOrder().getInstructions());
                        total.setText("Total Bill: AED " + model.getOrder().getTotalPrice());
                        materialBill.setText("Mat. Bill: AED " + model.getOrder().getMaterialBill());
                        totalTime.setText("Total Time: " + model.getOrder().getTotalHours() + " hours");

                        tax.setText(model.getOrder().getTax() + "% tax");
                        materialPercent.setText("10% mat. bill: AED " + (int) (model.getOrder().getMaterialBill() / 10));
                        serviceName.setText("Service Type: " + model.getOrder().getServiceName());
                        perHourCost.setText("Cost: " + model.getOrder().getTotalHours() + "*" + model.getOrder().getServiceCharges());

                        buildingType.setText("Building Type: " + model.getOrder().getBuildingType());
                        if (model.getOrder().isCouponApplied()) {
                            couponArea.setVisibility(View.VISIBLE);
                            couponCode.setText("Coupon code: " + model.getOrder().getCouponCode());
                            discount.setText("Discount: " + model.getOrder().getDiscount() + "%");
                        } else {
                            couponArea.setVisibility(View.GONE);
                        }


                        recycler.setLayoutManager(new LinearLayoutManager(ViewInvoice.this, LinearLayoutManager.VERTICAL, false));
                        adapter = new InvoiceListAdapter(ViewInvoice.this, model.getOrder().getCountModelArrayList());
                        recycler.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        wholeLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
