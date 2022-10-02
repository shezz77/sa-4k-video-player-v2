package com.mxplayer.paksoft.activity;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.mxplayer.paksoft.R;
import com.mxplayer.paksoft.adapter.TabAdapter;
import com.mxplayer.paksoft.fragment.LockVideosList;
import com.mxplayer.paksoft.fragment.LockFolders;
import com.mxplayer.paksoft.utils.MyUtils;
import com.google.android.material.tabs.TabLayout;

public class lockedmedia extends AppCompatActivity {
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Toolbar toolbar;
    private int theme;
    private Context context;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context=lockedmedia.this;
        getWindow().setStatusBarColor(ContextCompat.getColor(lockedmedia.this,R.color.dark_gray4));
        theme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockedmedia);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(sharedPreferences.getInt("THEME", 0)!=111) {
            getWindow().setStatusBarColor(ContextCompat.getColor(lockedmedia.this,R.color.dark_gray4));
        }else{
            getWindow().setStatusBarColor(ContextCompat.getColor(lockedmedia.this,R.color.toolbar_style));

        }

        toolbar.setTitle("Private File");
        sharedPreferences = getSharedPreferences(MyUtils.pref_key, Context.MODE_PRIVATE);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new LockFolders(), "Folder");
        adapter.addFragment(new LockVideosList(), "Video");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    public void theme() {
        sharedPreferences = getSharedPreferences(MyUtils.pref_key, Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("THEME", 0);

        MyUtils.settingTheme(context, theme);
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(lockedmedia.this, MainActivity.class));
        finish();
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
