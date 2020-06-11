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

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    public final String KEY_USER_SIGNEDUP = "user";

    private Button mBtnLogin , mBtnSignUp;
    private EditText mEditTextPhoneNumber;
    private ProgressBar mProgressBar;

    private UserViewModel mUserViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mBtnSignUp = findViewById(R.id.btn_login_signup);
        mBtnSignUp.setOnClickListener(this);
        mBtnLogin = findViewById(R.id.btn_login_login);
        mBtnLogin.setOnClickListener(this);
        mEditTextPhoneNumber = findViewById(R.id.edit_text_login_phonenumber);

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mUserViewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user!=null){
                    Intent intent = new Intent(LogInActivity.this,VerifyActivity.class);
                    intent.putExtra(KEY_USER_SIGNEDUP,user);
                    startActivity(intent);
                }else{
                    if(!TextUtils.isEmpty(mEditTextPhoneNumber.getText())){
                        Toast.makeText(LogInActivity.this , "Your number is not registered , please sign Up", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int clicked = v.getId();
        switch (clicked){
            case R.id.btn_login_signup:
                Intent intent = new Intent(LogInActivity.this , SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login_login:
                logIn();
                break;


        }
    }

    private void logIn() {
        if(!TextUtils.isEmpty(mEditTextPhoneNumber.getText())){
            mUserViewModel.logIn(mEditTextPhoneNumber.getText().toString());
        }else{
            Toast.makeText(LogInActivity.this , "Input your number or SignUp", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
