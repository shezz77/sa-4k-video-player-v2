package com.mxplayer.paksoft;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.util.FileUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mxplayer.paksoft.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class OpenStreamIntentActivity extends AppCompatActivity {
    private ArrayList<HashMap<String, Object>> listmap2 = new ArrayList<>();
    String videoData = "";
    String filesData = "";
    Timer _timer = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_stream_intent);

        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = getIntent();
                        String action = intent.getAction();
                        String type = intent.getType();

                        if (intent != null) {

                            if (Intent.ACTION_VIEW.equals(action)) {
                                Uri file_uri = intent.getData();
                                if (file_uri != null) {

                                    String share = file_uri.toString();
                                    Intent intentt = new Intent();
                                    intentt.putExtra("openPath", share);
                                    intentt.setClass(getApplicationContext(), OnlineStreamActivity.class);
                                    startActivity(intentt);
                                    finish();

                                } else {

                                    loaded_files();


                                }

                            }
                        }


                    }


                });
            }
        };
        _timer.schedule(t, (int) (100));

    }



    private void loaded_files() {

        Intent intent = new Intent();
        intent.setClass(OpenStreamIntentActivity.this, MainActivity.class);
        startActivity(intent);


    }



}