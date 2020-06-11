package com.fdev.betaplayer.service.audioservices;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.fdev.betaplayer.R;
import com.fdev.betaplayer.service.model.Music;
import com.fdev.betaplayer.service.repository.MusicListRepository;
import com.fdev.betaplayer.view.ui.MainActivity;
import com.fdev.betaplayer.viewmodel.MusicListViewModel;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class AudioPlayerService extends LifecycleService {

    public static final String PLAYBACK_CHANNEL_ID = "playback_channel";
    public static final int PLAYBACK_NOTIFICATION_ID = 1;
    private SimpleExoPlayer mPlayer;
    private List<Music> musicList;
    private PlayerNotificationManager playerNotificationManager;
    private ConcatenatingMediaSource concatenatingMediaSource;

//    MutableLiveData<AudioPlayerService.LocalBinder> mutableBinder = new MutableLiveData<>();

    private IBinder mBinder = new LocalBinder();
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        final Context mContext = this;



        mPlayer = new SimpleExoPlayer.Builder(mContext).build();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory
                (mContext, Util.getUserAgent(mContext,getString(R.string.app_name)));

        concatenatingMediaSource = new ConcatenatingMediaSource();


        musicList = MusicListRepository.getInstance().getMusicList();

        for(Music music : musicList){
            Log.d("music" , "judul " + music.getTitle());
            Uri uri = Uri.parse(music.getMusicURL());
            MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri);
            concatenatingMediaSource.addMediaSource(audioSource);
        }

        mPlayer.prepare(concatenatingMediaSource);
        mPlayer.setPlayWhenReady(true);

        PlayerNotificationManager.MediaDescriptionAdapter mediaDescriptionAdapter = new PlayerNotificationManager.MediaDescriptionAdapter() {
            @Override
            public CharSequence getCurrentContentTitle(Player player) {
                return musicList.get(player.getCurrentWindowIndex()).getTitle();
            }

            @Nullable
            @Override
            public PendingIntent createCurrentContentIntent(Player player) {
                Intent intent = new Intent(mContext , MainActivity.class);
                return PendingIntent.getActivity(mContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            }

            @Nullable
            @Override
            public CharSequence getCurrentContentText(Player player) {
                return "this is currentContext";
            }

            @Nullable
            @Override
            public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                return null;
            }
        };

        PlayerNotificationManager.NotificationListener notificationListener = new PlayerNotificationManager.NotificationListener(){
            @Override
            public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                startForeground(notificationId,notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                stopSelf();
            }

        };

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel
                (mContext,PLAYBACK_CHANNEL_ID,R.string.channel_name,R.string.channel_desc,PLAYBACK_NOTIFICATION_ID,mediaDescriptionAdapter,notificationListener);

        playerNotificationManager.setPlayer(mPlayer);
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_MAX);
        playerNotificationManager.setUseChronometer(false);



        MusicListRepository.getInstance().getMusicLiveData().observe(this,musicList -> {
            concatenatingMediaSource.clear();
            for(Music music : musicList){
                Log.d("music" , "judul " + music.getTitle());
                Uri uri = Uri.parse(music.getMusicURL());
                MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
                concatenatingMediaSource.addMediaSource(audioSource);
            }
            mPlayer.prepare(concatenatingMediaSource);
            mPlayer.setPlayWhenReady(true);
        });

        MusicListRepository.getInstance().getNewMusic().observe((LifecycleOwner) mContext, music -> {
            Uri uri = Uri.parse(music.getMusicURL());
            MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri);
            concatenatingMediaSource.addMediaSource(audioSource);
            if(!mPlayer.isPlaying()){
                mPlayer.prepare(concatenatingMediaSource);
                mPlayer.setPlayWhenReady(false);
            }
        });


    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        playerNotificationManager.setPlayer(null);
        mPlayer.release();
        mPlayer=null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void loadBitMap(String url , PlayerNotificationManager.BitmapCallback bitmapCallback){
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                bitmapCallback.onBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };


    }

    public SimpleExoPlayer getplayerInstance() {
        if (mPlayer == null) {
           return null;
        }
        return mPlayer;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    public class LocalBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }





}
