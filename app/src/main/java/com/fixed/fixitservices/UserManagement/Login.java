package com.fixed.fixitservices.UserManagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.fixed.fixitservices.Activities.MainActivity;
import com.fixed.fixitservices.Models.User;
import com.fixed.fixitservices.R;
import com.fixed.fixitservices.Utils.CommonUtils;
import com.fixed.fixitservices.Utils.PrefManager;
import com.fixed.fixitservices.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    RelativeLayout login;
    EditText username, password;
    DatabaseReference mDatabase;
    HashMap<String, User> map = new HashMap<>();
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getAllUsersFromDB();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((username.getText().toString().trim().isEmpty())) {
                    username.setError("Enter username");
                } else if ((password.getText().toString().trim().isEmpty())) {
                    password.setError("Enter password");
                } else {
                    checkUser();
                }
            }
        });

    }

    private void checkUser() {
        if (map.containsKey(username.getText().toString())) {
            if (map.get(username.getText().toString()).getPassword().equalsIgnoreCase(password.getText().toString())) {
                loginUser();
            } else {
                CommonUtils.showToast("Wrong password");
            }
        } else {
            CommonUtils.showToast("User does not exist\nPlease signup");
        }
    }


    private void getAllUsersFromDB() {
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    map.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {

                            map.put(snapshot.getKey(), user);


                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loginUser() {
        SharedPrefs.setUser(map.get(username.getText().toString()));
        SharedPrefs.setIsLoggedIn("yes");
        CommonUtils.showToast("Logged in successfully");

        if (!map.get(username.getText().toString()).isNumberVerified()) {
            Intent i = new Intent(Login.this, MobileVerification.class);

            startActivity(i);
        } else {
            prefManager.setFirstTimeLaunch(false);

            Intent i = new Intent(Login.this, MainActivity.class);

            startActivity(i);

            finish();
        }

    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);

        Intent i = new Intent(Login.this, MainActivity.class);

        startActivity(i);

        finish();
    }
}
