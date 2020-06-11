package com.fdev.betaplayer.view.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fdev.betaplayer.R;
import com.fdev.betaplayer.service.model.User;
import com.fdev.betaplayer.viewmodel.UserViewModel;

public class VerifyActivity extends AppCompatActivity {

    public final String KEY_USER_SIGNEDUP = "user";

    private Button mButtonVerify;
    private EditText mEditeTextOTP;
    private ProgressBar mProgressBar;
    private String phoneNumber;
    private UserViewModel mUserViewModel;
    private User unveryfiedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        mButtonVerify = findViewById(R.id.verify_btn_verify);
        mEditeTextOTP = findViewById(R.id.verify_edit_text_otp);
        mProgressBar = findViewById(R.id.verify_progressbar);

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mUserViewModel.getCurrentUser().observe(this, user -> {
            mProgressBar.setVisibility(View.INVISIBLE);
            Log.d("user changed" , "user changed");
            //Log in Success
            if(user != null) {
                Intent intent = new Intent(VerifyActivity.this, MusicListActivity.class);
                intent.putExtra(KEY_USER_SIGNEDUP, user);
                startActivity(intent);
            }else{
                String errorMessage = mUserViewModel.getErrorMessage();
                if(!TextUtils.isEmpty(errorMessage)){
                    Log.d("Error Message" , errorMessage);
                    showLongToast(errorMessage);
                }
            }
        });

        if(getIntent().hasExtra(KEY_USER_SIGNEDUP)){
            if(getIntent().getSerializableExtra(KEY_USER_SIGNEDUP) != null){
                unveryfiedUser = (User)getIntent().getSerializableExtra(KEY_USER_SIGNEDUP);
                mUserViewModel.signUp(unveryfiedUser);
            }
        }

        mButtonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(mEditeTextOTP.getText())){
                    mUserViewModel.manualInputCode(mEditeTextOTP.getText().toString(),unveryfiedUser);
                }
            }
        });


    }

    private void showLongToast(String messsage) {
        Toast.makeText(VerifyActivity.this,messsage , Toast.LENGTH_LONG).show();
    }


}