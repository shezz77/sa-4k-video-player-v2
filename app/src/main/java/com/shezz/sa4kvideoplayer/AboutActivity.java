package com.shezz.sa4kvideoplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.shezz.sa4kvideoplayer.R;
import com.shezz.sa4kvideoplayer.activity.MainActivity;

public class AboutActivity extends AppCompatActivity {

    ImageView btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(ContextCompat.getColor(AboutActivity.this, R.color.dark_gray4));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        btnback = findViewById(R.id.backBtn);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AboutActivity.this, MainActivity.class));
        finish();
        super.onBackPressed();

    }
}