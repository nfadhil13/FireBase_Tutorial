package com.fdev.betaplayer.view.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fdev.betaplayer.R;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnLogin , mBtnSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mBtnSignUp = findViewById(R.id.btn_login_signup);
        mBtnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int clicked = v.getId();
        switch (clicked){
            case R.id.btn_login_signup:
                Intent intent = new Intent(LogInActivity.this , SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
