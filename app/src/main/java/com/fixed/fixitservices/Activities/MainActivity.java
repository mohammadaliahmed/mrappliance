package com.fixed.fixitservices.Activities;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.bumptech.glide.Glide;
import com.fixed.fixitservices.ApplicationClass;
import com.fixed.fixitservices.Bills.ListOfBills;
import com.fixed.fixitservices.Models.OrderModel;
import com.fixed.fixitservices.Models.PdfModel;
import com.fixed.fixitservices.Models.ServicemanModel;
import com.fixed.fixitservices.Notifications.NotificationHistory;
import com.fixed.fixitservices.UserManagement.LoginMenu;
import com.fixed.fixitservices.Utils.CommonUtils;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fixed.fixitservices.R;
import com.fixed.fixitservices.Services.ListOfServices;
import com.fixed.fixitservices.UserManagement.EditProfile;
import com.fixed.fixitservices.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ImageView nav;
    private DrawerLayout drawer;
    TextView newBooking, transactions, myProfile, bookingHistory;
    TextView username;
    RelativeLayout bookingCall;
    DatabaseReference mDatabase;
    PdfModel pdf;


    CircleImageView servicemanImage;
    TextView serviceManDetails;
    RelativeLayout assignedPersonLayout;
    private ServicemanModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        username = findViewById(R.id.username);
        if (SharedPrefs.getUser() != null) {
            username.setText(SharedPrefs.getUser().getFullName());
            mDatabase.child("Users").child(SharedPrefs.getUser().getUsername()).child("fcmKey").setValue(FirebaseInstanceId.getInstance().getToken());
        } else {
            username.setText("Fixed It");

        }
        nav = findViewById(R.id.nav);
        assignedPersonLayout = findViewById(R.id.assignedPersonLayout);
        bookingCall = findViewById(R.id.bookingCall);
        serviceManDetails = findViewById(R.id.serviceManDetails);
        servicemanImage = findViewById(R.id.servicemanImage);
        newBooking = findViewById(R.id.newBooking);
        transactions = findViewById(R.id.transactions);
        myProfile = findViewById(R.id.myProfile);
        bookingHistory = findViewById(R.id.bookingHistory);

        bookingCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:03158000333"));
                startActivity(i);
            }
        });

        newBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListOfServices.class));
            }
        });
        transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListOfBills.class));
            }
        });


        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPrefs.getUser() != null) {
                    startActivity(new Intent(MainActivity.this, EditProfile.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginMenu.class));

                }
            }
        });

        bookingHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPrefs.getUser() != null) {
                    startActivity(new Intent(MainActivity.this, MyOrders.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginMenu.class));

                }
            }
        });


        assignedPersonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getMobile()));
                startActivity(i);
            }
        });
        getPdfFromDB();
        initDrawer();


        if (SharedPrefs.getUser() != null) {
            mDatabase.child("Users").child(SharedPrefs.getUser().getUsername()).child("Orders").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String orderId = snapshot.getKey();
                            getOrderDetailsFromDB(orderId);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    private void getOrderDetailsFromDB(final String orderId) {
        mDatabase.child("Orders").child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
                    if (orderModel != null) {
                        if (orderModel.isAssigned() && !orderModel.isJobDone()) {
                            getServicemanDetailsFromDb(orderModel.getAssignedTo());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getServicemanDetailsFromDb(String assignedTo) {
        mDatabase.child("Servicemen").child(assignedTo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    bookingCall.setVisibility(View.GONE);
                    assignedPersonLayout.setVisibility(View.VISIBLE);
                    model = dataSnapshot.getValue(ServicemanModel.class);
                    serviceManDetails.setText("" + model.getName() + "\n" + model.getMobile());
                    if (model != null) {
                        try {
                            Glide.with(MainActivity.this).load(model.getImageUrl()).into(servicemanImage);

                        } catch (Exception e) {

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPdfFromDB() {
        mDatabase.child("PDFs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    pdf = dataSnapshot.getValue(PdfModel.class);
                    if (pdf != null) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initDrawer() {

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                } else {
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            // Handle the camera action
            if (pdf.getAboutUs() != null) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(pdf.getAboutUs()));
                startActivity(i);
            } else {
                CommonUtils.showToast("Not available");
            }

        } else if (id == R.id.nav_terms) {
            if (pdf.getTerms() != null) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(pdf.getTerms()));
                startActivity(i);
            } else {
                CommonUtils.showToast("Not available");
            }
        } else if (id == R.id.nav_notifications) {
            if (SharedPrefs.getUser() != null) {
                Intent i = new Intent(MainActivity.this, NotificationHistory.class);

                startActivity(i);
            } else {
                Intent i = new Intent(MainActivity.this, LoginMenu.class);

                startActivity(i);
            }

        } else if (id == R.id.nav_rate_list) {
            if (pdf.getRateList() != null) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(pdf.getRateList()));
                startActivity(i);

            } else {
                CommonUtils.showToast("Not available");
            }
        } else if (id == R.id.nav_social) {
            Intent i = new Intent(MainActivity.this, SocialScreen.class);

            startActivity(i);

        } else if (id == R.id.nav_service_area) {
            if (pdf.getServiceArea() != null) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(pdf.getServiceArea()));
                startActivity(i);
            } else {
                CommonUtils.showToast("Not available");
            }
        } else if (id == R.id.nav_faqs) {
            if (pdf.getFaqs() != null) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(pdf.getFaqs()));
                startActivity(i);

            } else {
                CommonUtils.showToast("Not available");
            }

        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Alert");
            builder.setMessage("Sure to logout?");

            // add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    SharedPrefs.logout();
                    Intent intent = new Intent(MainActivity.this, Splash.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", null);

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void downloadFile(String url, String filename) {
        DownloadManager downloadManager = (DownloadManager) ApplicationClass.getInstance().getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename + ".pdf");
        Long referene = downloadManager.enqueue(request);
    }
}
