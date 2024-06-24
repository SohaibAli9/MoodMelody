package com.example.moodmelody;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private List<MusicList> list;
    private final Context context;
    private int playing_position = 0;
    private final SongChangeListener songChangeListener;

    public MusicAdapter(List<MusicList> list, Context context) {
        this.list = list;
        this.context = context;
        this.songChangeListener = ((SongChangeListener) context);
    }

    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MyViewHolder holder, int position) {
        MusicList new_list = list.get(holder.getAdapterPosition());

        if (new_list.isPlaying())
        {
            playing_position = holder.getAdapterPosition();
            holder.root_layout.setBackgroundResource((R.drawable.round_back_blue_10));
        }
        else {
            holder.root_layout.setBackgroundResource((R.drawable.round_back_10));
        }

        String generateDuration = String.format(Locale.getDefault(), "%02d:%02d"
                , TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(new_list.getDuration()))
                , TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(new_list.getDuration()))
                , TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(new_list.getDuration()))));

        holder.title.setText(new_list.getTitle());
        holder.artist.setText(new_list.getArtist());
        holder.music_duration.setText(generateDuration);

        holder.root_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.get(playing_position).setPlaying(false);
                new_list.setPlaying(true);
                songChangeListener.onChanged(holder.getAdapterPosition());

                notifyDataSetChanged();
            }
        });
    }

    public void update_list(List<MusicList> list)
    {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout root_layout;
        private final TextView title, artist, music_duration;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            root_layout = itemView.findViewById(R.id.root_layout);
            title = itemView.findViewById(R.id.music_title);
            artist = itemView.findViewById(R.id.music_artist);
            music_duration = itemView.findViewById(R.id.music_duration);
        }
    }
}
