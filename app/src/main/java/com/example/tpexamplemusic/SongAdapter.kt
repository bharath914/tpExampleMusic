package com.example.tpexamplemusic

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import java.util.*
import java.util.concurrent.TimeUnit

class SongAdapter     //    MainActivity mainActivity=new MainActivity();
    (
    var context: Context,
    var songs: List<Song>,
    var player: ExoPlayer,
    var playingView: ConstraintLayout
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var filteredsong: List<Song>? = null
    var requestOptions = RequestOptions()
    @SuppressLint("NotifyDataSetChanged")
    fun FileteredList(filteredList: List<Song>) {
        songs = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        requestOptions = requestOptions.transform(RoundedCorners(20))
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.audiofileslist, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //onBindViewHolder current song and view holder
        val song = songs[position]
        val viewHolder = holder as SongViewHolder
        viewHolder.titleHolder.text = song.getTitle()
        viewHolder.durationHolder.text = getduration(song.getDuration())
        viewHolder.filetypeHolder.text = song.fileTypename
        viewHolder.artistHolder.text = song.getArtist()
        //artwork setting
        val artworkUri = song.getArtwork()
        Glide.with(context)
            .load(artworkUri)
            .override(dptopixel(), dptopixel())
            .apply(requestOptions)
            .error(R.drawable.starboysvg) //                .centerCrop()
            .into(viewHolder.artworkHolder)
            .clearOnDetach()

        //on click listener
        viewHolder.itemView.setOnClickListener { view: View? ->
            context.startService(Intent(context.applicationContext, PlayerService::class.java))
            if (!player.isPlaying) {
                player.setMediaItems(mediaItemns, position, 0)
            } else {
                player.pause()
                player.seekTo(position, 0)
            }
            player.prepare()
            player.play()

//        Toast.makeText(context, song.getTitle(), Toast.LENGTH_SHORT).show();
            playingView.visibility = View.VISIBLE
        }
    }

    //    private String TitleSetter(String title) {
    //        String nodot=title.substring(0,title.lastIndexOf('.'));
    //        String output = nodot.replaceAll("^[\\p{Punct}\\d]+", "");
    //        String output2=output.replaceFirst(" -","");
    //        return output2;
    //
    //    }
    private fun getFileType(filetype: String): String {
        val fileformat = filetype.substring(filetype.lastIndexOf('.') + 1)
        return fileformat.uppercase(Locale.getDefault())
    }

    //add the media items to media item list
    private val mediaItemns: List<MediaItem>
        private get() {
            val mediaItems: MutableList<MediaItem> = ArrayList()
            for (song in songs) {
                val mediaItem = MediaItem.Builder()
                    .setUri(song.getUri())
                    .setMediaMetadata(getMetadata(song))
                    .build()
                //add the media items to media item list
                mediaItems.add(mediaItem)
            }
            return mediaItems
        }

    private fun getMetadata(song: Song): MediaMetadata {
        return MediaMetadata.Builder()
            .setTitle(song.getTitle())
            .setArtist(song.getArtist())
            .setArtworkUri(song.getArtwork())
            .build()
    }

    private fun dptopixel(): Int {
        return (80 * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun bitmapresize(bitmap: Bitmap?): Bitmap {
        return Bitmap.createScaledBitmap(bitmap     !!, dptopixel(), dptopixel(), false)
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var artworkHolder: ImageView
        var titleHolder: TextView
        var durationHolder: TextView
        var filetypeHolder: TextView
        var artistHolder: TextView

        init {
            artworkHolder = itemView.findViewById(R.id.albumart)
            titleHolder = itemView.findViewById(R.id.title)
            durationHolder = itemView.findViewById(R.id.duration)
            filetypeHolder = itemView.findViewById(R.id.filetype)
            artistHolder = itemView.findViewById(R.id.artist)
        }
    }

    override fun getItemCount(): Int {
        return songs.size
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
    fun getduration(duration: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) - TimeUnit.MINUTES.toSeconds(minutes)
        if (minutes > 59) {
            val hours = TimeUnit.MILLISECONDS.toHours(duration.toLong())
            return "$hours:$minutes:$seconds"
        }
        return "$minutes:$seconds"
    } //    private void RecylerViewOnclick(int position) {
    //        context.startService(new Intent(context.getApplicationContext(),PlayerService.class));
    //        if(!player.isPlaying()){
    //            player.setMediaItems(getMediaItemns(),position,0);
    //        }
    //        else {
    //            player.pause();
    //            player.seekTo(position,0);
    //        }
    //        player.prepare();
    //        player.play();
    //
    ////        Toast.makeText(context, song.getTitle(), Toast.LENGTH_SHORT).show();
    //        playingView.setVisibility(View.VISIBLE);
    //    }
}