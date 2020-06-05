package com.fdev.betaplayer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.fdev.betaplayer.service.model.Music;
import com.fdev.betaplayer.service.repository.MusicRepository;

import java.util.List;

public class MusicViewModel extends AndroidViewModel {

    private MusicRepository musicRepository;
    private LiveData<List<Music>> allMusic;
    private LiveData<Boolean> isDone;
    public MusicViewModel(@NonNull Application application) {
        super(application);
        musicRepository = MusicRepository.getInstance(application.getApplicationContext());
        isDone = musicRepository.getIsDone();
        allMusic = musicRepository.getAllMusic();
    }

    public LiveData<Boolean> getIsDone() {
        return isDone;
    }

    public void AddMusic(Music music){
        musicRepository.addMusic(music);
    }

    public LiveData<List<Music>> getAllMusic() {
        return allMusic;
    }

    public void loadAllMusic(){
        musicRepository.loadAllMusicData();
    }
}
