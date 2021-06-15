package com.appsinventiv.mrappliance.Services;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.appsinventiv.mrappliance.Activities.BookingSumary;
import com.appsinventiv.mrappliance.Adapters.TimeslotsAdapter;
import com.appsinventiv.mrappliance.R;
import com.appsinventiv.mrappliance.Utils.CommonUtils;
import com.appsinventiv.mrappliance.Utils.ConnectivityManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class ChooseServiceOptions extends AppCompatActivity {

    ImageView back;

    RelativeLayout next;
    RelativeLayout commercial, residential, villa;
    ImageView commercialTick, residentialTick, villaTick;
    public static String buildingType="Residential";
    public static String timeSelected;
    public static String monthSelected, daySelected;
    DatabaseReference mDatabase;
    HashMap<String, ArrayList<String>> map = new HashMap<>();


    RecyclerView recyclerview;
    TimeslotsAdapter adapter;
    ArrayList<String> itemList = new ArrayList<>();
    CalendarView calender;
    String getValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_service_options);
        recyclerview = findViewById(R.id.recyclerview);
        calender = findViewById(R.id.calender);


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

        monthSelected = CommonUtils.getMonthName(System.currentTimeMillis());


        commercial = findViewById(R.id.commercial);
        residential = findViewById(R.id.residential);
        residentialTick = findViewById(R.id.residentialTick);
        commercialTick = findViewById(R.id.commercialTick);
        villaTick = findViewById(R.id.villaTick);
        villa = findViewById(R.id.villa);


        back = findViewById(R.id.back);
        next = findViewById(R.id.next);

        daySelected = CommonUtils.getDay(System.currentTimeMillis()) + CommonUtils.getDayName(System.currentTimeMillis()).toLowerCase();
        getValue=CommonUtils.getMonthName(System.currentTimeMillis())+daySelected;
        calender.setMinDate(System.currentTimeMillis() - 1000);
        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                monthSelected = CommonUtils.getMonthNameAbbr(month);
                daySelected = dayOfMonth + CommonUtils.getNameOfDay(year, month + 1, dayOfMonth).toLowerCase();
                getValue=CommonUtils.getMonthNameAbbr(month)+daySelected;
                String dy = CommonUtils.getDay(System.currentTimeMillis());
                String mn = CommonUtils.getMonthNumber(System.currentTimeMillis());
                if ((Integer.parseInt(dy) == dayOfMonth) && (Integer.parseInt(mn) == (month + 1))) {
                    ArrayList<String> list = map.get(getValue) == null ? new ArrayList<String>() : map.get(getValue);
                    adapter.setUnavailableTime(list);
                    adapter.setavailableTime(itemList);
                    itemList.clear();
                    calculateTime();
                    adapter.setavailableTime(itemList);
                } else {
                    ArrayList<String> list = map.get(getValue) == null ? new ArrayList<String>() : map.get(getValue);
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
                villaTick.setVisibility(View.GONE);
            }
        });
        villa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildingType = "Villa";
                commercialTick.setVisibility(View.GONE);
                residentialTick.setVisibility(View.GONE);
                villaTick.setVisibility(View.VISIBLE);
            }
        });
        residential.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildingType = "Residential";
                residentialTick.setVisibility(View.VISIBLE);
                commercialTick.setVisibility(View.GONE);
                villaTick.setVisibility(View.GONE);

            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildingType="Residential";
                if (timeSelected == null) {
                    CommonUtils.showToast("Select available time slot");

                } else {

                    if (ConnectivityManager.isNetworkConnected(ChooseServiceOptions.this)) {
                        startActivity(new Intent(ChooseServiceOptions.this, BookingSumary.class));
                    } else {
                        CommonUtils.showToast("Please check internet connection");
                    }
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
        if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 9) {
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

        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 9 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 10) {
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

        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 10 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 11) {
            itemList.add("12:00 pm");
            itemList.add("1:00 pm");
            itemList.add("2:00 pm");
            itemList.add("3:00 pm");
            itemList.add("4:00 pm");
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");

        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 11 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 12) {
            itemList.add("1:00 pm");
            itemList.add("2:00 pm");
            itemList.add("3:00 pm");
            itemList.add("4:00 pm");
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");

        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 12 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 13) {
            itemList.add("2:00 pm");
            itemList.add("3:00 pm");
            itemList.add("4:00 pm");
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");

        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 13 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 14) {
            itemList.add("3:00 pm");
            itemList.add("4:00 pm");
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");

        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 14 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 15) {
            itemList.add("3:00 pm");
            itemList.add("4:00 pm");
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");

        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 15 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 16) {
            itemList.add("5:00 pm");
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");

        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 16 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 17) {
            itemList.add("6:00 pm");
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");

        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 17 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 18) {
            itemList.add("7:00 pm");
            itemList.add("8:00 pm");
        } else if (Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) >= 18 && Integer.parseInt(CommonUtils.getHour(System.currentTimeMillis())) < 19) {
            itemList.add("8:00 pm");

        }
    }


    private void getTimeSlotsFromDB() {
        mDatabase.child("TimeSlots")
//                .child(ListOfSubServices.parentService)
                .child(CommonUtils.getYear(System.currentTimeMillis()))

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot allMonths : dataSnapshot.getChildren()) {
                                for (DataSnapshot allDays : allMonths.getChildren()) {
                                    ArrayList<String> timeList = new ArrayList<>();

                                    for (DataSnapshot allTimes : allDays.getChildren()) {
                                        timeList.add(allTimes.getValue(String.class));
                                    }
                                    map.put(allMonths.getKey() + allDays.getKey(), timeList);
                                }
                            }

                            ArrayList<String> item = map.get(getValue);
                            if (item == null) {
                                adapter.setUnavailableTime(new ArrayList<String>());
                            } else {
                                adapter.setUnavailableTime(item);
                            }
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
