package com.example.newshub;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.newshub.Adapters.NewsHighlightAdptr;
import com.example.newshub.databinding.ActivityHomeScreenBinding;
import com.example.newshub.databinding.FragmentHomeBinding;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.util.ArrayList;


public class FragmentHome extends Fragment implements RadioGroup.OnCheckedChangeListener{
    FragmentHomeBinding binder;
    ArrayList<Article> articles;
    NewsApiClient newsApiClient;
    NewsHighlightAdptr newsHighlightAdptr;
    Context mcontext;
    final int TAB1=1,TAB2=2,TAB3=3,TAB4=4;

    public FragmentHome(Context context) {
        mcontext=context;
    }

    private void loadNews(String lan, String cat, String query) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                newsApiClient.getTopHeadlines(
                        new TopHeadlinesRequest.Builder()
                                .language(lan)
                                .q(query)
                                .pageSize(100)
                                .category(cat)
                                .build(),
                        new NewsApiClient.ArticlesResponseCallback() {
                            @Override
                            public void onSuccess(ArticleResponse response) {
                                if(response.getArticles().size()==0){
                                    visibilitySettings(View.GONE,View.GONE,View.GONE,View.VISIBLE);
                                }
                                else {
                                    for (Article a : response.getArticles()) {
                                        articles.add(a);
                                    }
                                    newsHighlightAdptr.notifyDataSetChanged();
                                    visibilitySettings(View.GONE, View.GONE, View.VISIBLE, View.GONE);
                                }
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                visibilitySettings(View.GONE,View.GONE,View.GONE,View.VISIBLE);
                                Log.d("TAG",throwable.getMessage());
                            }
                        }
                );
            }
        },3000);


    }
    void visibilitySettings(int progress,int shimmer,int recycle,int error){
        binder.progressHorizontal.setVisibility(progress);
        binder.shimmer.setVisibility(shimmer);
        binder.newscycle.setVisibility(recycle);
        binder.nodatafoundanim.setVisibility(error);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        RadioButton rbtn=radioGroup.findViewById(i);
        if(rbtn.isChecked()) {
            visibilitySettings(View.VISIBLE,View.VISIBLE,View.GONE,View.GONE);
            binder.shimmer.startShimmer();
            articles.clear();
            loadNews("en", rbtn.getText().toString(),binder.searchView.getQuery().toString()==null?null:binder.searchView.getQuery().toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder=FragmentHomeBinding.inflate(inflater,container,false);

        TextView searchText = (TextView) binder.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        Typeface myCustomFont = ResourcesCompat.getFont(getActivity().getBaseContext(),R.font.poppins_regular);
        searchText.setTypeface(myCustomFont);
        searchText.setTextSize(15);
        //view=inflater.inflate(R.layout.fragment_home, container, false);

        newsApiClient = new NewsApiClient("9ba238cb5a5845ffa8ab94ef3e55bb6b");
        articles=new ArrayList<>();
        newsHighlightAdptr=new NewsHighlightAdptr(articles,mcontext);
        binder.newscycle.setLayoutManager(new LinearLayoutManager(mcontext));
        binder.newscycle.setAdapter(newsHighlightAdptr);

        visibilitySettings(View.VISIBLE,View.VISIBLE,View.GONE,View.GONE);
        binder.shimmer.startShimmer();

//        binder.btmNav.add(new MeowBottomNavigation.Model(TAB1, R.drawable.icnhome));
//        binder.btmNav.add(new MeowBottomNavigation.Model(TAB2, R.drawable.icnprofile));
//        binder.btmNav.add(new MeowBottomNavigation.Model(TAB3, R.drawable.icncommunity));
//        binder.btmNav.add(new MeowBottomNavigation.Model(TAB4, R.drawable.icnsetting));

        loadNews("en",null,null);

        binder.rbngrp.setOnCheckedChangeListener(this);

        binder.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                visibilitySettings(View.VISIBLE,View.VISIBLE,View.GONE,View.GONE);
                binder.shimmer.startShimmer();
                RadioButton rbtn=(RadioButton)binder.getRoot().findViewById(binder.rbngrp.getCheckedRadioButtonId());
                articles.clear();
                loadNews("en",rbtn.getText().toString(),query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return binder.getRoot();
    }
}