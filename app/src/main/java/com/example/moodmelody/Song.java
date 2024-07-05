package com.example.moodmelody;

public class Song {
    private String title;
    private String artist;
    private int albumArt;

    public Song(String title, String artist, int albumArt) {
        this.title = title;
        this.artist = artist;
        this.albumArt = albumArt;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getAlbumArt() {
        return albumArt;
    }
}
