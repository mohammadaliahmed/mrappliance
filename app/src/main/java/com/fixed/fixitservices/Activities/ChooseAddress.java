package com.fixed.fixitservices.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fixed.fixitservices.Models.AdminModel;
import com.fixed.fixitservices.Models.CouponModel;
import com.fixed.fixitservices.Models.OrderModel;
import com.fixed.fixitservices.Models.ServiceCountModel;
import com.fixed.fixitservices.Models.User;
import com.fixed.fixitservices.R;
import com.fixed.fixitservices.Services.ChooseServiceOptions;
import com.fixed.fixitservices.Services.ListOfSubServices;
import com.fixed.fixitservices.Utils.CommonUtils;
import com.fixed.fixitservices.Utils.Constants;
import com.fixed.fixitservices.Utils.GetAddress;
import com.fixed.fixitservices.Utils.NotificationAsync;
import com.fixed.fixitservices.Utils.NotificationObserver;
import com.fixed.fixitservices.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class ChooseAddress extends AppCompatActivity implements NotificationObserver {
    //    CheckBox googleCheckBox, addressCheckBox;
    TextView googleAddress, address;
    Button placeOrder;
    DatabaseReference mDatabase;
    ImageView back;
    RelativeLayout wholeLayout;
    long orderId = 000;
    private long finalTotalTime = 0;
    private long finalTotalCost = 0;
    private String orderDate;

    int addressOption = 0;
    String adminFcmKey;
    RelativeLayout gogleAd;
    private String number;
    Button applyCoupon;
    EditText couponCode;
    HashMap<String, CouponModel> couponMap = new HashMap<>();
    private boolean couponOk;
    private CouponModel couponModel;
    LinearLayout couponAppliedView, applyCouponView;
    TextView orderDetails;
    private AdminModel adminModel;
    EditText instructions;

    @Override
    protected void onResume() {
        super.onResume();
        googleAddress.setText(SharedPrefs.getUser().getGoogleAddress());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address);

        applyCouponView = findViewById(R.id.applyCouponView);
        couponAppliedView = findViewById(R.id.couponAppliedView);
        couponCode = findViewById(R.id.couponCode);
        applyCoupon = findViewById(R.id.applyCoupon);
        orderDetails = findViewById(R.id.orderDetails);
//        googleCheckBox = findViewById(R.id.googleCheckBox);
//        addressCheckBox = findViewById(R.id.addressCheckBox);
        googleAddress = findViewById(R.id.googleAddress);
        address = findViewById(R.id.address);
        placeOrder = findViewById(R.id.placeOrder);
        back = findViewById(R.id.back);
        instructions = findViewById(R.id.instructions);
        wholeLayout = findViewById(R.id.wholeLayout);
        gogleAd = findViewById(R.id.gogleAd);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        orderDate = ChooseServiceOptions.daySelected;
        orderDate = orderDate.replace("\n", "");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        getOrderCountFromDB();
        address.setText(SharedPrefs.getUser().getAddress());
//        googleAddress.setText(SharedPrefs.getUser().getGoogleAddress());


        applyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponCode.getText().length() == 0) {
                    couponCode.setError("Enter coupon");
                } else {
                    checkCoupon(couponCode.getText().toString());
                }
            }
        });

        gogleAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseAddress.this, MapsActivity.class);
                startActivityForResult(i, 1);
            }
        });


//        addressCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (buttonView.isPressed()) {
//                    if (isChecked) {
//                        addressOption = 1;
//                        googleCheckBox.setChecked(false);
//
//                    } else {
//                        addressOption = 2;
//                        googleCheckBox.setChecked(true);
//                    }
//                }
//            }
//        });
//        googleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (buttonView.isPressed()) {
//                    if (isChecked) {
//                        addressOption = 2;
//                        addressCheckBox.setChecked(false);
//                    } else {
//                        addressOption = 1;
//                        addressCheckBox.setChecked(true);
//                    }
//                }
//            }
//        });
        calculateTotal();
        getAdminFCMkey();
        getCouponsFromDb();

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });


    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Confirm placing order?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                placeOrderNow();

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void placeOrderNow() {
        String city = GetAddress.getCity(ChooseAddress.this, SharedPrefs.getUser().getLat(), SharedPrefs.getUser().getLon());
        if (adminModel.getProvidingServiceInCities().contains(city)) {
            User us = SharedPrefs.getUser();
            us.setFcmKey(FirebaseInstanceId.getInstance().getToken());
            SharedPrefs.setUser(us);
            if (SharedPrefs.getUser().getGoogleAddress() != null && SharedPrefs.getUser().getGoogleAddress().equalsIgnoreCase("")) {
                CommonUtils.showToast("Please choose one address");
            } else {
                wholeLayout.setVisibility(View.VISIBLE);
                final OrderModel model = new OrderModel(
                        orderId,
                        System.currentTimeMillis(),
                        SharedPrefs.getUser(),
                        ListOfSubServices.orderList,
                        finalTotalCost,
                        finalTotalTime,
                        instructions.getText().toString(),
                        orderDate,
                        ChooseServiceOptions.timeSelected,
                        "Pending",
                        SharedPrefs.getUser().getGoogleAddress(),
                        SharedPrefs.getUser().getLat(),
                        SharedPrefs.getUser().getLon(),
                        ChooseServiceOptions.buildingType,
                        ListOfSubServices.parentServiceModel.getName(),
                        ListOfSubServices.parentServiceModel.getId(),
                        couponOk,
                        couponOk ? couponModel.getCouponCode() : "",
                        couponOk ? couponModel.getDiscount() : 0,
                        ChooseServiceOptions.buildingType.equalsIgnoreCase("commercial"),
                        adminModel.getTax()


                );


                final String finalOrderDate = orderDate;
                mDatabase.child("Orders").child("" + orderId).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.sendMessage(number, "FIXEDIT \nNew " + model.getServiceName() + " order \nOrder Id: " + orderId
                                + "\n\nClick to view: \n" + Constants.FIXEDIT_URL + "admin/" + orderId);
                        ;
                        CommonUtils.sendMessage(SharedPrefs.getUser().getMobile(), "FIXEDIT\n\nOrder was successfully placed\n" +
                                "Order Id: " + model.getOrderId() + "\n\nYou will receive a call shortly for order confirmation");
                        mDatabase.child("TimeSlots").child(ListOfSubServices.parentService)
                                .child(CommonUtils.getYear(System.currentTimeMillis()))
                                .child(CommonUtils.getMonthName(System.currentTimeMillis()))
                                .child(finalOrderDate)
                                .child(ChooseServiceOptions.timeSelected).setValue(ChooseServiceOptions.timeSelected);

                        mDatabase.child("Users").child(SharedPrefs.getUser().getUsername()).child("Orders").child("" + orderId).setValue(orderId);
                        mDatabase.child("Users").child(SharedPrefs.getUser().getUsername()).child("Cart").removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        wholeLayout.setVisibility(View.GONE);
                                        NotificationAsync notificationAsync = new NotificationAsync(ChooseAddress.this);
                                        String notification_title = "New " + ListOfSubServices.parentServiceModel.getName() + " order from " + SharedPrefs.getUser().getFullName();
                                        String notification_message = "Click to view";
                                        notificationAsync.execute("ali", adminFcmKey, notification_title, notification_message, "Order", "" + orderId);
                                        Intent i = new Intent(ChooseAddress.this, OrderPlaced.class);
                                        i.putExtra("orderId", orderId);
                                        i.putExtra("estimatedCost", finalTotalCost);
                                        i.putExtra("estimatedTime", finalTotalTime);
                                        startActivity(i);
                                        finish();
                                    }
                                });

                    }
                });
            }
        } else {
            CommonUtils.showToast("We are not providing service in " + city);
        }
    }

    private void getCouponsFromDb() {
        mDatabase.child("Coupons").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CouponModel couponModel = snapshot.getValue(CouponModel.class);
                        if (couponModel != null) {
                            couponMap.put(couponModel.getCouponCode(), couponModel);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkCoupon(String s) {
        if (couponMap.containsKey(s)) {
            if (couponMap.get(s).isActive()) {
                couponModel = couponMap.get(s);
                if (System.currentTimeMillis() > couponModel.getCouponStartTime() && System.currentTimeMillis() < couponModel.getCouponEndTime()) {

                    couponOk = true;
                    CommonUtils.showToast("Coupon Applied");
                    applyCouponView.setVisibility(View.GONE);
                    couponAppliedView.setVisibility(View.VISIBLE);
                    calculateTotal();
                } else {
                    CommonUtils.showToast("Coupon is expired");
                }
            } else {
                CommonUtils.showToast("You have used an expired coupon");
            }
        } else {
            CommonUtils.showToast("Coupon does not exist or is invalid");
        }
    }


    private void getAdminFCMkey() {
        mDatabase.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    adminModel = dataSnapshot.getValue(AdminModel.class);
                    if (adminModel != null) {
                        adminFcmKey = adminModel.getFcmKey();
                        number = adminModel.getAdminNumber();
                        SharedPrefs.setAdminFcmKey(adminFcmKey);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private long calculateTotal() {
        float totalMinutes = 0;
        float hours = 0;
        for (ServiceCountModel model : ListOfSubServices.orderList) {
            totalMinutes = totalMinutes + (model.getQuantity() * (model.getService().getTimeMin() + model.getService().getTimeHour()));
        }

        hours = (totalMinutes / 60);
        int h = (int) (totalMinutes / 60);

        float dif = hours - h;

        if (dif > 0.17) {
            finalTotalTime = h + 1;
        } else {
            finalTotalTime = h;
        }

        if (couponOk) {
            if (CommonUtils.getWhichRateToCharge(ChooseServiceOptions.timeSelected)) {

                if (ChooseServiceOptions.buildingType.equalsIgnoreCase("commercial")) {
                    finalTotalCost = finalTotalTime * ListOfSubServices.parentServiceModel.getCommercialServicePeakPrice();

                } else {
                    finalTotalCost = finalTotalTime * ListOfSubServices.parentServiceModel.getPeakPrice();

                }
            } else {
                if (ChooseServiceOptions.buildingType.equalsIgnoreCase("commercial")) {
                    finalTotalCost = finalTotalTime * ListOfSubServices.parentServiceModel.getCommercialServicePrice();

                } else {
                    finalTotalCost = finalTotalTime * ListOfSubServices.parentServiceModel.getServiceBasePrice();

                }
            }
            finalTotalCost = finalTotalCost - (finalTotalCost * couponModel.getDiscount() / 100);
            orderDetails.setText("Total time will last for:" +
                    " " + finalTotalTime + " - " + (finalTotalTime + 1) + "" +
                    " hours and \ncost shall be: Rs" + finalTotalCost + " - Rs" + (finalTotalCost
                    + 200)
                    + "\n" + couponModel.getDiscount() + "% applied");
        } else {
            if (CommonUtils.getWhichRateToCharge(ChooseServiceOptions.timeSelected)) {

                if (ChooseServiceOptions.buildingType.equalsIgnoreCase("commercial")) {
                    finalTotalCost = finalTotalTime * ListOfSubServices.parentServiceModel.getCommercialServicePeakPrice();

                } else {
                    finalTotalCost = finalTotalTime * ListOfSubServices.parentServiceModel.getPeakPrice();

                }
            } else {
                if (ChooseServiceOptions.buildingType.equalsIgnoreCase("commercial")) {
                    finalTotalCost = finalTotalTime * ListOfSubServices.parentServiceModel.getCommercialServicePrice();

                } else {
                    finalTotalCost = finalTotalTime * ListOfSubServices.parentServiceModel.getServiceBasePrice();

                }
            }
            orderDetails.setText("Total time will last for:" +
                    " " + finalTotalTime + " - " + (finalTotalTime + 1) + "" +
                    " hours and \ncost shall be: Rs" + finalTotalCost + " - Rs" + (finalTotalCost + 200));
        }


//        orderDetails.setText("Total time will last for:" +
//                " " + finalTotalTime + " - " + (finalTotalTime + 1) + "" +
//                " hours and \ncost shall be: Rs" + finalTotalCost + " - Rs" + (finalTotalCost + 200));

        return finalTotalCost;
    }

    private void getOrderCountFromDB() {
        mDatabase.child("Orders").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        if (key.contains(CommonUtils.getFormattedDateOnly(System.currentTimeMillis()))) {
                            orderId = Long.parseLong(key) + 1;
                        } else {
                            orderId = Long.parseLong(CommonUtils.getFormattedDateOnly(System.currentTimeMillis()) + String.format("%03d", 1));
                        }

                    }
                } else {
                    orderId = Long.parseLong(CommonUtils.getFormattedDateOnly(System.currentTimeMillis()) + String.format("%03d", 1));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
