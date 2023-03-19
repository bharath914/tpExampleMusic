package com.example.tpexamplemusic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Song> songs;
    ExoPlayer player;
    ConstraintLayout playingView;
    public SongAdapter(Context context, List<Song> songs,ExoPlayer player,ConstraintLayout playingView) {
        this.context = context;
        this.songs = songs;
        this.player=player;
        this.playingView=playingView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.audiofileslist,parent,false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //onBindViewHolder current song and view holder
        Song song=songs.get(position);
        SongViewHolder viewHolder=(SongViewHolder) holder;
        viewHolder.titleHolder.setText(song.getTitle());
        viewHolder.durationHolder.setText(getduration(song.getDuration()));
        viewHolder.filetypeHolder.setText(getFileType(song.getTitle()));
        viewHolder.artistHolder.setText(song.getArtist());
        //artwork setting
        Uri artworkUri=song.getArtwork();
        if(artworkUri!=null){
            //setting artwork
            viewHolder.artworkHolder.setImageURI(artworkUri);
            //make sure that uri has an artwork
            if(viewHolder.artworkHolder.getDrawable()==null){
                viewHolder.artworkHolder.setImageResource(R.drawable.starboy);
            }
        }
        //on click listener
        viewHolder.itemView.setOnClickListener(view-> {
            //start the player service
            context.startService(new Intent(context.getApplicationContext(),PlayerService.class));
            if(!player.isPlaying()){
                player.setMediaItems(getMediaItemns(),position,0);
            }
            else {
                player.pause();
                player.seekTo(position,0);
            }
            player.prepare();
            player.play();

            Toast.makeText(context, song.getTitle(), Toast.LENGTH_SHORT).show();
            playingView.setVisibility(View.VISIBLE);
        });


    }


    private String getFileType(String filetype) {
        String fileformat=filetype.substring(filetype.lastIndexOf('.')+1,filetype.length());
        return fileformat;
    }

    private List<MediaItem> getMediaItemns() {
        List<MediaItem>mediaItems=new ArrayList<>();
        for (Song song:songs
             ) {
            MediaItem mediaItem=new MediaItem.Builder()
                    .setUri(song.getUri())
                    .setMediaMetadata(getMetadata(song))
                    .build();
            //add the media items to media item list
            mediaItems.add(mediaItem);
        }
        return  mediaItems;
    }

    private MediaMetadata getMetadata(Song song) {
        return  new MediaMetadata.Builder()
                .setTitle(song.getTitle())
                .setArtist(song.getArtist())
                .setArtworkUri(song.getArtwork())
                .build();
    }


    public static class SongViewHolder extends  RecyclerView.ViewHolder{
        ImageView artworkHolder;
        TextView titleHolder,durationHolder,filetypeHolder,artistHolder;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            artworkHolder=itemView.findViewById(R.id.albumart);
            titleHolder=itemView.findViewById(R.id.title);
            durationHolder=itemView.findViewById(R.id.duration);
            filetypeHolder=itemView.findViewById(R.id.filetype);
            artistHolder=itemView.findViewById(R.id.artist);
            titleHolder.setSelected(true);
            artistHolder.setSelected(true);
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void filterSongs(List<Song> filteredList){
        songs=filteredList;
        notifyDataSetChanged();
    }
//    public String songDuration(long bytes){
//        String  nsize;
//        double kb=bytes/1024.0;
//        double mb=(kb/1024.0);
//        double gb=(mb/1024.0);
//        double tb=(gb/1024.0);
//        DecimalFormat dc=new DecimalFormat("0.00");
//        if(tb>1){
//            nsize=dc.format(tb).concat("TB");
//        }else if(gb>1){
//            nsize=dc.format(gb).concat("GB");
//        }else if(mb>1){
//            nsize=dc.format(mb).concat("MB");
//        }else if(kb>1){
//            nsize=dc.format(kb).concat("KB");
//        }else{
//            nsize=dc.format(bytes).concat("B");
//        }
//        return nsize;
//    }
    @SuppressLint("DefaultLocale")
    public String getduration(int duration){
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minutes);
        if(minutes>59){
            long hours=TimeUnit.MILLISECONDS.toHours(duration);
            return String.valueOf(hours)+"H"+" "+ String.valueOf(minutes)+"M"+" "+String.valueOf(seconds)+"S";
        }
        return String.valueOf(minutes)+"M"+" "+String.valueOf(seconds)+"S";
    }
}
