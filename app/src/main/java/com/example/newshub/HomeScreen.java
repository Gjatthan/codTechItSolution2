package com.example.newshub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.newshub.Adapters.NewsHighlightAdptr;
import com.example.newshub.databinding.ActivityHomeScreenBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.Source;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.request.SourcesRequest;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import com.kwabenaberko.newsapilib.models.response.SourcesResponse;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {
    ActivityHomeScreenBinding binder;
    int tab=1;
    ArrayList<Article> articles;
    NewsApiClient newsApiClient;
    NewsHighlightAdptr newsHighlightAdptr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder=ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binder.getRoot());

        loadFragment(new FragmentHome(binder.getRoot().getContext()));

        binder.bottomnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:loadFragment(new FragmentHome(binder.getRoot().getContext()));
                    tab=1;
                    break;
                    case R.id.profile:loadFragment(new FragmentProfile(binder.getRoot().getContext()));
                    tab=2;
                    break;
                    case R.id.community:loadFragment(new FragmentCommunity(binder.getRoot().getContext()));
                        tab=3;
                    break;
                }
                return true;
            }
        });
//
//        newsApiClient = new NewsApiClient("9ba238cb5a5845ffa8ab94ef3e55bb6b");
//        articles=new ArrayList<>();
//        newsHighlightAdptr=new NewsHighlightAdptr(articles,this);
//        binder.newscycle.setLayoutManager(new LinearLayoutManager(this));
//        binder.newscycle.setAdapter(newsHighlightAdptr);
//
//        visibilitySettings(View.VISIBLE,View.VISIBLE,View.GONE,View.GONE);
//        binder.shimmer.startShimmer();
//
//        loadNews("en",null,null);
//
//        binder.rbngrp.setOnCheckedChangeListener(this);
//
//        binder.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                visibilitySettings(View.VISIBLE,View.VISIBLE,View.GONE,View.GONE);
//                binder.shimmer.startShimmer();
//                RadioButton rbtn=(RadioButton)findViewById(binder.rbngrp.getCheckedRadioButtonId());
//                articles.clear();
//                loadNews("en",rbtn.getText().toString(),query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });

    }

    void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binder.maincontainer.getId(), fragment);
        transaction.commit();
    }

    public void onClickAddPost(View v){
        startActivity(new Intent(HomeScreen.this,PostActivity.class));
    }

    @Override
    public void onBackPressed() {
        if(tab==1){
            super.onBackPressed();
        }
        else {
            binder.bottomnav.findViewById(R.id.home).performClick();
        }
    }
}

    //private void loadNews(String lan,String cat,String query) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                newsApiClient.getEverything(
//                        new EverythingRequest.Builder()
//                                .pageSize(50)
//                                .sources("in")
//                                .from("2024-06-14")
//                                .to("2024-06-30")
//                                .build(),
//                        new NewsApiClient.ArticlesResponseCallback() {
//                            @Override
//                            public void onSuccess(ArticleResponse response) {
//                                for (Article a:response.getArticles()) {
//                                    articles.add(a);
//                                }
//                                binder.shimmer.stopShimmer();
//                                newsHighlightAdptr.notifyDataSetChanged();
//                                visibilitySettings(View.GONE,View.GONE,View.VISIBLE,View.GONE);
//                            }
//
//                            @Override
//                            public void onFailure(Throwable throwable) {
//                                visibilitySettings(View.GONE,View.GONE,View.GONE,View.VISIBLE);
//                                Log.d("TAG",throwable.getMessage());
//                                Toast.makeText(HomeScreen.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                );
//            }
//        },3000);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                newsApiClient.getTopHeadlines(
//                        new TopHeadlinesRequest.Builder()
//                                .language(lan)
//                                .q(query)
//                                .pageSize(100)
//                                .category(cat)
//                                .build(),
//                        new NewsApiClient.ArticlesResponseCallback() {
//                            @Override
//                            public void onSuccess(ArticleResponse response) {
//                                if(response.getArticles().size()==0){
//                                    visibilitySettings(View.GONE,View.GONE,View.GONE,View.VISIBLE);
//                                }
//                                else {
//                                    for (Article a : response.getArticles()) {
//                                        articles.add(a);
//                                    }
//                                    newsHighlightAdptr.notifyDataSetChanged();
//                                    visibilitySettings(View.GONE, View.GONE, View.VISIBLE, View.GONE);
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Throwable throwable) {
//                                visibilitySettings(View.GONE,View.GONE,View.GONE,View.VISIBLE);
//                                Log.d("TAG",throwable.getMessage());
//                            }
//                        }
//                );
//            }
//        },3000);
//
//
//    }
//    void visibilitySettings(int progress,int shimmer,int recycle,int error){
//        binder.progressHorizontal.setVisibility(progress);
//        binder.shimmer.setVisibility(shimmer);
//        binder.newscycle.setVisibility(recycle);
//        binder.nodatafoundanim.setVisibility(error);
//    }
//
//    @Override
//    public void onCheckedChanged(RadioGroup radioGroup, int i) {
//        RadioButton rbtn=radioGroup.findViewById(i);
//        if(rbtn.isChecked()) {
//            visibilitySettings(View.VISIBLE,View.VISIBLE,View.GONE,View.GONE);
//            binder.shimmer.startShimmer();
//            articles.clear();
//            loadNews("en", rbtn.getText().toString(),binder.searchView.getQuery().toString()==null?null:binder.searchView.getQuery().toString());
//        }
//            //Toast.makeText(this, ""+rbtn.getText(), Toast.LENGTH_SHORT).show();
//    }
//}