package com.example.tpexamplemusic;

import  androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;

import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarMenu;
import com.google.common.cache.Cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private GestureDetectorCompat gestureDetectorCompat;
    RecyclerView recyclerView;
    SongAdapter songAdapter;
    List<Song> allSongs=new ArrayList<>();
    ExoPlayer exoPlayer;
    // gestures
    SwipeListener swipeListener;
    ActivityResultLauncher<String> storagepermissionLauncher;
    final  String permission= Manifest.permission.READ_EXTERNAL_STORAGE;

    //the controller in main page
    TextView nowplaying,homeskippreviousbuttton,playbuttonhome,homeskipnextbutton;
    ImageView Currentplayingimage;
    //the buttons and layouts in playingview.xml
    TextView playingviewbackbutton,currentplayingsongplayingview,currentplayingsongunderimageview,togglerepeat,songnavigationprevious,songnavigationnext,songnavigationplay,shuffleswitch;
    TextView progresstime,totaltime;
    TextView playingviewfiletype,bitrate,samplingrate;
    SeekBar seekBar;
    ImageView imageviewinplayingview;
    int repeatmode=1;
    int shufflemode=1;
    //shuffle mode 1= shuffle off 2=on
    ConstraintLayout homewrapper,artworkwrapper,seekbarwrapper,controlwrapper,playerviewwrapper,mainactivity,SearchViewxml,MainactivityXml;


    //repeat all=1, repeat one=2,shuffle all=3
    // is the act.bound
    boolean isBound=false;
//    TextView searchbutton; //search button
    // in search view xml
    SearchView searchView;
    TextView cancelbutton;

    RequestOptions requestOptions=new RequestOptions();

    @SuppressLint("InlinedApi")
    private static final int UI_OPTIONS = View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    private void hideSystemUI() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        getWindow().getDecorView().setSystemUiVisibility(UI_OPTIONS);
    }

    private NotificationManagerCompat mNotificationManagerCompat;

    @Override
        protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        hideSystemUI();
mNotificationManagerCompat=NotificationManagerCompat.from(this);




        recyclerView=findViewById(R.id.recycler_view);
        storagepermissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(),granted->{
            if(granted){
                fetchSongs();
            }
            else{
                userResponses();

            }
        });
//        storagepermissionLauncher.launch(permission);
//        exoPlayer=new ExoPlayer.Builder(this).build();

        //in main xml ids
        nowplaying=findViewById(R.id.NowPlaying);
        homeskippreviousbuttton=findViewById(R.id.homeskippreviousbutton);
        homeskipnextbutton=findViewById(R.id.homeskipnextbutton);
        playbuttonhome=findViewById(R.id.playbuttonhome);
        Currentplayingimage=findViewById(R.id.currentplayingimage);

        //in playingview xml ids
        seekBar=findViewById(R.id.seekbarplayingview);
        MainactivityXml=findViewById(R.id.mainactivity);
        imageviewinplayingview=findViewById(R.id.imageviewinplayingview);
//        playingviewbackbutton=findViewById(R.id.playingviewbackbutton);
//        currentplayingsongplayingview=findViewById(R.id.currentplayingsongplayingview);
        currentplayingsongunderimageview=findViewById(R.id.currenplayingsongunderimageview);
        togglerepeat=findViewById(R.id.repeattoggleswitch);
        songnavigationprevious=findViewById(R.id.songnavigationprevious);
        songnavigationnext=findViewById(R.id.songnavigationnext);
        songnavigationplay=findViewById(R.id.songnavigationplay);
        shuffleswitch=findViewById(R.id.shuffleswitch);
        totaltime=findViewById(R.id.totaltime);
        progresstime=findViewById(R.id.progresstime);
        currentplayingsongunderimageview.setSelected(true);
        mainactivity=findViewById(R.id.audiofilelist);
        //layouts
        homewrapper=findViewById(R.id.audioplaying);
        playerviewwrapper=findViewById(R.id.playingview);

//        searchbutton=findViewById(R.id.searchViewNavigator);
        searchView=findViewById(R.id.SearchViewSongs);
        searchView.clearFocus();
//        cancelbutton=findViewById(R.id.cancelSearchView);
//        SearchViewxml=findViewById(R.id.searchviewxml);
        playingviewfiletype=findViewById(R.id.playingviewfiletype);
        samplingrate=findViewById(R.id.samplingrate);
    //bind the player service
        doBindService();
        swipeListener=new SwipeListener(imageviewinplayingview);

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {

            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            filterList(newText);
            return true;
        }

    });
    }




    public void sendNotification(View view){

    }



    private void doBindService() {
        Intent playerServiceintent=new Intent(this,PlayerService.class);
        bindService(playerServiceintent,playerServiceConnection, Context.BIND_AUTO_CREATE);
        isBound=true;
    }

    ServiceConnection playerServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // get the service instance
            PlayerService.ServiceBinder binder= (PlayerService.ServiceBinder) service;
            exoPlayer=binder.getPlayerService().player;
            isBound=true;
            //ready to show the songs
            storagepermissionLauncher.launch(permission);
            //call player control method
            playerControls();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
          //changes made
            stopService(new Intent(getApplicationContext(),PlayerService.class));
                onDestroy();

            if (exoPlayer.isPlaying()){
                exoPlayer.stop();
                exoPlayer.release();
            }
            else{
                exoPlayer.release();
            }


        }
    };



    private void playerControls() {

        songnavigationplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exoPlayer.isPlaying()){
                    exoPlayer.pause();
                    currentplayingsongunderimageview.setSelected(true);
                    playbuttonhome.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_play_arrow_24,0,0,0);
                    songnavigationplay.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_play_circle_outline_24,0,0,0);

                }
                else  {
                    exoPlayer.play();
                    currentplayingsongunderimageview.setSelected(true);

                    playbuttonhome.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_pause_24,0,0,0);
                    songnavigationplay.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.pauselarge,0,0,0);

                }
            }
        });
        songnavigationnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentplayingsongunderimageview.setSelected(true);

                exoPlayer.seekToNext();

            }
        });
        songnavigationprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentplayingsongunderimageview.setSelected(true);

                    exoPlayer.seekToPrevious();


            }
        });
//        playingviewbackbutton.setOnClickListener(view ->ExittingPlayingView());
        homewrapper.setOnClickListener(view ->showplayerview());
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
//
                currentplayingsongunderimageview.setText(mediaItem.mediaMetadata.title);
//
                assert mediaItem.mediaMetadata.title != null;

                nowplaying.setText(mediaItem.mediaMetadata.title);
                nowplaying.setSelected(true);
                currentplayingsongunderimageview.setSelected(true);
                progresstime.setText(getthetime((int) exoPlayer.getCurrentPosition()));
                seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                seekBar.setMax((int) exoPlayer.getDuration());
                totaltime.setText(getthetime((int) exoPlayer.getDuration()));
                playbuttonhome.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_pause_24,0,0,0);
                songnavigationplay.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.pauselarge,0,0,0);
//                playingviewfiletype.setText()
                CurrentAlbumArtwork();
                updateCurrentsongposition();
                if(!exoPlayer.isPlaying()){
                    exoPlayer.play();
                }

            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if(playbackState==ExoPlayer.STATE_READY){
//                    currentplayingsongplayingview.setText(exoPlayer.getMediaMetadata().title);
                    currentplayingsongunderimageview.setText(exoPlayer.getMediaMetadata().title);
                    nowplaying.setText(exoPlayer.getMediaMetadata().title);
                    nowplaying.setSelected(true);
                    currentplayingsongunderimageview.setSelected(true);
//                    playingviewfiletype.setText(getSongType((String) exoPlayer.getMediaMetadata().title));
                    progresstime.setText(getthetime((int) exoPlayer.getCurrentPosition()));
                    seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                    seekBar.setMax((int) exoPlayer.getDuration());
                    totaltime.setText(getthetime((int) exoPlayer.getDuration()));
                    playbuttonhome.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_pause_24,0,0,0);
                    songnavigationplay.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.pauselarge,0,0,0);
                    CurrentAlbumArtwork();
                    updateCurrentsongposition();

                }
                else {
                    playbuttonhome.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_play_arrow_24,0,0,0);
//                    songnavigationplay.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_play_circle_outline_24,0,0,0);


                }
            }

        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    exoPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //repeat mode
        togglerepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatmode==1){
                    exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
                    repeatmode=2;
                    togglerepeat.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_repeat_one_24,0,0,0);
                    Toast.makeText(MainActivity.this, "repeat mode one", Toast.LENGTH_SHORT).show();

                }
                else if(repeatmode==2){
                    exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                    repeatmode=3;
                    togglerepeat.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_repeat_24,0,0,0);
                    Toast.makeText(MainActivity.this, "repeat mode all", Toast.LENGTH_SHORT).show();
                }
                else if(repeatmode==3){
                    exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                    repeatmode=1;
                    togglerepeat.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.norepeat,0,0,0);
                    Toast.makeText(MainActivity.this, "repeat mode off", Toast.LENGTH_SHORT).show();
                }



            }
        });
        shuffleswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shufflemode==1){
                exoPlayer.setShuffleModeEnabled(true);
                shuffleswitch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_shuffle_24,0,0,0);
                shufflemode=2;
            } else if (shufflemode==2) {
                    exoPlayer.setShuffleModeEnabled(false);
                    shuffleswitch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.noshuffle,0,0,0);
                    shufflemode=1;

                }
            }
        });

        playbuttonhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exoPlayer.isPlaying()){
                    exoPlayer.pause();
                    playbuttonhome.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_play_arrow_24,0,0,0);
                    songnavigationplay.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_play_circle_outline_24,0,0,0);

                }
                else  {
                    exoPlayer.play();
                    playbuttonhome.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_pause_24,0,0,0);
                    songnavigationplay.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.pauselarge,0,0,0);

                }
            }
        });
        homeskipnextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exoPlayer.hasNextMediaItem()){
                    exoPlayer.seekToNext();
                }
            }
        });
        homeskippreviousbuttton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exoPlayer.hasPreviousMediaItem()){
                    exoPlayer.seekToPrevious();
                }
            }
        });

    }



    private void filterList(String text) {
        List<Song>filterlist=new ArrayList<>();
        for(Song songs:allSongs){
            if (songs.getTitle().toLowerCase().contains(text.toLowerCase())) {

                filterlist.add(songs);
            }
            if (filterlist.isEmpty()){
                Toast.makeText(this, "No Songs", Toast.LENGTH_SHORT).show();
//                    songAdapter.FileteredList(allSongs);
            }
            else {
                songAdapter.FileteredList(filterlist);
            }
        }
    }

    private void updateCurrentsongposition() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(exoPlayer.isPlaying()){
                progresstime.setText(getthetime((int) exoPlayer.getCurrentPosition()));
                seekBar.setProgress((int) exoPlayer.getCurrentPosition());
            }
                //repetetion for the time and seekbar progresss
            updateCurrentsongposition();
            }

        },1000);
    }

    private String getthetime(int duration) {

        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minutes);
        if(minutes>59){
            long hours=TimeUnit.MILLISECONDS.toHours(duration);
            return hours +":"+ minutes +":"+ seconds;
        }
        return minutes +":"+ seconds;



    }
    private int timeFilter(int duration){
        long seconds=TimeUnit.MILLISECONDS.toSeconds(duration);

        return (int) seconds;
    }


    @NonNull
    private String getSongType(String name){
        String stype=name.substring(name.lastIndexOf('.'));
        return stype.toUpperCase();
    }
    private void CurrentAlbumArtwork() {

        requestOptions=requestOptions.transform(new CenterCrop(),new RoundedCorners(25));

        Glide.with(getApplicationContext())
                .load(Objects.requireNonNull(exoPlayer.getCurrentMediaItem()).mediaMetadata.artworkUri)
                .apply(requestOptions)
                .override(dptopixel(),dptopixel())
                .sizeMultiplier(1)
                .error(R.drawable.starboysvg)
                .into(imageviewinplayingview);
        Glide.with(getApplicationContext())
                .load(Objects.requireNonNull(exoPlayer.getCurrentMediaItem()).mediaMetadata.artworkUri)
                .override(dptopixelsmall(),dptopixelsmall())
                .apply(requestOptions)
                .error(R.drawable.starboysvg)
                .into(Currentplayingimage);


    }
    private int dptopixel(){
        return (int)(400* Resources.getSystem().getDisplayMetrics().density);
    }
    private int dptopixelsmall(){
        return (int)(50*Resources.getSystem().getDisplayMetrics().density);
    }

    private void ExittingPlayingView() {
        playerviewwrapper.setVisibility(View.GONE);
    }

    private void showplayerview() {
        playerviewwrapper.setVisibility(View.VISIBLE);

    }

    private void userResponses() {
        if(ContextCompat.checkSelfPermission(this,permission)== PackageManager.PERMISSION_GRANTED){
            fetchSongs();
        }
        else if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
            if(shouldShowRequestPermissionRationale(permission)){
                new AlertDialog.Builder(this)
                        .setTitle("Requesting Permisssion")
                        .setMessage("Allow us to fetch songs on your Device")
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                storagepermissionLauncher.launch(permission);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        }
        else{
            Toast.makeText(this,"cancelled permission",Toast.LENGTH_LONG).show();
        }
    }

    private void fetchSongs() {
        if(allSongs.isEmpty()){

        List<Song> songs =new ArrayList<>();
        Uri mediaStoreUri;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            mediaStoreUri= MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }
        else{
            mediaStoreUri=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String[] projection=new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST,
//                MediaStore.Audio.Media.BITRATE,
        };
        String sortOrder=MediaStore.Audio.Media.DEFAULT_SORT_ORDER+"";

        try(Cursor cursor=getContentResolver().query(mediaStoreUri,projection,null,null,sortOrder)) {
            //cache the cursor indices
//            Toast.makeText(this, "entering try block", Toast.LENGTH_SHORT).show();

            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int namecolumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationcolumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);

//              int bitrate=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BITRATE);
//
            int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
            int artistID = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);

//
//            Toast.makeText(this, "trying to enter while loop", Toast.LENGTH_SHORT).show();
            while (cursor.moveToNext()){
                //get the values of a column for a given audio file

                  long id = cursor.getLong(idColumn);
                  String songname = cursor.getString(namecolumn);
                  int duration = cursor.getInt(durationcolumn);
                  long albumId = cursor.getLong(albumIdColumn);

                String artistId=cursor.getString(artistID);
                    int timefilter=timeFilter(duration);



                  Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                  Uri albumarturi = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
                  if(timefilter>59){

                  Song song = new Song(songname, uri, albumarturi, duration, 10,artistId );
                  songs.add(song);

                  }
//                  Toast.makeText(this, "Successfully Ran while Loop", Toast.LENGTH_SHORT).show();


            }
            cursor.close();
//            Toast.makeText(this, "going to show songs function", Toast.LENGTH_SHORT).show();
            showSongs(songs);
        }
        catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            Log.d("Exception",e.toString());

        }

    }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        if(exoPlayer.isPlaying()){
            exoPlayer.stop();
        }
        exoPlayer.release();
    doUnBindService();
    }

    private void doUnBindService() {
        if(isBound){
            unbindService(playerServiceConnection);
            exoPlayer.stop();
            exoPlayer.setForegroundMode(false);
            isBound=false;

        }
    }


    private void showSongs(List<Song> songs) {
            Intent intent=new Intent(this,NosongsActivity.class);



        allSongs.addAll(songs);
        if(allSongs.isEmpty())startActivity(intent);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        songAdapter =new SongAdapter(this,songs,exoPlayer,playerviewwrapper);
        Toast.makeText(this, "Setting recyclerView", Toast.LENGTH_SHORT).show();
        recyclerView.setAdapter(songAdapter);





    }






    private class SwipeListener implements View.OnTouchListener {
        // initialize variable
        GestureDetector gestureDetector;
        SwipeListener(View view){
            int threshold=100;
            int velocity_threshold=100;
            // initialize simple gesture listerner
            GestureDetector.SimpleOnGestureListener listener= new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDown(@NonNull MotionEvent e) {
                    return  true;
                }

                @Override
                public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                     // get x and y differences
                    float xDiff=e2.getX()-e1.getX();
                    float yDiff=e2.getY()-e1.getY();
                    try {
                        if(Math.abs(xDiff)>Math.abs(yDiff)){
                            if(Math.abs(xDiff)>threshold &&Math.abs(velocityX)>velocity_threshold){
                                // when x difference is greater than threshold
                                // when x velocity is greater than velocity threshold
                                if(xDiff>0){
                                    // swiped right
                                    exoPlayer.seekToPrevious();
                                    Toast.makeText(MainActivity.this, "swiped right ", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    exoPlayer.seekToNext();
                                    //swiped left
                                    Toast.makeText(MainActivity.this, "swiped left", Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            }
                        }
                        else {
                            if(Math.abs(yDiff)>threshold &&Math.abs(velocityY)>velocity_threshold){
                                // when y diffrernce is greater than threshold
                                // when y velocity is greater than thershold
                                if(yDiff>0){
                                    // swiped Down
                                    playerviewwrapper.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "Swiped down ", Toast.LENGTH_SHORT).show();
                                }else {
                                    // swiped up
                                    Toast.makeText(MainActivity.this, "swiped up ", Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return  false;

                }
            };
            // initiallize the gesture detector
            gestureDetector =new GestureDetector(listener);
            view.setOnTouchListener(this);
        }
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }
    }
}