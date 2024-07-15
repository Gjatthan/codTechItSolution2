package com.example.newshub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;

import com.example.newshub.databinding.ActivityFullNewsScreenBinding;

public class FullNewsScreen extends AppCompatActivity {

    String news_url;
    ActivityFullNewsScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFullNewsScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        news_url=getIntent().getExtras().getString("url");

        binding.newsfullview.getSettings().setJavaScriptEnabled(true);
        binding.newsfullview.setWebChromeClient(new WebChromeClient());
        binding.newsfullview.loadUrl(news_url);
    }

    @Override
    public void onBackPressed() {
        if(binding.newsfullview.canGoBack())
            binding.newsfullview.goBack();
        else
            super.onBackPressed();
    }
}