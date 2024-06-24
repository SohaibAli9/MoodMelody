package com.example.moodmelody;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class HomeScreenMusicPlayer extends AppCompatActivity implements SongChangeListener{

    private final List<MusicList> music_lists = new ArrayList<>();
    private RecyclerView music_recycler_view;
    private MediaPlayer mediaPlayer;
    private TextView start_time, end_time;
    private boolean is_playing = false;
    private SeekBar player_seek_bar;
    private ImageView play_pause_img;
    private Timer timer;
    private int current_song_list_position = 0;
    private MusicAdapter musicAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decodeView = getWindow().getDecorView();
        int options = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decodeView.setSystemUiVisibility(options);
        setContentView(R.layout.activity_home_screen_music_player);

        final LinearLayout search_btn = findViewById(R.id.search_btn);
        final LinearLayout menu_btn = findViewById(R.id.menu_btn);
        music_recycler_view = findViewById(R.id.music_recycler_view);
        final CardView play_pause_card = findViewById(R.id.play_pause_card);
        play_pause_img = findViewById(R.id.play_pause_img);
        final ImageView next_btn = findViewById(R.id.next_btn);
        final ImageView prev_btn = findViewById(R.id.previous_btn);

        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        player_seek_bar = findViewById(R.id.player_seek_bar);

        music_recycler_view.setHasFixedSize(true);
        music_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        mediaPlayer = new MediaPlayer();

        Toast.makeText(this, "Going to ask for permission", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Already Granted", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "onCreate: GOING TO GET MUSIC FILES");
            get_music_files();
        }
        else {
            Toast.makeText(this, "Not Granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 1);
            Toast.makeText(this, "Line Ran", Toast.LENGTH_SHORT).show();
        }

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int next_song_list_position = current_song_list_position + 1;
                if (next_song_list_position >= music_lists.size()) {
                    next_song_list_position = 0;
                }

                music_lists.get(current_song_list_position).setPlaying(false);
                music_lists.get(next_song_list_position).setPlaying(true);

                musicAdapter.update_list(music_lists);
                music_recycler_view.scrollToPosition(next_song_list_position);
                onChanged(next_song_list_position);
            }
        });

        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int prev_song_list_position = current_song_list_position - 1;
                if (prev_song_list_position < 0) {
                    prev_song_list_position = music_lists.size() - 1;
                }

                music_lists.get(current_song_list_position).setPlaying(false);
                music_lists.get(prev_song_list_position).setPlaying(true);

                musicAdapter.update_list(music_lists);
                music_recycler_view.scrollToPosition(prev_song_list_position);
                onChanged(prev_song_list_position);
            }
        });

        play_pause_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_playing) {
                    is_playing = false;
                    mediaPlayer.pause();
                    play_pause_img.setImageResource(R.drawable.play_icon);
                } else {
                    is_playing = true;
                    mediaPlayer.start();
                    play_pause_img.setImageResource(R.drawable.pause_icon);
                }
            }
        });

        player_seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    if (is_playing) {
                        mediaPlayer.seekTo(i);
                    } else {
                        mediaPlayer.seekTo(0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @SuppressLint("Range")
    private void get_music_files(){
        Log.d("TAG", "get_music_files: INSIDE");
        Toast.makeText(this, "Loading Music Files", Toast.LENGTH_SHORT).show();
        Log.d("TAG", "get_music_files: INSIDE GET MUSIC FILES");
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, MediaStore.Audio.Media.DATA + " LIKE?", new String[]{"%.mp3%"}, null);

        if (cursor == null) {
            Toast.makeText(this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
        } else if (!cursor.moveToNext()) {
            Toast.makeText(this, "No Music Found", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Found Music", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "get_music_files: FOUND MUSIC");

            cursor.moveToFirst();
            final String get_music_file_name_ = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            final String get_artist_name_ = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            long cursor_id_ = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            Uri music_file_uri_ = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor_id_);
            String get_duration_ = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
            final MusicList music_list_ = new MusicList(get_music_file_name_, get_artist_name_, get_duration_, false, music_file_uri_);
            music_lists.add(music_list_);

            while (cursor.moveToNext())
            {
                Log.d("TAG", "get_music_files: INSIDE WHILE");
                final String get_music_file_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                final String get_artist_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long cursor_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                Log.d("TAG", "get_music_files: " + get_music_file_name);

                Uri music_file_uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor_id);
                String get_duration = "00:00";

                get_duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));

                final MusicList music_list = new MusicList(get_music_file_name, get_artist_name, get_duration, false, music_file_uri);
                music_lists.add(music_list);
            }

            musicAdapter = new MusicAdapter(music_lists, HomeScreenMusicPlayer.this);
            music_recycler_view.setAdapter(musicAdapter);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        Toast.makeText(this, "Inside Callback", Toast.LENGTH_SHORT).show();
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "onRequestPermissionsResult: Going to get music files");
            get_music_files();
        }
        else {
            Toast.makeText(this, "Permission Declined by the User", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            View decodeView = getWindow().getDecorView();
            int options = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decodeView.setSystemUiVisibility(options);
        }
    }

    @Override
    public void onChanged(int position) {

        current_song_list_position = position;
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            mediaPlayer.reset();
        }

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.setDataSource(HomeScreenMusicPlayer.this, music_lists.get(position).getMusic_file());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    Log.d("TAG", "run: Cannot PLAY SONG");
                    Toast.makeText(HomeScreenMusicPlayer.this, "Cannot play song", Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        }).start();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                final int get_total_duration = mediaPlayer.getDuration();
                String generateDuration = String.format(Locale.getDefault(), "%02d:%02d"
                        , TimeUnit.MILLISECONDS.toMinutes(get_total_duration)
                        , TimeUnit.MILLISECONDS.toSeconds(get_total_duration)
                        , TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(get_total_duration)));
                end_time.setText(generateDuration);
                is_playing = true;

                mediaPlayer.start();

                player_seek_bar.setMax(get_total_duration);
                play_pause_img.setImageResource(R.drawable.pause_icon);
            }
        });

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final int get_current_duration = mediaPlayer.getCurrentPosition();

                        String generateDuration = String.format(Locale.getDefault(), "%02d:%02d"
                                , TimeUnit.MILLISECONDS.toMinutes(get_current_duration)
                                , TimeUnit.MILLISECONDS.toSeconds(get_current_duration)
                                , TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(get_current_duration)));

                        player_seek_bar.setProgress(get_current_duration);
                        start_time.setText(generateDuration);
                    }
                });
            }
        }, 1000, 1000);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();

                timer.purge();
                timer.cancel();

                is_playing = false;
                play_pause_img.setImageResource(R.drawable.play_icon);
                player_seek_bar.setProgress(0);
            }
        });
    }
}