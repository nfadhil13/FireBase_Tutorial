package com.fdev.betaplayer.view.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fdev.betaplayer.R;
import com.fdev.betaplayer.Util.UserSingleton;
import com.fdev.betaplayer.service.model.Music;
import com.fdev.betaplayer.viewmodel.MusicViewModel;
import com.fdev.betaplayer.viewmodel.UploadViewModel;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.Timestamp;

import java.util.Date;

public class PostMusicActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_CODE = 11;
    private static final int MUSIC_CODE = 12;

    private Button mBtnAddFile , mBtnSave;
    private ImageButton mBtnAddImage;
    private EditText mEditTextTitle , mEditTextFile;
    private ImageView mImageViewImage;
    private ProgressBar mProgressBar;

    private PlayerView mPlayerView;

    private SimpleExoPlayer mPlayer;
    private Uri mChoosenImageUri , mChoosenAudioUri;

    private MusicViewModel mMusicViewModel;
    private UploadViewModel mUploadViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_music);

        mMusicViewModel = new ViewModelProvider(this).get(MusicViewModel.class);
        mUploadViewModel = new ViewModelProvider(this).get(UploadViewModel.class);

        mBtnAddFile = findViewById(R.id.add_music_btn_addfile);
        mBtnAddImage = findViewById(R.id.add_music_btn_addImage);
        mBtnSave = findViewById(R.id.add_music_btn_save);
        mEditTextTitle = findViewById(R.id.add_music_edit_text_title);
        mEditTextFile = findViewById(R.id.add_music_edit_text_musicfile);
        mImageViewImage = findViewById(R.id.add_music_imageview_musicimage);
        mProgressBar = findViewById(R.id.add_music_progressbar);
        mPlayerView = findViewById(R.id.my_exo_player);

        mBtnAddImage.setOnClickListener(this);
        mBtnAddFile.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);

        mProgressBar.setVisibility(View.INVISIBLE);

        initViewModelOnChanged();



    }

    @Override
    public void onClick(View v) {
        int clickedID = v.getId();
        switch(clickedID){
            case R.id.add_music_btn_addImage:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
                break;
            case R.id.add_music_btn_addfile:
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,MUSIC_CODE);
                break;
            case R.id.add_music_btn_save:
                saveMusic();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mPlayer = new SimpleExoPlayer.Builder(this).build();

        mPlayerView.setPlayer(mPlayer);
        mPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_CODE && resultCode == RESULT_OK){
            if(data!=null){
                mChoosenImageUri = data.getData();
                mEditTextFile.setText(mChoosenImageUri.toString());
                mImageViewImage.setImageURI(mChoosenImageUri);
            }
        }
        if(requestCode==MUSIC_CODE && resultCode == RESULT_OK){
            if(data!=null){
                mChoosenAudioUri = data.getData();
                mEditTextFile.setText(mChoosenAudioUri.toString());

                DataSource.Factory datasoureFactory = new DefaultDataSourceFactory(this,
                        Util.getUserAgent(this,getString(R.string.app_name)));

                MediaSource videoSource = new ProgressiveMediaSource.Factory(datasoureFactory)
                        .createMediaSource(mChoosenAudioUri);

                mPlayer.prepare(videoSource);
            }
        }


    }



    private void saveMusic(){
        if(!TextUtils.isEmpty(mEditTextTitle.getText().toString())){
            if(mChoosenImageUri!=null){
                if(mChoosenAudioUri!=null){
                    if(mPlayer.isPlaying()){
                        mPlayer.stop();
                        mPlayer.release();
                        mPlayer=null;
                    }
                    mProgressBar.setVisibility(View.VISIBLE);
                    mUploadViewModel.uploadImage(UserSingleton.getInstance().getUser().getUid(),mChoosenImageUri);
                    /*After done uploading the image , then i will upload the music.After Upload the music then
                        The program will upload music data to database with the link , All other things done by observing
                        livedata.
                    */
                }else{
                    Toast.makeText(this,"Upload your song first",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this,"Choose your image first",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Empty Title not Allowed",Toast.LENGTH_SHORT).show();
        }
    }

    public void initViewModelOnChanged(){
        mUploadViewModel.getUploadImageFileURL().observe(this,s->{
            if(s!=null){
                mUploadViewModel.uploadAudio(UserSingleton.getInstance().getUser().getUid()
                , mChoosenAudioUri);
            }else{
                shortToast("fail to upload image");
            }
        });

        mUploadViewModel.getUploadAudioFileURL().observe(this,s->{
            if(s!=null){
                String title = mEditTextTitle.getText().toString();
                Music music = new Music();
                music.setTitle(title);
                music.setImageURL(mUploadViewModel.getUploadImageFileURL().getValue());
                music.setMusicURL(s);
                music.setPostTime(new Timestamp(new Date()));
                music.setPostBy(UserSingleton.getInstance().getUser().getUsername());
                music.setPostByUID(UserSingleton.getInstance().getUser().getUid());
                mMusicViewModel.AddMusic(music);
            }else{
                shortToast("fail to upload music");
            }
        });

        mMusicViewModel.getIsDone().observe(this,isDone->{
            if(isDone){
                mProgressBar.setVisibility(View.INVISIBLE);
                shortToast("Upload succes");
                Intent intent=new Intent(PostMusicActivity.this,MusicListActivity.class);
                startActivity(intent);
            }else{
                shortToast("fail to upload your data to database");
            }
        });
    }

    private void shortToast(String message){
        Toast.makeText(PostMusicActivity.this,message,Toast.LENGTH_SHORT).show();
    }



}
