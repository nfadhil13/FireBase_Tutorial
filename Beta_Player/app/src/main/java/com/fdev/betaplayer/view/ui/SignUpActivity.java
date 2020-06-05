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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    public final String KEY_USER_SIGNEDUP = "user";

    private Button mBtnSignUp;
    private ProgressBar mProgressBar;
    private EditText mEditTextEmail , mEditTextUsername , mEditTextPassword;
    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mBtnSignUp = findViewById(R.id.btn_signup_signup);
        mProgressBar = findViewById(R.id.progressbar_signup);
        mEditTextEmail = findViewById(R.id.edit_text_signup_email);
        mEditTextPassword = findViewById(R.id.edit_text_signup_password);
        mEditTextUsername = findViewById(R.id.edit_text_signup_username);

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mUserViewModel.getCurrentUser().observe(this, user -> {
            mProgressBar.setVisibility(View.INVISIBLE);
            Log.d("user changed" , "user changed");
            //Log in Success
            if(user != null){
                Intent intent = new Intent(SignUpActivity.this,MusicListActivity.class);
                intent.putExtra(KEY_USER_SIGNEDUP,user);
                startActivity(intent);
            }else{
                Toast.makeText(SignUpActivity.this,
                        "The email address is already in use by another account.",
                        Toast.LENGTH_SHORT).show();
                // lOG IN FIAL
            }
        });
        mProgressBar.setVisibility(View.INVISIBLE);
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });




    }

    private void signUp() {
        if(!TextUtils.isEmpty(mEditTextEmail.getText())){
            if(!TextUtils.isEmpty(mEditTextUsername.getText())){
                if(!TextUtils.isEmpty(mEditTextPassword.getText())){
                    mProgressBar.setVisibility(View.VISIBLE);
                    mBtnSignUp.setEnabled(false);
                    String email = mEditTextEmail.getText().toString().trim();
                    String username = mEditTextUsername.getText().toString().trim();
                    String password = mEditTextPassword.getText().toString().trim();


                    mUserViewModel.signUp(new User(username,email),password);
                }else{
                    Toast.makeText(this,"Password must be filled", Toast.LENGTH_LONG).show();
                }
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
