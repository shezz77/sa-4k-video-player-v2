package com.mxplayer.paksoft.musicplayer.listener;




import com.mxplayer.paksoft.musicplayer.model.Music;

import java.util.List;

public interface MusicSelectListener {
    void playQueue(List<Music> musicList);

    void setShuffleMode(boolean mode);
}