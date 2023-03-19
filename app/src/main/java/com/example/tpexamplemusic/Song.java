package com.example.tpexamplemusic;

import android.net.Uri;

public class Song {
    String title;
    Uri uri;
    Uri artwork;

    int duration;
    int size;
    String artist;
    public Song(String title, Uri uri, Uri artwork, int duration, int size,String artist) {
        this.title = title;
        this.uri = uri;
        this.artwork = artwork;
        this.duration = duration;
        this.size = size;
        this.artist=artist;
    }

    public String getTitle() {
        return title;
    }

    public Uri getUri() {
        return uri;
    }

    public Uri getArtwork() {
        return artwork;
    }

    public int getDuration() {
        return duration;
    }

    public int getFiletype() {
        return size;
    }
    public String getArtist(){return artist;}
}
