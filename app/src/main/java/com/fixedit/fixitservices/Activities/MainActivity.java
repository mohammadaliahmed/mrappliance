package com.fixedit.fixitservices.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.fixedit.fixitservices.UserManagement.LoginMenu;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fixedit.fixitservices.R;
import com.fixedit.fixitservices.Services.ListOfServices;
import com.fixedit.fixitservices.UserManagement.EditProfile;
import com.fixedit.fixitservices.Utils.SharedPrefs;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ImageView nav;
    private DrawerLayout drawer;
    TextView newBooking, transactions, myProfile, bookingHistory;
    TextView username;
    RelativeLayout bookingCall;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase=FirebaseDatabase.getInstance().getReference();

        username = findViewById(R.id.username);
        if (SharedPrefs.getUser() != null) {
            username.setText(SharedPrefs.getUser().getFullName());
            mDatabase.child("Users").child(SharedPrefs.getUser().getUsername()).child("fcmKey").setValue(FirebaseInstanceId.getInstance().getToken());
        } else {
            username.setText("Fixed It");

        }
        nav = findViewById(R.id.nav);
        bookingCall = findViewById(R.id.bookingCall);
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


        initDrawer();
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
        } else if (id == R.id.nav_terms) {

        } else if (id == R.id.nav_rate_list) {

        } else if (id == R.id.nav_service_area) {

        } else if (id == R.id.nav_faqs) {

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
}
