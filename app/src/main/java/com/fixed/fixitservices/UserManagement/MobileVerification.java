package com.fixed.fixitservices.UserManagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fixed.fixitservices.Activities.MainActivity;
import com.fixed.fixitservices.Models.User;
import com.fixed.fixitservices.R;
import com.fixed.fixitservices.Utils.CommonUtils;
import com.fixed.fixitservices.Utils.PrefManager;
import com.fixed.fixitservices.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class MobileVerification extends AppCompatActivity {
    Button proceed;
    EditText mobile;
    PhoneAuthProvider phoneAuth;
    DatabaseReference mDatabase;
    private PrefManager prefManager;
    RelativeLayout wholeLayout;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);
        prefManager = new PrefManager(this);


        phoneAuth = PhoneAuthProvider.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        text = findViewById(R.id.text);
        wholeLayout = findViewById(R.id.wholeLayout);
        proceed = findViewById(R.id.proceed);
        mobile = findViewById(R.id.mobile);
        mobile.setText(SharedPrefs.getUser().getMobile());

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobile.getText().toString().trim().isEmpty()) {
                    mobile.setError("Enter number");
                } else if (mobile.getText().length() < 10) {
                    mobile.setError("Wrong phone number");
                } else {
                    verifyMobile();
                }
            }
        });

    }

    private void verifyMobile() {
        String mob = mobile.getText().toString();
        if (mob.startsWith("03")) {
            mob=mob.substring(1);
            mob = "+92" + mob;
            mobile.setText(mob);
        }
        wholeLayout.setVisibility(View.VISIBLE);
        text.setText("Sending code..");
        phoneAuth.verifyPhoneNumber(
                mob,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        wholeLayout.setVisibility(View.GONE);
                        CommonUtils.showToast("Completed");
                        mDatabase.child("Users").child(SharedPrefs.getUser().getUsername()).child("numberVerified").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                launchHomeScreen();
                            }
                        });
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        CommonUtils.showToast(e.getMessage());
                        wholeLayout.setVisibility(View.GONE);
                        text.setText("");
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

//                        CommonUtils.showToast("Code sent");
//                        CommonUtils.showToast("Waiting to verify");
                        text.setText("Code sent\nWaiting to verify..");

                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        CommonUtils.showToast("Time out");
                        wholeLayout.setVisibility(View.GONE);
                        text.setText("");
                        proceed.setText("Resend code again");
                    }

                }

        );
    }

    private void launchHomeScreen() {
        User user = SharedPrefs.getUser();
        user.setNumberVerified(true);
        SharedPrefs.setUser(user);
        SharedPrefs.setIsLoggedIn("yes");
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(MobileVerification.this, MainActivity.class));


        finish();
    }
}
