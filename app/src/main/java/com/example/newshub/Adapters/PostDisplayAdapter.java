package com.example.newshub.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newshub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostDisplayAdapter extends RecyclerView.Adapter<PostDisplayAdapter.PostDisplayHolder> {
    ArrayList<PostData> arrayList;
    Context mcontext;
    OnClickPostListener onClickPostListener;
    int tab;

    public interface OnClickPostListener{
        public void onClickPost(int position);
    }

    public void setOnClickPostListener(OnClickPostListener onClickPostListener){
        this.onClickPostListener=onClickPostListener;
    }

    public PostDisplayAdapter(ArrayList<PostData> arrayList, Context mcontext,int tab) {
        this.arrayList = arrayList;
        this.mcontext = mcontext;
        this.tab=tab;
    }

    @NonNull
    @Override
    public PostDisplayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostDisplayHolder(LayoutInflater.from(mcontext).inflate(R.layout.layout_post_view,parent,false),onClickPostListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostDisplayHolder holder, int position) {
        Picasso.get().load(arrayList.get(position).uimgurl).error(R.drawable.icnprofile).placeholder(R.drawable.icnprofile).into(holder.imgUser);
        Picasso.get().load(arrayList.get(position).incimgurl).error(R.drawable.imgload).into(holder.imgins);
        holder.txtuid.setText(arrayList.get(position).uid);
        holder.txtdate.setText(arrayList.get(position).date);
        holder.txttitle.setText(arrayList.get(position).title);
        holder.txtdesc.setText(arrayList.get(position).description);

        holder.btnlike.setOnClickListener(view -> {
            ImageButton btn= (ImageButton) view;
            if(btn.getTag().toString().equals("false")) {
                btn.setImageResource(R.drawable.icnliked);
                btn.setTag("true");
                holder.txtlcount.setText("1");
            }
            else {
                btn.setImageResource(R.drawable.icnlike);
                btn.setTag("false");
                holder.txtlcount.setText("0");
            }

        });

        if (tab==3){
            holder.btndlt.setVisibility(View.GONE);
        }
        else
            holder.btndlt.setOnClickListener(view -> {
                onClickPostListener.onClickPost(position);
            });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class PostDisplayHolder extends RecyclerView.ViewHolder{
        ImageView imgUser,imgins;
        ImageButton btnlike,btnsave,btnshare,btndlt;
        TextView txtuid,txtdate,txttitle,txtdesc,txtlcount;
        public PostDisplayHolder(@NonNull View itemView,OnClickPostListener onClickPostListener) {
            super(itemView);
            imgUser=itemView.findViewById(R.id.imgprofile);
            btnlike=itemView.findViewById(R.id.btnlike);
            btnsave=itemView.findViewById(R.id.btnsave);
            btnshare=itemView.findViewById(R.id.btnshare);
            txtuid=itemView.findViewById(R.id.txtuid);
            txtdate=itemView.findViewById(R.id.txtdate);
            txttitle=itemView.findViewById(R.id.txttitle);
            txtdesc=itemView.findViewById(R.id.txtdescription);
            imgins=itemView.findViewById(R.id.imgincident);
            btndlt=itemView.findViewById(R.id.btndlt);
            txtlcount=itemView.findViewById(R.id.txtlikecount);
        }
    }
}
