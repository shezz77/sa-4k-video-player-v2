package com.shezz.sa4kvideoplayer.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.shezz.sa4kvideoplayer.AboutActivity;
import com.shezz.sa4kvideoplayer.MusicPlayerActivity;
import com.shezz.sa4kvideoplayer.OnlineStreamActivity;
import com.shezz.sa4kvideoplayer.musicplayer.player.PlayerService;
import com.shezz.sa4kvideoplayer.pinlib.CustomPinActivity;
import com.shezz.sa4kvideoplayer.R;
import com.shezz.sa4kvideoplayer.db.DbHandler;
import com.shezz.sa4kvideoplayer.floatingactionbutton.FloatingActionButton;
import com.shezz.sa4kvideoplayer.floatingactionbutton.FloatingActionsMenu;
import com.shezz.sa4kvideoplayer.fragment.VideoFolders;
import com.shezz.sa4kvideoplayer.fragment.VideosList;
import com.shezz.sa4kvideoplayer.fragment.favorite_video;
import com.shezz.sa4kvideoplayer.models.SingleFile;
import com.shezz.sa4kvideoplayer.utils.ColorChooserDialog;
import com.shezz.sa4kvideoplayer.utils.MyUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.media.audiofx.AudioEffect.EXTRA_AUDIO_SESSION;
import static android.media.audiofx.AudioEffect.EXTRA_CONTENT_TYPE;
import static android.os.Build.VERSION.SDK_INT;

import static com.google.android.exoplayer2.C.CONTENT_TYPE_MUSIC;

import butterknife.ButterKnife;


public class    MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_REQUEST_CODE = 10;
    private static final int REQUEST_EQ = 0;
    Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private int theme;
    Class fragmentClass = null;
    Fragment fragment;
    private Context context;
    private SharedPreferences.Editor editor;
    WifiManager wifi;
    FloatingActionButton add_url,donwload_list,nowdownloading;
    FloatingActionsMenu fabbutton;
    public  static   boolean activityResumed = false;
    public  static   boolean deleteFiles = false;
    public  static   int detailsID = -1;
    public  static   boolean selectionMode = false;
    public  static  boolean serviceConnected = false;
    public  static   boolean showDetails = false;
    public  static String TAG = "MainActvity";
    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = MainActivity.this;
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.dark_gray4));
        theme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sharedPreferences = this.getSharedPreferences(MyUtils.pref_key, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




//        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.menuicon));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if(sharedPreferences.getInt("THEME", 0)!=111) {
            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.dark_gray4));
            toolbar.setNavigationIcon(R.drawable.baseline_menu_black_24dp);
        }else{
            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.toolbar_style));
            toolbar.setNavigationIcon(R.drawable.baseline_menu_white_24dp);

        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        add_url = (FloatingActionButton) findViewById(R.id.network_stream);
        nowdownloading = (FloatingActionButton) findViewById(R.id.musicplayer);

        fabbutton = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        add_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabbutton.collapse();
                showDialogPrompt();
            }
        });

        nowdownloading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MusicPlayerActivity.class));
                finish();
            }
        });
       permissionHandle();
    }
    public void theme() {
        sharedPreferences = getSharedPreferences(MyUtils.pref_key, Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("THEME", 0);

        MyUtils.settingTheme(context, theme);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.local:
                    fabbutton.setVisibility(View.VISIBLE);
                    toolbar.setTitle("Folders");
                    fragment = new VideoFolders();
                    loadFragment(fragment);
                    return true;

            }

            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showDetailFragment(Intent intent) {

        if (intent.getExtras() != null && (intent.getFlags() & 1048576) == 0) {
            if (!(intent.hasExtra("occupied") ? intent.getBooleanExtra("occupied", false) : false)) {
                int intExtra;
                if (intent.hasExtra("showDetails")) {
                    if (intent.getBooleanExtra("showDetails", false) && intent.hasExtra("fileID")) {
                        intExtra = intent.getIntExtra("fileID", -1);
                        int intExtra2 = intent.getIntExtra("status", -1);
                        boolean booleanExtra = intent.getBooleanExtra("singleThread", false);
                        String stringExtra = intent.getStringExtra("fileName");
                        if (intExtra != -1) {
                            String str = TAG;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("File Id : ");
                            stringBuilder.append(intExtra);
                            Log.i(str, stringBuilder.toString());
                            str = TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Status : ");
                            stringBuilder.append(intExtra2);
                            Log.i(str, stringBuilder.toString());
                            str = TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("singleThread : ");
                            stringBuilder.append(booleanExtra);
                            Log.i(str, stringBuilder.toString());
                            FragmentManager supportFragmentManager = getSupportFragmentManager();
                            supportFragmentManager.popBackStack(null, 1);
                            FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                            beginTransaction.commit();
                        }
                    }
                    intent.putExtra("occupied", true);
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                MainActivity.this.finish();
                System.exit(0);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 1500);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//
//        if (sharedPreferences.getBoolean("is_pin_enable", false)) {
//menu.findItem(R.id.s).setVisible(true);
//
//        }else{
//            menu.findItem(R.id.lockmedia).setVisible(false);
//
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.lockmedia) {
//            startActivity(new Intent(MainActivity.this, lockedmedia.class));
//            finish();
//
//            Intent  intent = new Intent(MainActivity.this, CustomPinActivity.class);
//            intent.putExtra("type", 4);
//
//            startActivityForResult(intent, 123);
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void stopMusicPlayer(){
        stopService(new Intent(this, PlayerService.class));
    }

    public void onBackPressed1() {
        toolbar.setNavigationIcon(R.drawable.baseline_menu_black_24dp);
        startActivity(new Intent(MainActivity.this, MainActivity.class));
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
            onBackPressed1();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        if (id == R.id.nav_music) {
            startActivity(new Intent(MainActivity.this, MusicPlayerActivity.class));
            finish();

        }
      else  if (id == R.id.videofolder) {
            toolbar.setTitle("Video Folder");
            fragment = new VideoFolders();
            loadFragment(fragment);

        }
        else if (id == R.id.favorite_video) {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if(sharedPreferences.getInt("THEME", 0)!=111) {
                toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp);
            }else{
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            }
            toolbar.setTitle("Favorite Video");
            fragment = new favorite_video();
            loadFragment(fragment);


        }
        else if (id == R.id.nav_setting) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            finish();

        }
        else if (id == R.id.nav_stream) {
           showDialogPrompt();

        }
        else if (id == R.id.equalizer) {
            openEqualizer();

        }
        else if (id == R.id.apptheme) {
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            ColorChooserDialog dialog1 = new ColorChooserDialog();
            dialog1.setOnItemChoose(new ColorChooserDialog.OnItemChoose() {
                @Override
                public void onClick(int position) {
                    setThemeFragment(position);
                }

                @Override
                public void onSaveChange() {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
            dialog1.show(fragmentManager1, "fragment_color_chooser");

        }
        else if (id == R.id.nav_private) {
            if (sharedPreferences.getBoolean("is_pin_enable", false)) {
                Intent  intent = new Intent(this, CustomPinActivity.class);
                intent.putExtra("type", 4);

                startActivityForResult(intent, 123);
            }else{
                Toast.makeText(getApplicationContext(), " First Enable Private Folder From Settings", Toast.LENGTH_LONG).show();
            }

        }
        else if (id == R.id.rateus) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        }
        else if (id == R.id.aboutus) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            finish();
        }
        else if (id == R.id.privacypolicy) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://paksofttech.com/pricavy_policy/privacy-policy.html")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://paksofttech.com/pricavy_policy/privacy-policy.html")));
            }
        }
        else if (id == R.id.shareapp) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                String shareMessage= "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share"));
            } catch(Exception e) {
                //e.toString();
            }

        }
        else if (id == R.id.screen_mirroring) {
            enablingWiFiDisplay();


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void enablingWiFiDisplay() {
        if (wifi.isWifiEnabled()) {
            wifidisplay();
            return;
        }
        wifi.setWifiEnabled(true);
        wifidisplay();
    }

    public void wifidisplay() {
        try {
            startActivity(new Intent("android.settings.WIFI_DISPLAY_SETTINGS"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            try {
                startActivity(getPackageManager().getLaunchIntentForPackage("com.samsung.wfd.LAUNCH_WFD_PICKER_DLG"));
            } catch (Exception e2) {
                try {
                    startActivity(new Intent("android.settings.CAST_SETTINGS"));
                } catch (Exception e3) {
                    Toast.makeText(getApplicationContext(), "Device not supported", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    public final void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            Intent  intent = new Intent(MainActivity.this, lockedmedia.class);


            startActivity(intent);
        }
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Bundle extras = getIntent().getExtras();

                    if (getIntent().hasExtra("destination") && extras != null && extras.getString("destination").equals("locked")) {
                        fragment = new VideosList();

                        Bundle args = new Bundle();
                        args.putString("folder_path", extras.getString("folder_path"));
                        args.putString("destination", "locked");
                        args.putSerializable("newvideoid", extras.getSerializable("newvideoid"));
                        fragment.setArguments(args);
                        loadFragment(fragment);
                    } else {
                        fragment = new VideoFolders();
                        loadFragment(fragment);
                    }
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void permissionHandle() {

        if (SDK_INT >= 23) {
            if (checkPermission()) {


                    Bundle extras = getIntent().getExtras();

                    if (getIntent().hasExtra("destination") && extras!= null &&  extras.getString("destination").equals("locked")) {
                        fragment = new VideosList();

                        Bundle args = new Bundle();
                        args.putString("folder_path", extras.getString("folder_path"));
                        args.putString("destination", "locked");
                        args.putSerializable("newvideoid", extras.getSerializable("newvideoid"));
                        fragment.setArguments(args);
                        loadFragment(fragment);
                    } else {
                        fragment = new VideoFolders();
                        loadFragment(fragment);
                    }

            }else {
                requestPermission();
            }
        }else{
            fragment = new VideoFolders();
            loadFragment(fragment);
        }
    }

    private boolean checkPermission() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//
//
//
//        } else {
//
//        }

        int result = ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//
//
//            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
//            dialog.setMessage("Video player  needs to allow access and manage external storage permission for  use features like view video,hide video,locked video,cut video,delete,rename video etc. This is mandatory permission is required by Android for our app. This permission required only theses features, but we are not collecting any data in our personal use and no store in any server.");
//            dialog.setTitle("Why app needs All File Access Permission?");
//            dialog.setCancelable(false);
//            dialog.setPositiveButton("Ok",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog,
//                                            int which) {
//                            dialog.dismiss();
//                            //   Log.i("requestPermission","devicelocation");
//                            try {
//                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                                intent.addCategory("android.intent.category.DEFAULT");
//                                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
//                                startActivityForResult(intent, 2296);
//                            } catch (Exception e) {
//                                Intent intent = new Intent();
//                                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                                startActivityForResult(intent, 2296);
//                            }
//
//
//
//                        }
//                    });
//            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            });
//          dialog.create();
//            dialog.show();
//
//        } else {
//            //below android 11
//
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted");
                    Intent intent = getIntent();


                    Bundle extras = getIntent().getExtras();

                    if (getIntent().hasExtra("destination") && extras != null && extras.getString("destination").equals("locked")) {
                        fragment = new VideosList();

                        Bundle args = new Bundle();
                        args.putString("folder_path", extras.getString("folder_path"));
                        args.putString("destination", "locked");
                        args.putSerializable("newvideoid", extras.getSerializable("newvideoid"));
                        fragment.setArguments(args);
                        loadFragment(fragment);
                    } else {
                        fragment = new VideoFolders();
                        loadFragment(fragment);
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Please allow storage permission in App Settings.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void onResume() {
        super.onResume();
        this.activityResumed = true;
        String str;
        StringBuilder stringBuilder;
        try {
            str = "MainActivity";
            stringBuilder = new StringBuilder();
            stringBuilder.append("onResume showDetails  : ");
            stringBuilder.append(this.showDetails);
            stringBuilder.append(" detailsID : ");
            stringBuilder.append(this.detailsID);
            Log.i(str, stringBuilder.toString());
            if (this.showDetails && this.detailsID > 0) {
                DbHandler.openDB();
                SingleFile fileDetail = DbHandler.getFileDetail(this.detailsID);
                DbHandler.closeDB();
                if (!(fileDetail == null || fileDetail.status == 4)) {
                    FragmentManager supportFragmentManager = getSupportFragmentManager();
                    FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                    supportFragmentManager.popBackStack(null, 1);
                    beginTransaction.commit();

                }
                this.showDetails = false;
                this.detailsID = -1;
            }
        } catch (Exception e) {
            str = "MainActivity";
            stringBuilder = new StringBuilder();
            stringBuilder.append("onResume Exception : ");
            stringBuilder.append(e.getMessage());
            Log.i(str, stringBuilder.toString());
        }
    }

    public void onPause() {
        super.onPause();
        this.activityResumed = false;
    }

    public void onDestroy() {
        super.onDestroy();


    }

    public void onStart() {
        super.onStart();

    }
    public void onStop() {
        super.onStop();

    }



    private void showDialogPrompt() {
        // get dialog_prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_paste_link, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder( this);
        // set dialog_prompts.xml to dialog
        mBuilder.setView(promptsView);
        final EditText userInputURL = (EditText) promptsView
                .findViewById(R.id.editTextDialogUrlInput);
        // set dialog message here
        mBuilder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                boolean isURL = Patterns.WEB_URL.matcher(userInputURL.getText().toString().trim()).matches();
                                if (isURL) {
                                    Intent mIntent = OnlineStreamActivity.getStartIntent(MainActivity.this, userInputURL.getText().toString().trim());
                                    startActivity(mIntent);
                                } else {
                                    Toast.makeText(MainActivity.this, getString(R.string.error_drm_not_supported), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).create().show();
    }

    public void setThemeFragment(int theme) {
        switch (theme) {
            case 1:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 1).apply();
                break;
            case 2:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 2).apply();
                break;
            case 3:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 3).apply();
                break;
            case 4:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 4).apply();
                break;
            case 5:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 5).apply();
                break;
            case 6:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 6).apply();
                break;
            case 7:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 7).apply();
                break;
            case 8:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 8).apply();
                break;
            case 9:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 9).apply();
                break;
            case 10:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 10).apply();
                break;
        }
    }

    public void openEqualizer() {

        Intent eqIntent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
        eqIntent.putExtra(EXTRA_CONTENT_TYPE, CONTENT_TYPE_MUSIC);
        eqIntent.putExtra(EXTRA_AUDIO_SESSION, CONTENT_TYPE_MUSIC);

        if ((eqIntent.resolveActivity(getPackageManager()) != null)) {
            startActivityForResult(eqIntent, REQUEST_EQ);
            finish();
        } else {
            Toast.makeText(MainActivity.this, "No Equalizer found!",
                    Toast.LENGTH_LONG).show();
        }
    }

}