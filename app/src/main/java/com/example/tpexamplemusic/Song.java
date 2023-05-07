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
        String nodot=title.substring(0,title.lastIndexOf('.'));
        String output = nodot.replaceAll("^[\\p{Punct}\\d]+", "");
        String output2=output.replaceFirst(" -","");
        return output2;
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
    public String getFileTypename(){
        String fileformat=title.substring(title.lastIndexOf('.')+1);
        return fileformat.toUpperCase();

    }
}
