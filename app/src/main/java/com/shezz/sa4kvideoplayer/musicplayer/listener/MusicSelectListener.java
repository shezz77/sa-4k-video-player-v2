package com.shezz.sa4kvideoplayer.musicplayer.listener;




import com.shezz.sa4kvideoplayer.musicplayer.model.Music;

import java.util.List;

public interface MusicSelectListener {
    void playQueue(List<Music> musicList);

    void setShuffleMode(boolean mode);
}