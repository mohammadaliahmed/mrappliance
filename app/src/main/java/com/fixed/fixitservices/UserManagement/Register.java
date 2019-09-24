package com.fixed.fixitservices.UserManagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fixed.fixitservices.Activities.MainActivity;
import com.fixed.fixitservices.Activities.MapsActivity;
import com.fixed.fixitservices.Models.User;
import com.fixed.fixitservices.R;
import com.fixed.fixitservices.Utils.CommonUtils;
import com.fixed.fixitservices.Utils.PrefManager;
import com.fixed.fixitservices.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    Button signUp;
    EditText firstname, lastname, username, password, confirmPassword, email, mobile, phone, address;
    DatabaseReference mDatabase;
    HashMap<String, User> map = new HashMap<>();
    private PrefManager prefManager;
    TextView googleMapAddress;
    private double lat, lon;
    private String returnString;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        signUp = findViewById(R.id.signUp);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        googleMapAddress = findViewById(R.id.googleMapAddress);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            if(LoginMenu.account!=null) {
                firstname.setText((LoginMenu.account.getDisplayName().split(" "))[0]);
                lastname.setText((LoginMenu.account.getDisplayName().split(" "))[1]);
                username.setText(LoginMenu.account.getDisplayName().replace(" ", ""));
                password.setText("abc123");
                confirmPassword.setText("abc123");
                password.setVisibility(View.GONE);
                confirmPassword.setVisibility(View.GONE);
                email.setText(LoginMenu.account.getEmail());
            }else if(LoginMenu.profile!=null){
                firstname.setText(LoginMenu.profile.getFirstName());
                lastname.setText(LoginMenu.profile.getLastName());
                username.setText(LoginMenu.profile.getFirstName()+LoginMenu.profile.getLastName());
                password.setText("abc123");
                confirmPassword.setText("abc123");
                password.setVisibility(View.GONE);
                confirmPassword.setVisibility(View.GONE);
//                email.setText(LoginMenu.account.getEmail());

            }

        }

        prefManager = new PrefManager(this);
//        if (!prefManager.isFirstTimeLaunch()) {
//            launchHomeScreen();
//            finish();
//        }


        mDatabase = FirebaseDatabase.getInstance().getReference();
        getAllUsersFromDB();


        googleMapAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Register.this, MapsActivity.class);
                startActivityForResult(i, 1);
            }
        });


        firstname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    /* Write your logic here that will be executed when user taps next button */
                    lastname.requestFocus();

                    handled = true;
                }

                return handled;
            }
        });
        lastname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    /* Write your logic here that will be executed when user taps next button */
                    username.requestFocus();

                    handled = true;
                }

                return handled;
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstname.getText().toString().trim().isEmpty()) {
                    firstname.setError("Enter firstname");
                } else if (lastname.getText().toString().trim().isEmpty()) {
                    lastname.setError("Enter lastname");
                } else if (username.getText().toString().trim().isEmpty()) {
                    username.setError("Enter username");
                } else if (password.getText().toString().trim().isEmpty()) {
                    password.setError("Enter password");
                } else if (confirmPassword.getText().toString().trim().isEmpty()) {
                    confirmPassword.setError("Enter confirmPassword");
                } else if (!password.getText().toString().equalsIgnoreCase(confirmPassword.getText().toString())) {
                    confirmPassword.setError("Password does not match");
                } else if (mobile.getText().toString().trim().isEmpty()) {
                    mobile.setError("Enter mobile");
                } else if (mobile.getText().length() < 10) {
                    mobile.setError("Wrong mobile number");
                } else if (address.getText().toString().trim().isEmpty()) {
                    address.setError("Enter address");
                } else if (returnString == null) {
                    CommonUtils.showToast("Please select google address");
                } else {
//                    singUpUser();
                    checkUser();
                }
            }
        });


    }

    private void checkUser() {
        if (map.containsKey(username.getText().toString())) {
            CommonUtils.showToast("Username is already taken\nPlease choose another");
        } else {
            singUpUser();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                returnString = data.getStringExtra("address");
                lat = data.getDoubleExtra("lat", 0.0);
                lon = data.getDoubleExtra("lon", 0.0);

                googleMapAddress.setText(returnString);


            }
        } else if (requestCode == 1 && resultCode == 0) {
        }
    }

    private void singUpUser() {
        final User user = new User(
                firstname.getText().toString(),
                lastname.getText().toString(),
                userId == null ? username.getText().toString() : userId,
                password.getText().toString(),
                email.getText().toString(),

                mobile.getText().toString(),
                phone.getText().toString(),
                address.getText().toString(),
                SharedPrefs.getFcmKey(),
                System.currentTimeMillis(),
                false,
                returnString,
                lat,
                lon
        );

        mDatabase.child("Users").child(userId == null ? username.getText().toString() : userId).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SharedPrefs.setUser(user);
                startActivity(new Intent(Register.this, MobileVerification.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.showToast(e.getMessage());
            }
        });

    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);

        Intent i = new Intent(Register.this, MainActivity.class);
        i.putExtra("mobile", mobile.getText().toString());
        i.putExtra("username", username.getText().toString());
        startActivity(i);

        finish();
    }
}
