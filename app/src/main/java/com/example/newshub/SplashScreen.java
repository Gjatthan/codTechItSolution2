package com.example.newshub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences userDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        userDetails= getSharedPreferences("userDetails", MODE_PRIVATE);
        //userDetails.edit().clear().apply();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(userDetails.getBoolean("Login",false))
                    startActivity(new Intent(SplashScreen.this,HomeScreen.class));
                else
                    startActivity(new Intent(SplashScreen.this,LogSignIn.class));
                finish();
            }
        },5000);
    }
}