package com.fixed.fixitservices.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fixed.fixitservices.R;
import com.fixed.fixitservices.Services.ChooseServiceOptions;
import com.fixed.fixitservices.Services.ListOfSubServices;
import com.fixed.fixitservices.UserManagement.LoginMenu;

import androidx.appcompat.app.AppCompatActivity;

public class SocialScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

    }
}
