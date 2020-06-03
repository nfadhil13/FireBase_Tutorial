package com.fdev.betaplayer.service.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.fdev.betaplayer.Util.DatabaseUtil;
import com.fdev.betaplayer.service.model.Music;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MusicRepository {

    private String TAG = "MUSIC_REPOSITORY";
    private static MusicRepository instance;
    private MutableLiveData<List<Music>> allMusic;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static MusicRepository getInstance(Context context){
        if(instance == null){
            instance = new MusicRepository();
        }
        return  instance;
    }

    public MutableLiveData<List<Music>> getAllMusic(){
        loadAllMusicData();
        return allMusic;
    }

    private void loadAllMusicData() {
        db.collection(DatabaseUtil.COLLECTION_MUSIC).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot queryDocumentSnapshot : q)
            }
        });
    }


}
