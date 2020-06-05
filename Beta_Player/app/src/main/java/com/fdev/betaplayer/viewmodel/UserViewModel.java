package com.fdev.betaplayer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fdev.betaplayer.service.model.User;
import com.fdev.betaplayer.service.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    MutableLiveData<User> currentUser;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository();
        currentUser = userRepository.getCurrentUser();
    }


    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void signUp(User user , String password){
        userRepository.signUp(user,password);
    }
}
