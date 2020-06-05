package com.fdev.betaplayer.service.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.fdev.betaplayer.Util.DatabaseUtil;
import com.fdev.betaplayer.service.model.Music;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MusicRepository {

    private String TAG = "MUSIC_REPOSITORY";
    private static MusicRepository instance;
    private MutableLiveData<List<Music>> allMusic;
    private MutableLiveData<Boolean> isDone;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference musicRef = db.collection(DatabaseUtil.COLLECTION_MUSIC);

    private MusicRepository(){
        allMusic = new MutableLiveData<>();
        isDone = new MutableLiveData<>();
    }

    public static MusicRepository getInstance(Context context){
        if(instance == null){
            instance = new MusicRepository();
        }
        return  instance;
    }

    public MutableLiveData<List<Music>> getAllMusic(){
        return allMusic;
    }


    public void addMusic(Music music){
        addAMusic(music);
    }

    private synchronized void addAMusic(Music music) {
        musicRef.add(music).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                logging("Succes Upload music");
                isDone.setValue(true);
            }
        }).addOnFailureListener(e->{
            logging(e.getMessage());
            isDone.setValue(false);
        })
        ;
    }

    public MutableLiveData<Boolean> getIsDone() {
        return isDone;
    }

    public synchronized void loadAllMusicData() {
        db.collection(DatabaseUtil.COLLECTION_MUSIC).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<Music> newMusicList = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        Music music = documentSnapshot.toObject(Music.class);
                        newMusicList.add(music);
                    }
                    allMusic.setValue(newMusicList);
                }
            }
        });
    }

    private void logging(String message){
        Log.d("In" + MusicRepository.class , message);
    }



}
