package com.example.newshub.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newshub.FullNewsScreen;
import com.example.newshub.R;
import com.kwabenaberko.newsapilib.models.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsHighlightAdptr extends RecyclerView.Adapter<NewsHighlightAdptr.ViewHolder>{
    ArrayList<Article> articles;
    Context mcontex;

    public NewsHighlightAdptr(ArrayList<Article> articles, Context mcontex) {
        this.articles = articles;
        this.mcontex = mcontex;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mcontex).inflate(R.layout.news_tumbnails,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txttitle.setText(articles.get(position).getTitle());
        holder.txtsrc.setText(articles.get(position).getSource().getName());
        Picasso.get()
                .load(articles.get(position).getUrlToImage())
                .placeholder(R.drawable.imgload)
                .error(R.drawable.imgnotsupport)
                .fit()
                .into(holder.imgview);
        holder.itemView.setOnClickListener(view -> {
            Intent i=new Intent(view.getContext(),FullNewsScreen.class);
            i.putExtra("url",articles.get(position).getUrl());
            view.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgview;
        TextView txttitle,txtsrc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgview=itemView.findViewById(R.id.newsimg);
            txtsrc=itemView.findViewById(R.id.txtsrc);
            txttitle=itemView.findViewById(R.id.txttitle);
        }
    }
}
