package com.example.tpexamplemusic;

import android.net.Uri;

public class Album {
    Uri uri;
    Uri artworkuri;
    String albumname;
    String albumartist;

    public Album(Uri uri, Uri artworkuri, String albumname,String albumartist) {
        this.uri = uri;
        this.artworkuri = artworkuri;
        this.albumname = albumname;
        this.albumartist=albumartist;

    }

    public String getAlbumartist() {
        return albumartist;
    }

    public Uri getUri() {
        return uri;
    }

    public Uri getArtworkuri() {
        return artworkuri;
    }

    public String getAlbumname() {
        return albumname;
    }
}
