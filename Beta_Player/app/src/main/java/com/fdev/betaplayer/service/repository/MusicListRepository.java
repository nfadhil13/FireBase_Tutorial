package com.fdev.betaplayer.service.repository;


import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fdev.betaplayer.service.model.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicListRepository {
    private static MusicListRepository instance;
    private MutableLiveData<List<Music>> musicLiveData;
    private List<Music> musicList;
    private MutableLiveData<Music> newMusic;

    private MusicListRepository(){
        musicLiveData = new MutableLiveData<>();
        newMusic = new MutableLiveData<>();
        musicList = new ArrayList<>();
    }

    public static MusicListRepository getInstance(){
        if(instance==null){
            synchronized (MusicListRepository.class){
                if(instance==null){
                    instance = new MusicListRepository();
                    Log.d("instance " , "selesai");
                }
            }

        }
        return instance;
    }

    public void addMusicToQueue(Music music){
        newMusic.setValue(music);
        musicList.add(music);
    }

    public void playMusicNow(Music music){
        new MusicListRepository.AsyncPlayMusicNow(musicList, musicList -> {
            this.musicList = musicList;
            musicLiveData.setValue(musicList);
        }).execute(music);
    }



    public void musicListChange(List<Music> musicList){


        if(musicList!=null){
            musicLiveData.getValue().clear();
            musicLiveData.setValue(musicList);
            this.musicList.clear();
            this.musicList = musicList;
        }else{
            musicLiveData.setValue(null);
            this.musicList.clear();
        }
    }

    public void initMusic(Music music){
        musicList.add(music);
    }

    public MutableLiveData<Music> getNewMusic() {
        return newMusic;
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public MutableLiveData<List<Music>> getMusicLiveData() {
        return musicLiveData;
    }

    private static class AsyncPlayMusicNow extends AsyncTask<Music , Void ,List<Music>> {

        private List<Music> musicList;
        private AsycnPlayNowListener callback;
        private AsyncPlayMusicNow( List<Music> musicList ,AsycnPlayNowListener callback){
            this.musicList = musicList;
            this.callback = callback;
        }

        @Override
        protected List<Music> doInBackground(Music... music) {
            musicList.set(0,music[0]);
            for(Music newmusic : musicList){
                Log.d("music" , "judul di asycn " + newmusic.getTitle());
            }
            return musicList;
        }

        @Override
        protected void onPostExecute(List<Music> musicList) {
            super.onPostExecute(musicList);
            callback.processFinish(musicList);
        }


    }

    public interface AsycnPlayNowListener{
        void processFinish(List<Music> musicList);
    }

}
