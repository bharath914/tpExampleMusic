package com.example.tpexamplemusic;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayer;

import java.util.List;

public class albumviewadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ExoPlayer player;
    List<Album> albums;
    RequestOptions requestOptions;

    public albumviewadapter(Context context, ExoPlayer player, List<Album> albums) {
        this.context = context;
        this.player = player;
        this.albums = albums;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.albumrecylerview,parent,false);
        requestOptions=requestOptions.transform(new CenterCrop(),new RoundedCorners(20));
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Album album=albums.get(position);
        AlbumViewHolder albumViewHolder=(AlbumViewHolder) holder;
        albumViewHolder.AlbumNameHolder.setText(album.getAlbumname());
        Uri artworkuri=album.getArtworkuri();
        Glide.with(context)
                .load(artworkuri)
                .override(dptopixel(),dptopixel())
                .error(R.drawable.defaultmusicjpg)
                .into(albumViewHolder.albumHolder);
    }
    private int dptopixel(){
        return (int)(150* Resources.getSystem().getDisplayMetrics().density);}
    @Override
    public int getItemCount() {
        return 0;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView albumHolder;
        TextView AlbumNameHolder;
        public AlbumViewHolder(@NonNull View itemview) {
            super(itemview);
            albumHolder=itemview.findViewById(R.id.albumartforrec);
            AlbumNameHolder=itemview.findViewById(R.id.albumname);
        }
    }
}
