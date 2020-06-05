package com.fdev.betaplayer.service.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fdev.betaplayer.Util.DatabaseUtil;
import com.fdev.betaplayer.service.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    MutableLiveData<User> currentUser;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(DatabaseUtil.COLLECTION_USER);
    //   MutableLiveData<User> firebaseLogIn

    public UserRepository(){
        currentUser = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!= null){
            logging("get current user");
            getUserDetail(firebaseUser.getUid());
        }else{
            currentUser.setValue(null);
        }
    }

    public synchronized  void signUp(User user , String password){
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), password)
               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                           user.setUid(firebaseUser.getUid());
                           usersRef.document(user.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   currentUser.setValue(user);
                               }
                           })
                           .addOnFailureListener(e -> logging("Fail to add To Database " + e.getMessage()));
                       }else{
                           currentUser.setValue(null);
                       }
                   }
               }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                logging(e.getMessage());
                currentUser.setValue(null);
            }
        });
    }

    public void getUserDetail(String UID){
        usersRef.document(UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = (User) documentSnapshot.toObject(User.class);
                if(user!=null){
                    logging("get email :" + user.getEmail());
                    currentUser.setValue(user);
                }else{
                    currentUser.setValue(null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                logging(e.getMessage());
                currentUser.setValue(null);
            }
        });
    }

    public MutableLiveData<User> getCurrentUser(){
        return currentUser;
    }



    private void logging(String message){
        Log.d("In" + UserRepository.class , message);
    }




}
