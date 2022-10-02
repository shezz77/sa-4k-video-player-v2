package com.mxplayer.paksoft;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mxplayer.paksoft.musicplayer.adapter.SongsAdapter;
import com.mxplayer.paksoft.musicplayer.helper.ListHelper;
import com.mxplayer.paksoft.musicplayer.listener.MusicSelectListener;
import com.mxplayer.paksoft.musicplayer.listener.PlayListListener;
import com.mxplayer.paksoft.musicplayer.model.Music;
import com.mxplayer.paksoft.musicplayer.viewmodel.MainViewModel;
import com.mxplayer.paksoft.musicplayer.viewmodel.MainViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;
import com.jgabrielfreitas.core.BlurImageView;

import java.util.ArrayList;
import java.util.List;

import io.alterac.blurkit.BlurLayout;

public class SongsFragment extends Fragment implements SearchView.OnQueryTextListener, PlayListListener {

    private static MusicSelectListener listener;
    private final List<Music> musicList = new ArrayList<>();
    private MainViewModel viewModel;
    private SongsAdapter songsAdapter;
    private List<Music> unChangedList = new ArrayList<>();

    private MaterialToolbar toolbar;
    private SearchView searchView;
    BlurLayout blurLayout;

    public SongsFragment() {
    }

    public static SongsFragment newInstance(MusicSelectListener selectListener) {
        SongsFragment.listener = selectListener;
        return new SongsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(),
                new MainViewModelFactory(requireActivity())).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        unChangedList = viewModel.getSongs(false);
        musicList.clear();
        musicList.addAll(unChangedList);

        RecyclerView recyclerView = view.findViewById(R.id.songs_layout);
        BlurImageView blurImageView = (BlurImageView) view.findViewById(R.id.blurImage1);
        blurImageView.setBlur(20);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songsAdapter = new SongsAdapter(listener, this, musicList);
        recyclerView.setAdapter(songsAdapter);


        return view;
    }


    private void setUpSearchView() {
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        updateAdapter(ListHelper.searchMusicByName(unChangedList, query.toLowerCase()));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        updateAdapter(ListHelper.searchMusicByName(unChangedList, newText.toLowerCase()));
        return true;
    }

    private void updateAdapter(List<Music> list) {
        musicList.clear();
        musicList.addAll(list);
        songsAdapter.notifyDataSetChanged();
    }

    @Override
    public void option(Context context, Music music) {
    }
}