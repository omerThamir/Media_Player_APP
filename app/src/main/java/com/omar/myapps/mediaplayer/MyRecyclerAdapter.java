package com.omar.myapps.mediaplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    List<Song> list;
    Context mContext;

    public MyRecyclerAdapter(Context context, List<Song> data) {
        this.list = data;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_view, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerAdapter.ViewHolder holder, int position) {
        final Song song = list.get(position);
        holder.nameTextView.setText(song.getName());
        holder.autherTextView.setText(song.getAuther());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, autherTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.songName);
            autherTextView = itemView.findViewById(R.id.Auther);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion = getLayoutPosition();
                    if (mContext instanceof MainActivity) {

                        ((MainActivity) mContext).startPlayingActivity(
                                list.get(postion).filepath ,
                                list.get(postion).name, postion);

                        // .playSound(Uri.parse(list.get(postion).filepath));
                        Song.currentSongIndex = postion;
                    }
                }
            });


        }
    }
}
