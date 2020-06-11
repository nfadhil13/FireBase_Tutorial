package com.fdev.betaplayer.view.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fdev.betaplayer.R;
import com.fdev.betaplayer.Util.UserSingleton;
import com.fdev.betaplayer.service.audioservices.AudioPlayerService;
import com.fdev.betaplayer.service.model.Music;
import com.fdev.betaplayer.service.model.User;
import com.fdev.betaplayer.view.adapter.MusicListRecylerViewAdapter;
import com.fdev.betaplayer.viewmodel.AudioServiceBindingViewModel;
import com.fdev.betaplayer.viewmodel.MusicListViewModel;
import com.fdev.betaplayer.viewmodel.MusicViewModel;
import com.fdev.betaplayer.viewmodel.UserViewModel;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MusicListActivity extends AppCompatActivity  implements MusicListRecylerViewAdapter.OnPlayClickedListener , View.OnClickListener {
    //Intent key from MainActivity
    public final static String USER_INPUT ="user";

    //Every variable to deal with recylerview list to show the music list
    private RecyclerView mRecyclerViewMusics; // Recylerview for music list
    private MusicListRecylerViewAdapter mMusicRecListAdapter; // Adapter for reclyerView lIST
    private List<Music> mMusicListShow; //mMusict to show at reclyerView
    private MusicViewModel mMusicViewModel; // View model to get  music data list from the music


    private MusicListViewModel mMusicListViewModel;/* View model to make sure that all activities play the same song as the service*/



    private Button mBtnAdd;
    private Button mBtnLogOut;

    private PlayerView mPlayerView;
    private SimpleExoPlayer mPlayer;

    private PlayerNotificationManager playerNotificationManager;
    private Intent audioIntent;

    private AudioPlayerService mAudioService;
    private AudioServiceBindingViewModel mAudioBindinVM;

    //Variables are responsible for more pop up dialog
    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;
    private RelativeLayout mAddToQueueRelativeLayout , mShareRelativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        Log.d("Checking User Log In Method" , "Status " );
        if(getIntent().hasExtra(USER_INPUT)){
            Log.d("Checking User Log In Method" , "Status  : " + "via intent" );
            //Check if it has intent from previous activity (log in activity or sign up)
            User user = (User) getIntent().getSerializableExtra(USER_INPUT);
            UserSingleton.getInstance().setUser(user);
            initMusicListActivity();
        } else if(UserSingleton.getInstance().getUser()==null){
            Log.d("Checking User Log In Method" , "Status  : " + "via check database" );
            UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
            userViewModel.getCurrentUser().observe(this, user -> {
                if(user!=null){
                    UserSingleton.getInstance().setUser(user);
                    initMusicListActivity();
                }else{
                    Intent failIntent = new Intent(MusicListActivity.this, LogInActivity.class);
                    startActivity(failIntent);
                }
            });
            //A condition where User singleton Might be destroyed by the system
        }else{
            Log.d("Checking User Log In Method" , "Status  : " + "via UserSingleton" );
            initMusicListActivity();
        }


    }

    public void initMusicListActivity(){
        Log.d("Inisiasi music list dan player" , "Status  : " + "mulai" );
        /* Initiate List Music that will be populate to the reclyerview */
        mMusicListShow = new ArrayList<>();
        /* Intiate the MusicViewModel(VM for showing the music list that taken from the database */
        mMusicViewModel = new ViewModelProvider(this).get(MusicViewModel.class);

        /* Initate MusicListVM (VM for communicate between activities and audiPlayerService about the music queue) */
        mMusicListViewModel = new ViewModelProvider(this).get(MusicListViewModel.class);

        /* Iniatae PlayerView for "detail player" in this Activity */
        mPlayerView = findViewById(R.id.music_list_player);

        /* Iniate ReclyerView */
        mRecyclerViewMusics = findViewById(R.id.music_list_recylerview);
        mRecyclerViewMusics.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewMusics.setHasFixedSize(true);

        mMusicRecListAdapter = new MusicListRecylerViewAdapter(this);
        mRecyclerViewMusics.setAdapter(mMusicRecListAdapter);

        mMusicRecListAdapter.setMusicList(mMusicListShow);

        mBtnAdd = findViewById(R.id.music_list_add_music);
        mBtnAdd.setOnClickListener(this);

        mBtnLogOut = findViewById(R.id.music_list_signout);
        mBtnLogOut.setOnClickListener(this);

        /* Observe to lIVEDATA of musicShow that would be shown .Data are from the database */
        mMusicViewModel.getAllMusic().observe(this, music -> {
            if(music!=null){
                /* Get list and store it to Activities Variabel so it coulbe be user for playing purpose */
                mMusicListShow = music;
                /* Update RecylerView */
                mMusicRecListAdapter.setMusicList(mMusicListShow);
            }
        });

        /* Get All music in database */
        mMusicViewModel.loadAllMusic();

        /* Get binding from AudioBinding VM so when there is a binding success , we can interact with the AudioService */
        mAudioBindinVM = new ViewModelProvider(this).get(AudioServiceBindingViewModel.class);

        /* Observe iBinder */
        mAudioBindinVM.getmBinder().observe(this,localBinder -> {
            if(localBinder!=null){
                boolean a = mAudioBindinVM.getmBinder()!=null;
                Log.d("IN Observer " , "Status " + a );
                mAudioService = localBinder.getService();
                a = mPlayerView.getPlayer()==null;
                Log.d("IN Observer " , "Status Player before " + a );
                mPlayerView.setPlayer(mAudioService.getplayerInstance());
                a = mPlayerView.getPlayer()==null;
                Log.d("IN Observer " , "Status Player after " + a );
            }else{
                mAudioService = null;
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d("On Start" , "Status  : " + "on Start" );
        super.onStart();
    }
    @Override
    public void onStop() {
        super.onStop();
        boolean a = mAudioBindinVM.isBinded();
        Log.d("Melakukan ubind terhadap service sebelum" , "Status  : " + a );
        if(mAudioBindinVM.isBinded()){
            unbindService(mAudioBindinVM.getServiceConnection());
            mAudioBindinVM.setBinded(false);
        }
        a = mAudioBindinVM.isBinded();
        Log.d("Melakukan ubind terhadap service setelah " , "Status  : " + a );
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("On Pause" , "Status  : " + "on pause" );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("On Resume" , "Status  : " + "on resume" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("On Destroy" , "Status  : " + "on Destroy" );
    }







    @Override
    public void onClickImageListener(int position) {
        playMusicNow(position);
    }

    private void playMusicNow(int position) {
        Music music = mMusicViewModel.getAllMusic().getValue().get(position);
        if(mAudioService==null){
            initMusicPlay(music);
        }else{
            Log.d("Memasukan music via list datalive" , "memasukan musik");
            mMusicListViewModel.playMusicNow(music);
        }
    }

    private void initMusicPlay(Music music) {
        Log.d("Masukan musik ke list perantar" , "mulai");
        mMusicListViewModel.initMusic(music);
        Log.d("is binding" , "binding");
        startService();
    }
    /* Make a service to play music on the foreground */
    private void startService(){
        audioIntent = new Intent(MusicListActivity.this, AudioPlayerService.class);
        Log.d("Memulai startForeGroundService " , "Memulai");
        Util.startForegroundService(this,audioIntent);
        Log.d("Memulai BindService " , "Memulai");
        bindService(audioIntent, mAudioBindinVM.getServiceConnection(), Context.BIND_AUTO_CREATE);

    }



    @Override
    public void onClickMoreListener(int position) {
        createMusicMorePopUPDialog(position);
    }



    //Create popup dialog for music detail like more , share etc
    private void createMusicMorePopUPDialog(int position){

            mBuilder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.pop_up_music_action,null);

            mAddToQueueRelativeLayout = view.findViewById(R.id.popop_layout_add_to_queue);
            mAddToQueueRelativeLayout.setOnClickListener(v -> {
                addMusicToQueue(position);
                mDialog.dismiss();
                showShortToast("Succes Add Music To Queue");
            });

            mShareRelativeLayout = view.findViewById(R.id.popop_layout_share);
            mShareRelativeLayout.setOnClickListener(v->{
                Music music = mMusicListShow.get(position);
                String textToShare = "Im listening to " + music.getTitle() + " in #BetaPlayer";
                shareText(textToShare);
            });



            mBuilder.setView(view);

            mDialog = mBuilder.create();
            mDialog.show();


    }
    /* Callback on Clicked RecyclerView item */
    private void addMusicToQueue(int position){

        /* Get Music item from the list . It could happen coz we keep the list on this acitivity same a the list on Adapter */
        Music music = mMusicViewModel.getAllMusic().getValue().get(position);
        Log.d("Masang musik" , "Status  : " + music.getTitle() );
        /* Check if the service already on the foreground */
        if(mAudioService==null){
            Log.d("Masukan musik ke list perantar" , "mulai");
            mMusicListViewModel.initMusic(music);
            Log.d("is binding" , "binding");
            startService();
        }else{
            Log.d("Memasukan music via list datalive" , "memasukan musik");
            mMusicListViewModel.addMusic(music);
        }
    }


    private void shareText(String textToShare) {
        // Create a String variable called mimeType and set it to "text/plain"
        String mimeType = "text/plain";

        // COMPLETED (3) Create a title for the chooser window that will pop up
        /* This is just the title of the window that will pop up when we call startActivity */
        String title = "Learning How to Share";

        // COMPLETED (4) Use ShareCompat.IntentBuilder to build the Intent and start the chooser
        /* ShareCompat.IntentBuilder provides a fluent API for creating Intents */
        ShareCompat.IntentBuilder
                /* The from method specifies the Context from which this share is coming from */
                .from(this)
                .setType(mimeType)
                .setChooserTitle(title)
                .setText(textToShare)
                .startChooser();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.music_list_add_music :
                Intent intent = new Intent(MusicListActivity.this,PostMusicActivity.class);
                startActivity(intent);
                break;
            case R.id.music_list_signout:
                FirebaseAuth.getInstance().signOut();
                Intent intentSignOut = new  Intent(MusicListActivity.this,LogInActivity.class);
                startActivity(intentSignOut);
                break;
        }

    }

    private void showShortToast(String message){
        Toast.makeText(MusicListActivity.this,message,Toast.LENGTH_SHORT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
