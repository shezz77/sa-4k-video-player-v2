package com.shezz.sa4kvideoplayer.musicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shezz.sa4kvideoplayer.R;
import com.shezz.sa4kvideoplayer.musicplayer.MPPreferences;
import com.shezz.sa4kvideoplayer.musicplayer.helper.MusicLibraryHelper;
import com.shezz.sa4kvideoplayer.musicplayer.listener.MusicSelectListener;
import com.shezz.sa4kvideoplayer.musicplayer.listener.PlayListListener;
import com.shezz.sa4kvideoplayer.musicplayer.model.Music;

import java.util.List;
import java.util.Locale;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> {

    private final List<Music> musicList;
    private final PlayListListener playListListener;
    public MusicSelectListener listener;

    public SongsAdapter(MusicSelectListener listener, PlayListListener playListListener, List<Music> musics) {
        this.listener = listener;
        this.musicList = musics;
        this.playListListener = playListListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.songName.setText(musicList.get(position).title);
        holder.albumName.setText(
                String.format(Locale.getDefault(), "%s • %s",
                        musicList.get(position).artist,
                        musicList.get(position).album)
        );


        holder.songHistory.setText(MusicLibraryHelper.formatDuration(musicList.get(position).duration));

        if (holder.state)
            Glide.with(holder.albumArt.getContext())
                    .load(musicList.get(position).albumArt)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.bg_default_album_art)
                    .into(holder.albumArt);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView songName;
        private final TextView albumName;
        private final TextView songHistory;
        private final ImageView albumArt;
        private final boolean state;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            state = MPPreferences.getAlbumRequest(itemView.getContext());
            albumArt = itemView.findViewById(R.id.album_art);
            songHistory = itemView.findViewById(R.id.song_history);
            songName = itemView.findViewById(R.id.song_name);
            albumName = itemView.findViewById(R.id.song_album);

            itemView.findViewById(R.id.root_layout).setOnClickListener(v -> {
                listener.setShuffleMode(false);
                listener.playQueue(musicList.subList(getAdapterPosition(), musicList.size()));
            });

            itemView.findViewById(R.id.root_layout).setOnLongClickListener(v -> {
                playListListener.option(itemView.getContext(), musicList.get(getAdapterPosition()));
                return true;
            });
        }
    }
}

