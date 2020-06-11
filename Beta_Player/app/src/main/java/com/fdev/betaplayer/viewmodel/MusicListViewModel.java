package com.fdev.betaplayer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.fdev.betaplayer.service.model.Music;
import com.fdev.betaplayer.service.repository.MusicListRepository;

import java.util.List;

public class MusicListViewModel extends AndroidViewModel {
    private MusicListRepository musicListRepository;
    private LiveData<List<Music>> musicLiveData;
    private LiveData<Music> newMusic;


    public MusicListViewModel(@NonNull Application application) {
        super(application);
        musicListRepository = MusicListRepository.getInstance();
        musicLiveData = musicListRepository.getMusicLiveData();
        newMusic = musicListRepository.getNewMusic();

    }

    public void playMusicNow(Music music){
        musicListRepository.playMusicNow(music);
    }



    public LiveData<List<Music>> getMusicLiveData() {
        return musicLiveData;
    }

    public void addMusic(Music music){
        musicListRepository.addMusicToQueue(music);
    }

    public LiveData<Music> getNewMusic() {
        return newMusic;
    }

    public void initMusic(Music music){
        musicListRepository.initMusic(music);
    }

}
