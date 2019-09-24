package com.fixed.fixitservices.Services;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.fixed.fixitservices.Activities.BookingSumary;
import com.fixed.fixitservices.Adapters.TimeslotsAdapter;
import com.fixed.fixitservices.R;
import com.fixed.fixitservices.Utils.CommonUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChooseServiceOptions extends AppCompatActivity {

    ImageView back;

    RelativeLayout next;
    RelativeLayout commercial, residential;
    ImageView commercialTick, residentialTick;
    public static String buildingType;
    public static String timeSelected;
    public static String daySelected;
    RadioButton day1, day2, day3;
    DatabaseReference mDatabase;
    HashMap<String, ArrayList<String>> map = new HashMap<>();
    String dayNo1, dayNo2, dayNo3;


    RecyclerView recyclerview;
    TimeslotsAdapter adapter;
    ArrayList<String> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_service_options);
        recyclerview = findViewById(R.id.recyclerview);


        calculateTime();


        recyclerview.setLayoutManager(new GridLayoutManager(this, 3));


        mDatabase = FirebaseDatabase.getInstance().getReference();
        adapter = new TimeslotsAdapter(ChooseServiceOptions.this, itemList, new ArrayList<String>());
        adapter.setCallback(new TimeslotsAdapter.TimeSlotsCallback() {
            @Override
            public void optionSelected(String timeChosen) {
                timeSelected = timeChosen;
            }
        });
        recyclerview.setAdapter(adapter);


        dayNo1 = CommonUtils.getDay(System.currentTimeMillis()) + CommonUtils.getDayName(System.currentTimeMillis());
        dayNo2 = CommonUtils.getDay(System.currentTimeMillis() + 86400000) + CommonUtils.getDayName(System.currentTimeMillis() + 86400000);
        dayNo3 = CommonUtils.getDay(System.currentTimeMillis() + 86400000 + 86400000) + CommonUtils.getDayName(System.currentTimeMillis() + 86400000 + 86400000);


        commercial = findViewById(R.id.commercial);
        residential = findViewById(R.id.residential);
        residentialTick = findViewById(R.id.residentialTick);
        commercialTick = findViewById(R.id.commercialTick);
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        day3 = findViewById(R.id.day3);

        day1.setText(CommonUtils.getDay(System.currentTimeMillis()) + "\n" + CommonUtils.getDayName(System.currentTimeMillis()));
        day2.setText(CommonUtils.getDay(System.currentTimeMillis() + 86400000) + "\n" + CommonUtils.getDayName(System.currentTimeMillis() + 86400000));
        day3.setText(CommonUtils.getDay(System.currentTimeMillis() + 86400000 + 86400000) + "\n" + CommonUtils.getDayName(System.currentTimeMillis() + 86400000 + 86400000));


        day1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    daySelected = day1.getText().toString();
                    ArrayList<String> list = map.get(dayNo1) == null ? new ArrayList<String>() : map.get(dayNo1);
                    adapter.setUnavailableTime(list);
                    adapter.setavailableTime(itemList);
                    itemList.clear();
                    calculateTime();
                    adapter.setavailableTime(itemList);


                }
            }
        });
        day2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {

                    daySelected = day2.getText().toString();
                    ArrayList<String> list = map.get(dayNo2) == null ? new ArrayList<String>() : map.get(dayNo2);
                    adapter.setUnavailableTime(list);
                    itemList.clear();
                    itemList.add("10:00 am");
                    itemList.add("11:00 am");
                    itemList.add("12:00 pm");
                    itemList.add("1:00 pm");
                    itemList.add("2:00 pm");
                    itemList.add("3:00 pm");
                    itemList.add("4:00 pm");
                    itemList.add("5:00 pm");
                    itemList.add("6:00 pm");
                    itemList.add("7:00 pm");
                    itemList.add("8:00 pm");
                    itemList.add("9:00 pm");
                    itemList.add("10:00 pm");
                    itemList.add("11:00 pm");
                    itemList.add("12:00 am");
                    adapter.setavailableTime(itemList);


                }
            }
        });
        day3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    daySelected = day3.getText().toString();
                    ArrayList<String> list = map.get(dayNo3) == null ? new ArrayList<String>() : map.get(dayNo3);
                    adapter.setUnavailableTime(list);
                    itemList.clear();
                    itemList.add("10:00 am");
                    itemList.add("11:00 am");
                    itemList.add("12:00 pm");
                    itemList.add("1:00 pm");
                    itemList.add("2:00 pm");
                    itemList.add("3:00 pm");
                    itemList.add("4:00 pm");
                    itemList.add("5:00 pm");
                    itemList.add("6:00 pm");
                    itemList.add("7:00 pm");
                    itemList.add("8:00 pm");
                    itemList.add("9:00 pm");
                    itemList.add("10:00 pm");
                    itemList.add("11:00 pm");
                    itemList.add("12:00 am");
                    adapter.setavailableTime(itemList);

                }
            }
        });


        commercial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildingType = "Commercial";
                commercialTick.setVisibility(View.VISIBLE);
                residentialTick.setVisibility(View.GONE);
            }
        });
        residential.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildingType = "Residential";
                residentialTick.setVisibility(View.VISIBLE);
                commercialTick.setVisibility(View.GONE);

            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timeSelected == null) {
                    CommonUtils.showToast("Select available time slot");

                } else if (buildingType == null) {
                    CommonUtils.showToast("Select building type");
                } else {


                    startActivity(new Intent(ChooseServiceOptions.this, BookingSumary.class));
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        getTimeSlotsFromDB();
        day1.setChecked(true);
        daySelected = day1.getText().toString();


        if (ListOfSubServices.parentServiceModel.offeringResidentialService) {
            residential.setVisibility(View.VISIBLE);
        } else {
            residential.setVisibility(View.GONE);

        }
        if (ListOfSubServices.parentServiceModel.offeringCommercialService) {
            commercial.setVisibility(View.VISIBLE);
        } else {
            commercial.setVisibility(View.GONE);

        }


//        getDaysOffFromDB();
    }

    private void calculateTime() {
        if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 10) {
            itemList.add("10:00 am");
            itemList.add("11:00 am");
            itemList.add("12:00 pm");
            itemList.add("1:00 pm");
            itemList.add("2:00 pm");
            itemList.add("3:00 pm");
            itemList.add("4:00 pm");
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");
            itemList.add("9:00 pm");
            itemList.add("10:00 pm");
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 10 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 11) {
            itemList.add("11:00 am");
            itemList.add("12:00 pm");
            itemList.add("1:00 pm");
            itemList.add("2:00 pm");
            itemList.add("3:00 pm");
            itemList.add("4:00 pm");
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");
            itemList.add("9:00 pm");
            itemList.add("10:00 pm");
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 11 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 12) {
            itemList.add("12:00 pm");
            itemList.add("1:00 pm");
            itemList.add("2:00 pm");
            itemList.add("3:00 pm");
            itemList.add("4:00 pm");
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");
            itemList.add("9:00 pm");
            itemList.add("10:00 pm");
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 12 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 13) {
            itemList.add("1:00 pm");
            itemList.add("2:00 pm");
            itemList.add("3:00 pm");
            itemList.add("4:00 pm");
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");
            itemList.add("9:00 pm");
            itemList.add("10:00 pm");
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 13 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 14) {
            itemList.add("2:00 pm");
            itemList.add("3:00 pm");
            itemList.add("4:00 pm");
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");
            itemList.add("9:00 pm");
            itemList.add("10:00 pm");
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 14 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 15) {
            itemList.add("3:00 pm");
            itemList.add("4:00 pm");
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");
            itemList.add("9:00 pm");
            itemList.add("10:00 pm");
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 15 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 16) {
            itemList.add("4:00 pm");
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");
            itemList.add("9:00 pm");
            itemList.add("10:00 pm");
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 16 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 17) {
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");
            itemList.add("9:00 pm");
            itemList.add("10:00 pm");
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 17 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 18) {
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");
            itemList.add("9:00 pm");
            itemList.add("10:00 pm");
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 18 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 19) {
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");
            itemList.add("9:00 pm");
            itemList.add("10:00 pm");
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 19 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 20) {
            itemList.add("8:00 pm");
            itemList.add("9:00 pm");
            itemList.add("10:00 pm");
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 20 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 21) {
            itemList.add("9:00 pm");
            itemList.add("10:00 pm");
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 21 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 22) {
            itemList.add("10:00 pm");
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 22 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 23) {
            itemList.add("11:00 pm");
            itemList.add("12:00 am");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 23 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 24) {
            itemList.add("12:00 am");
        }
    }

    private void getDaysOffFromDB() {
        mDatabase.child("DaysOff")
                .child(CommonUtils.getYear(System.currentTimeMillis()))
                .child(CommonUtils.getMonthName(System.currentTimeMillis()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot daysOff : dataSnapshot.getChildren()) {
                                if (daysOff.getKey().equalsIgnoreCase(dayNo1)) {
                                    day1.setEnabled(false);
                                    day2.setChecked(true);
                                } else if (daysOff.getKey().equalsIgnoreCase(dayNo2)) {
                                    day2.setEnabled(false);
                                    day1.setChecked(true);

                                } else if (daysOff.getKey().equalsIgnoreCase(dayNo3)) {
                                    day3.setEnabled(false);
                                    day1.setChecked(true);

                                } else {
                                    day1.setChecked(true);

                                    day1.setEnabled(true);
                                    day2.setEnabled(true);
                                    day3.setEnabled(true);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void getTimeSlotsFromDB() {
        mDatabase.child("TimeSlots").child(ListOfSubServices.parentService)
                .child(CommonUtils.getYear(System.currentTimeMillis()))
                .child(CommonUtils.getMonthName(System.currentTimeMillis()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            ArrayList<String> day1List = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.child(dayNo1).getChildren()) {
                                day1List.add(snapshot.getKey());

                            }
                            adapter.setUnavailableTime(day1List);
                            itemList.clear();
                            calculateTime();
                            adapter.setavailableTime(itemList);


                            map.put(dayNo1, day1List);
                            ArrayList<String> day1List2 = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.child(dayNo2).getChildren()) {
                                day1List2.add(snapshot.getKey());

                            }
                            map.put(dayNo2, day1List2);
                            ArrayList<String> day1Lis3 = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.child(dayNo3).getChildren()) {
                                day1Lis3.add(snapshot.getKey());

                            }
                            map.put(dayNo3, day1Lis3);


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
    public void onBackPressed() {
        ChooseServiceOptions.buildingType = null;
        ChooseServiceOptions.timeSelected = null;
        super.onBackPressed();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ChooseServiceOptions.buildingType = null;
            ChooseServiceOptions.timeSelected = null;

            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
