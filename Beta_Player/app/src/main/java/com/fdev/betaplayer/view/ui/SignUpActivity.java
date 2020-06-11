package com.fdev.betaplayer.view.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
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

public class SignUpActivity extends AppCompatActivity {

    public final String KEY_USER_SIGNEDUP = "user";

    private Button mBtnSignUp;
    private ProgressBar mProgressBar;
    private EditText mEditTextPhoneNumber, mEditTextUsername , mEditTextPassword;
    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mBtnSignUp = findViewById(R.id.btn_signup_signup);
        mProgressBar = findViewById(R.id.progressbar_signup);
        mEditTextPhoneNumber = findViewById(R.id.edit_text_signup_phone_number);
        mEditTextUsername = findViewById(R.id.edit_text_signup_username);

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        mProgressBar.setVisibility(View.INVISIBLE);
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        mUserViewModel.getIsExist().observe(this , isUsernameExist ->{
            if(isUsernameExist){
                Log.d("PROSES SIGN UP" , " username udah ada");
                Toast.makeText(SignUpActivity.this,"Your number has registered" , Toast.LENGTH_LONG);
            }else{
                Log.d("PROSES SIGN UP" , " username ga ada");
                String phoneNumber = mEditTextPhoneNumber.getText().toString().trim();
                mUserViewModel.logIn(phoneNumber);
            }
        });

        mUserViewModel.getCurrentUser().observe(this, user -> {
            mProgressBar.setVisibility(View.INVISIBLE);
            if(user!= null){
                Log.d("PROSES SIGN UP" , "nomer udah ada");
                Toast.makeText(SignUpActivity.this,"Your number has registered" , Toast.LENGTH_LONG);
            }else{
                Log.d("PROSES SIGN UP" , " nomer ga ada");
                String phoneNumber = mEditTextPhoneNumber.getText().toString().trim();
                String username = mEditTextUsername.getText().toString().trim();

                if(!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(username)){
                    User tempUser = new User();
                    tempUser.setUsername(username);
                    tempUser.setPhoneNumber(phoneNumber);

                    Intent intent = new Intent(SignUpActivity.this,VerifyActivity.class);
                    intent.putExtra(KEY_USER_SIGNEDUP,tempUser);
                    startActivity(intent);
                }

            }
        });




    }

    private void signUp() {
        if(!TextUtils.isEmpty(mEditTextPhoneNumber.getText())){
            if(!TextUtils.isEmpty(mEditTextUsername.getText())){
                    mProgressBar.setVisibility(View.VISIBLE);
                    mBtnSignUp.setEnabled(false);
                    String username = mEditTextUsername.getText().toString().trim();
                    mUserViewModel.checkUsername(username);
                    Log.d("PROSES SIGN UP" , " CHECK LOG IN");

            }else{
                Toast.makeText(this,"Username must be filled",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this,"Email must be filled",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
