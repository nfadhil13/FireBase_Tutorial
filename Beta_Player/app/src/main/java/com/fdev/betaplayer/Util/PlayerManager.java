package com.fdev.betaplayer.Util;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.ArrayList;

public class PlayerManager {


    private static final String TAG = "ExoPlayerManager";
    private static PlayerManager mInstance = null;
    PlayerView mPlayerView;
    DefaultDataSourceFactory dataSourceFactory;
    String uriString = "";
    ArrayList<String> mPlayList = null;
    Integer playlistIndex = 0;
    CallBacks.playerCallBack listner;
    private SimpleExoPlayer mPlayer;
}
