package com.example.moodmelody;

import android.net.Uri;

public class MusicList {
    private String title, artist, duration;
    private boolean playing;
    private Uri music_file;

    public MusicList(String title, String artist, String duration, boolean playing, Uri music_file) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.playing = playing;
        this.music_file = music_file;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isPlaying() {
        return playing;
    }

    public Uri getMusic_file() {
        return music_file;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void setMusic_file(Uri music_file) {
        this.music_file = music_file;
    }
}
