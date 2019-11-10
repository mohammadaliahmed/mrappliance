package com.fixed.fixitservices.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fixed.fixitservices.R;
import com.fixed.fixitservices.Services.ChooseServiceOptions;
import com.fixed.fixitservices.Services.ListOfSubServices;
import com.fixed.fixitservices.UserManagement.LoginMenu;

public class Splash extends AppCompatActivity {
    public static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        ChooseServiceOptions.buildingType=null;
        ChooseServiceOptions.timeSelected=null;
        ListOfSubServices.orderList=null;
        ListOfSubServices.parentService=null;
        ListOfSubServices.parentServiceModel=null;
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
//                if(SharedPrefs.getIsSkipped().equalsIgnoreCase("true")){
//                    Intent i = new Intent(Splash.this, MainActivity.class);
//                    startActivity(i);
//                }else {
                    Intent i = new Intent(Splash.this, LoginMenu.class);
                    startActivity(i);
//                }

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
//          <!--alias:fixitclient
//        pass:fixitclient-->
    }
}
