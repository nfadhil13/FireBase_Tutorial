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
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class UserRepository {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private MutableLiveData<User> currentUser;
    private String smsVerficationCode;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private MutableLiveData<Boolean> isExist = new MutableLiveData<>();
    private CollectionReference usersRef = rootRef.collection(DatabaseUtil.COLLECTION_USER);
    private String errorMessage;
    //   MutableLiveData<User> firebaseLogIn

    public UserRepository(){
        isExist = new MutableLiveData<>();
        currentUser = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!= null){
            logging("get current user");
            getUserDetail(firebaseUser.getUid());
        }else{
            currentUser.setValue(null);
        }
    }


    public void getUserDetail(String UID){
        usersRef.document(UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = (User) documentSnapshot.toObject(User.class);
                if(user!=null){
                    logging("get email :" + user.getPhoneNumber());
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
                errorMessage = e.getMessage();
            }
        });
    }

    public MutableLiveData<User> getCurrentUser(){
        return currentUser;
    }

    public synchronized  void sendVerficationCode(User user){
        logging(user.getPhoneNumber());
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                user.getPhoneNumber(),
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD
                , new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        Log.e("on Code Sent " , "Code " + s);
                        smsVerficationCode = s;

                    }
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        String code = phoneAuthCredential.getSmsCode();
                        if(code != null){
                            Log.e("onVerificationCompleted", phoneAuthCredential.getSmsCode());
                            verifyCode(code , user);
                        }

                    }



                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        logging(e.getMessage() + " " + "send verification failed");
                        errorMessage = e.getMessage();
                    }
                }
        );

    }
    public synchronized  void verifyCode(String userInputCode , User user) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(smsVerficationCode , userInputCode);
        Log.e("Verify Code" , "code veriffied");
        signInTheUser(credential , user);

    }

    private synchronized  void signInTheUser(PhoneAuthCredential credential , User user) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.e("Berhasil" , "code veriffied");
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        user.setUid(firebaseUser.getUid());
                        usersRef.document(user.getUid()).set(user).addOnSuccessListener(aVoid -> currentUser.setValue(user))
                                .addOnFailureListener(e -> {
                                    logging("Fail to add To Database " + e.getMessage());
                                    errorMessage = e.getMessage();
                                });
                    }else{
                        Log.e("Gagal" , "code unveriffied");
                        currentUser.setValue(null);
                        errorMessage = "Fail to veryfied , Comeback soon";
                    }


                }).addOnFailureListener(e->{
            logging(e.getMessage() + " " + "signIN the user");
            currentUser.setValue(null);
            errorMessage = e.getMessage();
        });
    }

    public void queryUserLogIn(String phoneNumber){
        Query query = usersRef.whereEqualTo("phoneNumber" , phoneNumber);
        Log.d("PROSES SIGN UP" , " mulai query nomor");
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if(!queryDocumentSnapshots.isEmpty()){
                    Log.d("PROSES SIGN UP" , " nomer udah ada");
                    User user = (User) queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                    currentUser.setValue(user);
                }else{
                    Log.d("PROSES SIGN UP" , " nomer acan ada");
                    currentUser.setValue(null);
                }
            }
        }).addOnFailureListener((OnFailureListener) e -> {
            currentUser.setValue(null);
            errorMessage = e.getMessage();
            Log.d("PROSES SIGN UP" , e.getMessage());
        } )
        ;
    }

    public void queryUserName(String username){
        Query query = usersRef.whereEqualTo("username" , username);
        Log.d("PROSES SIGN UP" , " mulai query uname");
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Log.d("PROSES SIGN UP" , " username belum ada");
                    isExist.setValue(false);
                }else{
                    Log.d("PROSES SIGN UP" , " username udah ada");
                    isExist.setValue(true);
                }
            }
        }).addOnFailureListener(e ->{
            Log.d("PROSES SIGN UP" , e.getMessage());
            isExist.setValue(false);
            errorMessage=e.getMessage();
        } )
        ;
    }

    public MutableLiveData<Boolean> getIsExist() {
        return isExist;
    }

    private void logging(String message){
        Log.d("In" + UserRepository.class , message);
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
