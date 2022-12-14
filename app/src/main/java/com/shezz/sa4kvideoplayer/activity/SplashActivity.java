package com.shezz.sa4kvideoplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.shezz.sa4kvideoplayer.R;


public class SplashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(ContextCompat.getColor(SplashActivity.this,R.color.blue));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);




        new Handler().postDelayed(new Runnable() {
            public void run() {


                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }

        }, 2000);
    }
}