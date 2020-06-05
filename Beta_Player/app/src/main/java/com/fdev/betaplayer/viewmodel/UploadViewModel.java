package com.fdev.betaplayer.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fdev.betaplayer.service.firebase.FirebaseUploadFile;

public class UploadViewModel extends AndroidViewModel {
    private FirebaseUploadFile firebaseUploadFile;
    private LiveData<String> uploadImageFileURL;
    private LiveData<String> uploadAudioFileURL;

    public UploadViewModel(@NonNull Application application) {
        super(application);
        firebaseUploadFile = new FirebaseUploadFile();
        uploadAudioFileURL = firebaseUploadFile.getUploadAudioFileURL();
        uploadImageFileURL = firebaseUploadFile.getUploadImageFileURL();

    }

    public LiveData<String> getUploadImageFileURL() {
        return uploadImageFileURL;
    }

    public LiveData<String> getUploadAudioFileURL() {
        return uploadAudioFileURL;
    }

    public void uploadAudio(String uploader , Uri uri){
        firebaseUploadFile.uploadAudio(uploader ,uri);
    }

    public void uploadImage(String uploader , Uri uri){
        firebaseUploadFile.uploadImage(uploader ,uri);
    }
}
