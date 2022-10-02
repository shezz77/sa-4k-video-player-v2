package com.shezz.sa4kvideoplayer.musicplayer.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.shezz.sa4kvideoplayer.musicplayer.MPPreferences;
import com.shezz.sa4kvideoplayer.musicplayer.helper.MusicLibraryHelper;
import com.shezz.sa4kvideoplayer.musicplayer.model.Album;
import com.shezz.sa4kvideoplayer.musicplayer.model.Artist;
import com.shezz.sa4kvideoplayer.musicplayer.model.Folder;
import com.shezz.sa4kvideoplayer.musicplayer.model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class MainViewModel extends ViewModel {

    public List<Music> songsList = new ArrayList<>();
    public List<Album> albumList = new ArrayList<>();
    public List<Artist> artistList = new ArrayList<>();
    public List<Folder> folderList = new ArrayList<>();

    public MainViewModel(Context context) {
        initSongList(context);
    }

    private void initSongList(Context context) {
        Set<String> excludedFolderList = MPPreferences.getExcludedFolders(context);
        List<Music> musicList = MusicLibraryHelper.fetchMusicLibrary(context);
        HashMap<String, Folder> map = new HashMap<>();

        for (Music music : musicList) {
            Folder folder;
            if (map.containsKey(music.relativePath)) {
                folder = map.get(music.relativePath);
                assert folder != null;
                folder.songsCount += 1;
            } else {
                folder = new Folder(1, music.relativePath);
                folderList.add(folder);
            }
            map.put(music.relativePath, folder);
        }

        for (Music music : musicList) {
            if (!excludedFolderList.contains(music.relativePath))
                songsList.add(music);
        }

    }

    public List<Music> getSongs(boolean reverse) {
        if (reverse)
            Collections.reverse(songsList);

        return songsList;
    }

}

