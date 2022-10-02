package com.shezz.sa4kvideoplayer.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mxplayer.paksoft.R;
import com.shezz.sa4kvideoplayer.models.VideoModel;

import java.io.File;
import java.util.List;

public class LockVideoAdapter extends RecyclerView.Adapter<LockVideoAdapter.ViewHolder> {

    private Context mContext;
    private List<VideoModel> videoList;

    public LockVideoAdapter(Context mContext, List<VideoModel> videolList) {
        this.mContext = mContext;
        this.videoList = videolList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lock_video_single_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final VideoModel video = videoList.get(i);
        Glide.with(mContext)
                .load(Uri.fromFile(new File(video.getPath())))
                .into(viewHolder.thumb);

        viewHolder.title.setText(video.getName());
        viewHolder.duration.setText(video.getDuration());

        if(video.getNewtag()){
            viewHolder.foldertag.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title, duration,foldertag;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.video_thumb);
            title = itemView.findViewById(R.id.video_title);
            duration = itemView.findViewById(R.id.video_duration);
            foldertag = itemView.findViewById(R.id.foldertag);
        }
    }

}
