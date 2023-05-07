package com.example.tpexamplemusic;

import static com.google.android.exoplayer2.util.NotificationUtil.IMPORTANCE_DEFAULT;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class PlayerService extends Service {
    private final IBinder servicebinder=new ServiceBinder();
    ExoPlayer player;
    PlayerNotificationManager notificationManager;
    public  class ServiceBinder extends Binder{
        public  PlayerService getPlayerService (){
            return PlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return servicebinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player=new ExoPlayer.Builder(getApplicationContext()).build();
        com.google.android.exoplayer2.audio.AudioAttributes audioAttributes=new com.google.android.exoplayer2.audio.AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)

                .build();
        player.setAudioAttributes(audioAttributes,true);
        final String channelId=getResources().getString( R.string.app_name)+"Music Channel ";
        final int notificationId=1111112;
        notificationManager=new PlayerNotificationManager.Builder(this,notificationId,channelId)
                .setNotificationListener(notificationListener)
                .setMediaDescriptionAdapter(descriptionAdapter)
                .setChannelImportance(IMPORTANCE_DEFAULT)
                .setNextActionIconResourceId(R.drawable.baseline_skip_next_24)
                .setPreviousActionIconResourceId(R.drawable.baseline_skip_previous_24)
                .setPlayActionIconResourceId(R.drawable.baseline_play_arrow_24)
                .setPauseActionIconResourceId(R.drawable.baseline_pause_24)
                .setStopActionIconResourceId(R.drawable.baseline_stop_24)

                .setChannelNameResourceId(R.string.app_name)

                .build();
    notificationManager.setPlayer(player);
    notificationManager.setPriority(NotificationCompat.PRIORITY_DEFAULT);
    notificationManager.setUseRewindAction(false);
    notificationManager.setUseFastForwardAction(false);
    notificationManager.setUseStopAction(true);
    notificationManager.setColor(R.color.black);


    }

    @Override
    public void onDestroy() {
        if(player.isPlaying())player.stop();
        notificationManager.setPlayer(null);
        player.release();
        player=null;

        stopForeground(true);
        stopSelf();
//        stopSelf(1111111);
        super.onDestroy();
    }

    PlayerNotificationManager.NotificationListener notificationListener=new PlayerNotificationManager.NotificationListener() {
        @Override
        public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
            PlayerNotificationManager.NotificationListener.super.onNotificationCancelled(notificationId, dismissedByUser);
            stopForeground(true);
//            stopService(new Intent(getApplicationContext(),MainActivity.class));
//changes mades
            }

        @Override
        public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
            PlayerNotificationManager.NotificationListener.super.onNotificationPosted(notificationId, notification, ongoing);
            startForeground(notificationId,notification);
        }
    };
PlayerNotificationManager.MediaDescriptionAdapter descriptionAdapter=new PlayerNotificationManager.MediaDescriptionAdapter() {
    @Override
    public CharSequence getCurrentContentTitle(Player player) {
        return Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.title;
    }

    @Nullable
    @Override
    public PendingIntent createCurrentContentIntent(Player player) {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);

        return PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_MUTABLE |PendingIntent.FLAG_UPDATE_CURRENT );
    }

    @Nullable
    @Override
    public CharSequence getCurrentContentText(Player player) {
        return null;
    }

    @Nullable
    @Override
    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
        ImageView view = new ImageView(getApplicationContext());
        view.setImageURI(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.artworkUri);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) view.getDrawable();
        if (bitmapDrawable == null) {
            bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.defaultmusicjpg);
        }
        assert bitmapDrawable != null;
        return bitmapDrawable.getBitmap();
    }
};
}