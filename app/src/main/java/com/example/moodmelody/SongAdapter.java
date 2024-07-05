package com.example.moodmelody;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> songs;

    public SongAdapter(List<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, null);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.albumArt.setImageResource(song.getAlbumArt());
        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());
    }

    public void update_list(List<Song> list)
    {
        Log.d("TAG", "update_list: inside new method");
        this.songs = list;
        notifyDataSetChanged();
        Log.d("TAG", "update_list: changed");
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        private final ImageView albumArt;
        private final TextView songTitle, songArtist;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.album_art);
            songTitle = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
        }
    }
}
