package com.fixedit.fixitservices.UserManagement;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fixedit.fixitservices.Models.User;
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

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {
    Button update;
    EditText firstname, lastname, username, password, email, mobile, phone, address;
    DatabaseReference mDatabase;
    private User user;
    TextView titleName;
    ImageView back;
    RelativeLayout wholeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        update = findViewById(R.id.update);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        phone = findViewById(R.id.phone);
        back = findViewById(R.id.back);
        titleName = findViewById(R.id.titleName);
        address = findViewById(R.id.address);
        wholeLayout = findViewById(R.id.wholeLayout);


        titleName.setText(SharedPrefs.getUser().getFullName());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        getUserFromDB();

        update.setOnClickListener(new View.OnClickListener() {
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
                } else if (mobile.getText().toString().trim().isEmpty()) {
                    mobile.setError("Enter mobile");
                }  else if (address.getText().toString().trim().isEmpty()) {
                    address.setError("Enter address");
                } else {
                    updateUser();
                }
            }
        });


    }


    private void getUserFromDB() {

        mDatabase.child("Users").child(SharedPrefs.getUser().getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    wholeLayout.setVisibility(View.GONE);
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {

                        firstname.setText(user.getFirstname());
                        lastname.setText(user.getLastname());
                        username.setText(user.getUsername());
                        password.setText(user.getPassword());
                        email.setText(user.getEmail());
                        mobile.setText(user.getMobile());
                        address.setText(user.getAddress());
                        phone.setText(user.getPhone());
                        SharedPrefs.setUser(user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateUser() {
        wholeLayout.setVisibility(View.VISIBLE);
        HashMap<String, Object> map = new HashMap<>();
        map.put("firstname", firstname.getText().toString());
        map.put("lastname", lastname.getText().toString());
        map.put("password", password.getText().toString());
        map.put("email", email.getText().toString());
        map.put("mobile", mobile.getText().toString());
        map.put("phone", phone.getText().toString());
        map.put("address", address.getText().toString());


        mDatabase.child("Users")
                .child(SharedPrefs.getUser().getUsername()).updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        wholeLayout.setVisibility(View.GONE);
                        CommonUtils.showToast("Updated");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.showToast(e.getMessage());
            }
        });

    }


}
