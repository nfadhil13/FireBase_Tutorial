package com.fdev.betaplayer.view.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fdev.betaplayer.R;
import com.fdev.betaplayer.Util.UserSingleton;
import com.fdev.betaplayer.service.model.Music;
import com.fdev.betaplayer.service.model.User;
import com.fdev.betaplayer.view.adapter.MusicListRecylerViewAdapter;
import com.fdev.betaplayer.viewmodel.MusicViewModel;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class MusicListActivity extends AppCompatActivity  implements MusicListRecylerViewAdapter.OnPlayClickedListener , View.OnClickListener {
    public final static String USER_INPUT ="user";

    private RecyclerView mRecyclerViewMusics;
    private MusicListRecylerViewAdapter mMusicRecListAdapter;
    private List<Music> mMusicList;
    private MusicViewModel mMusicViewModel;

    private Button mBtnAdd;

    private PlayerView mPlayerView;
    private SimpleExoPlayer mPlayer;
    private int lastPlayerPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        Intent intent = getIntent();
        if(intent.hasExtra(USER_INPUT)){
            User user = (User) intent.getSerializableExtra(USER_INPUT);
            UserSingleton.getInstance().setUser(user);
        }else{
            Intent inttent = new Intent(MusicListActivity.this, LogInActivity.class);
            startActivity(intent);
        }

        mMusicList = new ArrayList<>();
        mMusicViewModel = new ViewModelProvider(this).get(MusicViewModel.class);

        mPlayerView = findViewById(R.id.music_list_player);


        mRecyclerViewMusics = findViewById(R.id.music_list_recylerview);
        mRecyclerViewMusics.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewMusics.setHasFixedSize(true);

        mMusicRecListAdapter = new MusicListRecylerViewAdapter(this);
        mRecyclerViewMusics.setAdapter(mMusicRecListAdapter);

        mMusicRecListAdapter.setMusicList(mMusicList);

        mBtnAdd = findViewById(R.id.music_list_add_music);
        mBtnAdd.setOnClickListener(this);


        mMusicViewModel.getAllMusic().observe(this, music -> {
            if(music!=null){
                mMusicList = music;
                mMusicRecListAdapter.setMusicList(mMusicList);
            }
        });
        mMusicViewModel.loadAllMusic();
        
    }

    @Override
    protected void onStart() {
        super.onStart();


        mPlayer = new SimpleExoPlayer.Builder(this).build();

        mPlayerView.setPlayer(mPlayer);
        mPlayer.setPlayWhenReady(true);
    }

    void playMusic(Uri uri){
            DataSource.Factory datasoureFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, getString(R.string.app_name)));

            MediaSource videoSource = new ProgressiveMediaSource.Factory(datasoureFactory)
                    .createMediaSource(uri);

            mPlayer.prepare(videoSource);
    }

    @Override
    public void onClickListener(int position) {
        Music music = mMusicList.get(position);
        Uri uri = Uri.parse(music.getMusicURL());
        playMusic(uri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPlayer!=null){

        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.music_list_add_music){
            Intent intent = new Intent(MusicListActivity.this,PostMusicActivity.class);
            startActivity(intent);
        }
    }
}
