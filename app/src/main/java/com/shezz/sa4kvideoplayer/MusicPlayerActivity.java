package com.shezz.sa4kvideoplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.mxplayer.paksoft.R;
import com.shezz.sa4kvideoplayer.activity.MainActivity;
import com.shezz.sa4kvideoplayer.musicplayer.MPConstants;
import com.shezz.sa4kvideoplayer.musicplayer.MPPreferences;
import com.shezz.sa4kvideoplayer.musicplayer.activities.PlayerDialog;
import com.shezz.sa4kvideoplayer.musicplayer.adapter.MainPagerAdapter;
import com.shezz.sa4kvideoplayer.musicplayer.helper.ThemeHelper;
import com.shezz.sa4kvideoplayer.musicplayer.listener.MusicSelectListener;
import com.shezz.sa4kvideoplayer.musicplayer.model.Music;
import com.shezz.sa4kvideoplayer.musicplayer.player.PlayerBuilder;
import com.shezz.sa4kvideoplayer.musicplayer.player.PlayerListener;
import com.shezz.sa4kvideoplayer.musicplayer.player.PlayerManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;
import java.util.Locale;

import io.alterac.blurkit.BlurLayout;

public class MusicPlayerActivity extends AppCompatActivity
        implements MusicSelectListener, PlayerListener, View.OnClickListener {

    private RelativeLayout playerView;
    private ImageView ivBack,albumArt;
    private TextView songName;
    private TextView songDetails;
    private ImageButton play_pause;
    private LinearProgressIndicator progressIndicator;
    private PlayerDialog playerDialog;

    private PlayerBuilder playerBuilder;
    private PlayerManager playerManager;
    private boolean albumState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //To hide status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setTheme(ThemeHelper.getTheme(MPPreferences.getTheme(getApplicationContext())));
        AppCompatDelegate.setDefaultNightMode(MPPreferences.getThemeMode(getApplicationContext()));
        setContentView(R.layout.activity_music_player);
        MPConstants.musicSelectListener = this;



        if (hasReadStoragePermission(MusicPlayerActivity.this))
            setUpUiElements();
        else
            manageStoragePermission(MusicPlayerActivity.this);

        albumState = MPPreferences.getAlbumRequest(this);
        MPConstants.musicSelectListener = this;

        BlurLayout blurLayout = findViewById(R.id.blurLayout);
        blurLayout.startBlur();
        MaterialCardView playerLayout = findViewById(R.id.player_layout);
        ivBack  = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        progressIndicator = findViewById(R.id.song_progress);
        playerView = findViewById(R.id.player_view);
        songName = findViewById(R.id.song_title);
        songDetails = findViewById(R.id.song_details);
        play_pause = findViewById(R.id.control_play_pause);
        play_pause.setOnClickListener(this);
        playerLayout.setOnClickListener(this);
    }

    private void setPlayerView() {
        if (playerManager != null && playerManager.isPlaying()) {
            playerView.setVisibility(View.VISIBLE);
            onMusicSet(playerManager.getCurrentMusic());
        }
    }

    public void setUpUiElements() {
        playerBuilder = new PlayerBuilder(MusicPlayerActivity.this, this);
        MainPagerAdapter sectionsPagerAdapter = new MainPagerAdapter(
                getSupportFragmentManager(), this);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(3);


    }

    public void manageStoragePermission(Activity context) {
        if (!hasReadStoragePermission(context)) {
            // required a dialog?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Requesting permission")
                        .setMessage("Enable storage permission for accessing the media files.")
                        .setPositiveButton("Accept", (dialog, which) -> askReadStoragePermission(context)).show();
            } else
                askReadStoragePermission(context);
        }
    }

    public boolean hasReadStoragePermission(Activity context) {
        return (
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        );
    }

    public void askReadStoragePermission(Activity context) {
        ActivityCompat.requestPermissions(
                context,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MPConstants.PERMISSION_READ_STORAGE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ThemeHelper.applySettings(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (playerDialog != null)
            playerDialog.dismiss();
    }

    @Override
    public void playQueue(List<Music> musicList) {
        if (musicList.size() > 0) {
            playerManager.setMusicList(musicList);
            setPlayerView();
        }
    }


    @Override
    public void setShuffleMode(boolean mode) {
        playerManager.getPlayerQueue().setShuffle(mode);
    }

    @Override
    public void onPrepared() {
        playerManager = playerBuilder.getPlayerManager();
        setPlayerView();
    }

    @Override
    public void onStateChanged(int state) {
        if (state == State.PLAYING)
            play_pause.setImageResource(R.drawable.ic_controls_pause);
        else
            play_pause.setImageResource(R.drawable.ic_controls_play);
    }

    @Override
    public void onPositionChanged(int position) {
        progressIndicator.setProgress(position);
    }

    @Override
    public void onMusicSet(Music music) {
        songName.setText(music.title);
        songDetails.setText(
                String.format(Locale.getDefault(), "%s • %s",
                        music.artist, music.album));
        playerView.setVisibility(View.VISIBLE);

        if (albumState)
            Glide.with(getApplicationContext())
                    .load(music.albumArt)
                    .centerCrop()
                    .into(albumArt);

        if (playerManager != null && playerManager.isPlaying())
            play_pause.setImageResource(R.drawable.ic_controls_pause);
        else
            play_pause.setImageResource(R.drawable.ic_controls_play);
    }

    @Override
    public void onPlaybackCompleted() {
    }

    @Override
    public void onRelease() {
        playerView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.iv_back) {

            onBackPressed();
        }
        if (id == R.id.control_play_pause)
            playerManager.playPause();

        else if (id == R.id.player_layout)
            setUpPlayerDialog();
    }

    private void setUpPlayerDialog() {
        playerDialog = new PlayerDialog(this, playerManager);
        playerDialog.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MusicPlayerActivity.this, MainActivity.class));
        finish();
        super.onBackPressed();

    }
}