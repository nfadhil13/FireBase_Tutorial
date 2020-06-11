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
    private LiveData<User> currentUser;
    private LiveData<Boolean> isExist;
    private String errorMessage;
    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository();
        currentUser = userRepository.getCurrentUser();
        isExist = userRepository.getIsExist();

    }


    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void signUp(User user){
        userRepository.sendVerficationCode(user);
    }
    public  void manualInputCode(String code , User user){userRepository.verifyCode(code , user);}
    //public void addUserToDatabase(User user){userRepository.addUserToDatabase(user);}

    public void logIn(String phoneNumber){
        userRepository.queryUserLogIn(phoneNumber);
    }


    public LiveData<Boolean> getIsExist() {
        return isExist;
    }

    public void checkUsername(String username){
        userRepository.queryUserName(username);
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
