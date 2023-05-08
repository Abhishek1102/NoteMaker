package com.example.makeanoteapp.activity.splash_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.makeanoteapp.R;
import com.example.makeanoteapp.activity.HomeActivity;
import com.example.makeanoteapp.helper.AppConstant;
import com.example.makeanoteapp.helper.SecurePreferences;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initView();
            }
        },2000);
    }

    private void initView() {

        //night mode and light mode condition check
        if (SecurePreferences.getBooleanPreference(getApplicationContext(), AppConstant.IS_DARKMODE_ON)) {
            //when night mode equals to true set dark theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            //when night mode equals to no set light theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        finish();
    }
}