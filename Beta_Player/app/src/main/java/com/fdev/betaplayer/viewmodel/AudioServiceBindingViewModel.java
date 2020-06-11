package com.fdev.betaplayer.viewmodel;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fdev.betaplayer.service.audioservices.AudioPlayerService;

public class AudioServiceBindingViewModel extends ViewModel {

    private static final String TAG = "AudioServiceBindingViewModel";


    private MutableLiveData<AudioPlayerService.LocalBinder> mBinder = new MutableLiveData<>();
    private boolean isBinded = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG,"onServiceConnected : connected to service");
                AudioPlayerService.LocalBinder binder = (AudioPlayerService.LocalBinder) service;
                mBinder.postValue(binder);
                isBinded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconected : Disconected to service");
            mBinder.postValue(null);
            isBinded = false;
        }
    };


    public MutableLiveData<AudioPlayerService.LocalBinder> getmBinder() {
        return mBinder;
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public boolean isBinded() {
        return isBinded;
    }

    public void setBinded(boolean binded) {
        isBinded = binded;
    }
}
