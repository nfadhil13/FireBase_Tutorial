package com.fdev.betaplayer.view.ui;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fdev.betaplayer.R;
import com.fdev.betaplayer.service.model.User;
import com.fdev.betaplayer.viewmodel.UserViewModel;


public class MainActivity extends AppCompatActivity {

    public final String KEY_USER_SIGNEDUP = "user";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserViewModel  userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user!=null){
                    Log.d("Loggin in" , "IN MAINACTIVITY");
                    Intent intent = new Intent(MainActivity.this,MusicListActivity.class);
                    intent.putExtra(KEY_USER_SIGNEDUP,user);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this,LogInActivity.class);
                    startActivity(intent);
                }
            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
    }
}
