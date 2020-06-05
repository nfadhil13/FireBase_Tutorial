package com.fdev.betaplayer.service.firebase;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUploadFile {

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private MutableLiveData<String> uploadImageFileURL;
    private MutableLiveData<String> uploadAudioFileURL;

    public FirebaseUploadFile() {
        uploadImageFileURL = new MutableLiveData<>();
        uploadAudioFileURL = new MutableLiveData<>();
    }

    public void uploadImage(String userUpload , Uri uri){
        StorageReference filepath = storageReference
                .child("music")
                .child("images")
                .child(userUpload + Timestamp.now().getSeconds());
        filepath.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        filepath.getDownloadUrl().addOnSuccessListener(uri1 -> {
                            uploadImageFileURL.setValue(uri1.toString());
                        })
                        .addOnFailureListener(e->{
                            uploadImageFileURL.setValue(null);
                        })

                ).addOnFailureListener(e -> uploadImageFileURL.setValue(null));

    }

    public void uploadAudio(String userUpload ,Uri uri){
        StorageReference filepath = storageReference
                .child("music")
                .child("audio")
                .child(userUpload + Timestamp.now().toString());
        filepath.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        filepath.getDownloadUrl().addOnSuccessListener(uri1 -> {
                            uploadAudioFileURL.setValue(uri1.toString());
                        })
                                .addOnFailureListener(e->{
                                    uploadAudioFileURL.setValue(null);
                                })

                ).addOnFailureListener(e -> uploadAudioFileURL.setValue(null));



    }



    public MutableLiveData<String> getUploadAudioFileURL() {
        return uploadAudioFileURL;
    }

    public MutableLiveData<String> getUploadImageFileURL() {
        return uploadImageFileURL;
    }
}
