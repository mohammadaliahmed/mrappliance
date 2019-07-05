package com.fixedit.fixitservices.Services;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.fixedit.fixitservices.Activities.BookingSumary;
import com.fixedit.fixitservices.R;
import com.fixedit.fixitservices.Utils.CommonUtils;
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
    private RadioGroup radioTime;
    private RadioButton radioButton;
    RadioButton day1, day2, day3;
    DatabaseReference mDatabase;
    HashMap<String, ArrayList<String>> map = new HashMap<>();
    String dayNo1, dayNo2, dayNo3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_service_options);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        dayNo1 = CommonUtils.getDay(System.currentTimeMillis()) + CommonUtils.getDayName(System.currentTimeMillis());
        dayNo2 = CommonUtils.getDay(System.currentTimeMillis() + 86400000) + CommonUtils.getDayName(System.currentTimeMillis() + 86400000);
        dayNo3 = CommonUtils.getDay(System.currentTimeMillis() + 86400000 + 86400000) + CommonUtils.getDayName(System.currentTimeMillis() + 86400000 + 86400000);


        commercial = findViewById(R.id.commercial);
        residential = findViewById(R.id.residential);
        residentialTick = findViewById(R.id.residentialTick);
        commercialTick = findViewById(R.id.commercialTick);
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        radioTime = findViewById(R.id.radioTime);
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
                    for (int i = 0; i < 5; i++) {
                        ((RadioButton) radioTime.getChildAt(i)).setEnabled(true);
                    }
                    daySelected = day1.getText().toString();
                    ArrayList<String> list = map.get(dayNo1)==null?new ArrayList<String>():map.get(dayNo1);
                    for (String item : list) {
                        if (item.equalsIgnoreCase("10:00 am")) {
                            ((RadioButton) radioTime.getChildAt(0)).setEnabled(false);
                        } else if (item.equalsIgnoreCase("12:00 pm")) {
                            ((RadioButton) radioTime.getChildAt(1)).setEnabled(false);
                        } else if (item.equalsIgnoreCase("2:00 pm")) {
                            ((RadioButton) radioTime.getChildAt(2)).setEnabled(false);
                        } else if (item.equalsIgnoreCase("4:00 pm")) {
                            ((RadioButton) radioTime.getChildAt(3)).setEnabled(false);
                        } else if (item.equalsIgnoreCase("6:00 pm")) {
                            ((RadioButton) radioTime.getChildAt(4)).setEnabled(false);
                        }
                    }
                }
            }
        });
        day2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    for (int i = 0; i < 5; i++) {
                        ((RadioButton) radioTime.getChildAt(i)).setEnabled(true);
                    }
                    daySelected = day2.getText().toString();
                    ArrayList<String> list = map.get(dayNo2)==null?new ArrayList<String>():map.get(dayNo2);
                    for (String item : list) {
                        if (item.equalsIgnoreCase("10:00 am")) {
                            ((RadioButton) radioTime.getChildAt(0)).setEnabled(false);
                        } else if (item.equalsIgnoreCase("12:00 pm")) {
                            ((RadioButton) radioTime.getChildAt(1)).setEnabled(false);
                        } else if (item.equalsIgnoreCase("2:00 pm")) {
                            ((RadioButton) radioTime.getChildAt(2)).setEnabled(false);
                        } else if (item.equalsIgnoreCase("4:00 pm")) {
                            ((RadioButton) radioTime.getChildAt(3)).setEnabled(false);
                        } else if (item.equalsIgnoreCase("6:00 pm")) {
                            ((RadioButton) radioTime.getChildAt(4)).setEnabled(false);
                        }
                    }
                }
            }
        });
        day3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    for (int i = 0; i < 5; i++) {
                        ((RadioButton) radioTime.getChildAt(i)).setEnabled(true);
                    }
                    daySelected = day3.getText().toString();
                    ArrayList<String> list = map.get(dayNo3)==null?new ArrayList<String>():map.get(dayNo3);
                    for (String item : list) {
                        if (item.equalsIgnoreCase("10:00 am")) {
                            ((RadioButton) radioTime.getChildAt(0)).setEnabled(false);
                        } else if (item.equalsIgnoreCase("12:00 pm")) {
                            ((RadioButton) radioTime.getChildAt(1)).setEnabled(false);
                        } else if (item.equalsIgnoreCase("2:00 pm")) {
                            ((RadioButton) radioTime.getChildAt(2)).setEnabled(false);
                        } else if (item.equalsIgnoreCase("4:00 pm")) {
                            ((RadioButton) radioTime.getChildAt(3)).setEnabled(false);
                        } else if (item.equalsIgnoreCase("6:00 pm")) {
                            ((RadioButton) radioTime.getChildAt(4)).setEnabled(false);
                        }
                    }
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
                int selectedId = radioTime.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedId);
                if (radioButton == null) {
                    CommonUtils.showToast("Select available time slot");

                } else if (buildingType == null) {
                    CommonUtils.showToast("Select building type");
                } else {
                    timeSelected = radioButton.getText().toString();

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


//        getDaysOffFromDB();
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
        mDatabase.child("TimeSlots").child(CommonUtils.getYear(System.currentTimeMillis()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            ArrayList<String> day1List = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.child(dayNo1).getChildren()) {
                                day1List.add(snapshot.getKey());

                            }
                            for (String item : day1List) {
                                if (item.equalsIgnoreCase("10:00 am")) {
                                    ((RadioButton) radioTime.getChildAt(0)).setEnabled(false);
                                } else if (item.equalsIgnoreCase("12:00 pm")) {
                                    ((RadioButton) radioTime.getChildAt(1)).setEnabled(false);
                                } else if (item.equalsIgnoreCase("2:00 pm")) {
                                    ((RadioButton) radioTime.getChildAt(2)).setEnabled(false);
                                } else if (item.equalsIgnoreCase("4:00 pm")) {
                                    ((RadioButton) radioTime.getChildAt(3)).setEnabled(false);
                                } else if (item.equalsIgnoreCase("6:00 pm")) {
                                    ((RadioButton) radioTime.getChildAt(4)).setEnabled(false);
                                }
                            }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
