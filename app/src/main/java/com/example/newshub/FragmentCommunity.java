package com.example.newshub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newshub.Adapters.PostData;
import com.example.newshub.Adapters.PostDisplayAdapter;
import com.example.newshub.databinding.FragmentCommunityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentCommunity extends Fragment {
    Context mcontext;
    FragmentCommunityBinding binding;
    DatabaseReference firebase,firebaseCommunity;
    SharedPreferences sharedPreferences;
    ArrayList<PostData> postDataArrayList;
    PostDisplayAdapter adapter;
    String username,imgUrl;

    public FragmentCommunity(Context mcontext) {
        this.mcontext = mcontext;
        sharedPreferences=mcontext.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
        username=sharedPreferences.getString("userName",null);
        firebase= FirebaseDatabase.getInstance().getReference("UserAccounts");
        firebaseCommunity=FirebaseDatabase.getInstance().getReference("Community");
        postDataArrayList=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentCommunityBinding.inflate(inflater,container,false);

        adapter=new PostDisplayAdapter(postDataArrayList,binding.getRoot().getContext(),3);
        binding.postLayout.setLayoutManager(new LinearLayoutManager(mcontext));
        binding.postLayout.setAdapter(adapter);
        firebase.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((boolean)snapshot.child("community").getValue()){
                   setVisibility(View.VISIBLE,View.GONE);
                   loadCommunityData();
                }
                else
                {
                    setVisibility(View.GONE,View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.btnjoin.setOnClickListener(view -> {
            firebase.child(username).child("community").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    loadCommunityData();
                }
            });
        });

        binding.btnleave.setOnClickListener(view -> {
            new AlertDialog.Builder(mcontext)
                    .setTitle("NewsHub")
                    .setMessage("Do you really want to leave the community?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (d,w)->{
                        setVisibility(View.GONE,View.VISIBLE);
                        firebaseCommunity.child(username).removeValue();
                        firebase.child(username).child("community").setValue(false);
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });

        return binding.getRoot();
    }

    private void loadCommunityData() {
        postDataArrayList.clear();
        setVisibility(View.VISIBLE,View.GONE);

        firebaseCommunity.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot member:snapshot.getChildren()){
                    if(!member.getKey().equals(username)){
                        firebase.child(member.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild("profile"))
                                    imgUrl=snapshot.child("profile").getValue().toString();
                                else
                                    imgUrl=String.valueOf(R.drawable.icnprofile);
                                for(DataSnapshot post:snapshot.child("Post").getChildren()){
                                    PostData postData=new PostData(
                                            imgUrl,
                                            member.getKey(),
                                            post.child("datetime").getValue().toString(),
                                            post.child("imgurl").getValue().toString(),
                                            post.child("title").getValue().toString(),
                                            post.child("description").getValue().toString(),
                                            post.getKey()
                                    );
                                    postDataArrayList.add(postData);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    void setVisibility(int v1,int v2){
        binding.cardView.setVisibility(v1);
        binding.postLayout.setVisibility(v1);
        binding.btnjoin.setVisibility(v2);
        binding.imgnews.setVisibility(v2);
    }
}