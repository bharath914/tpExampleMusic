package com.example.tpexamplemusic;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;
import java.util.List;

public class AlbumView extends AppCompatActivity {
    RecyclerView recyclerView;
ExoPlayer exoPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);
        recyclerView = findViewById(R.id.recycler_view_albumview);
//        albumviewadapter albumviewadapter = new albumviewadapter();
//        recyclerView.setAdapter(albumviewadapter);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(linearLayoutManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            fetchAlbums();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void fetchAlbums() {
        List<Album> albums = new ArrayList<>();
        Uri mediaStoreUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mediaStoreUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,

                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
//                MediaStore.Audio.Media.ALBUM_KEY,


        };

        String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER + "";
        try(Cursor cursor = getContentResolver().query(mediaStoreUri, projection, null, null, sortOrder)){
            {
//            int albumkeyid= cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_KEY);
                int idC = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int albumIdC = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                int albumnameid = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                int albumartistId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST);
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idC);
                    long albumId = cursor.getLong(albumIdC);
                    String albumartist = cursor.getString(albumartistId);
                    String albumname = cursor.getString(albumnameid);
//                long albumkey=cursor.getLong(albumkeyid);
                    Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                    Uri albumart = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
                    Album album = new Album(uri, albumart, albumname, albumartist);
                    albums.add(album);

                }
            }
            cursor.close();
            showAlbums(albums);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void showAlbums(List<Album> albums){
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
            albumviewadapter albumviewadapter=new albumviewadapter(getApplicationContext(),exoPlayer,albums);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(albumviewadapter);


    }
}
