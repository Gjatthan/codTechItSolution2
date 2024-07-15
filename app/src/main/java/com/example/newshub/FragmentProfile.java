package com.example.newshub;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newshub.Adapters.PostData;
import com.example.newshub.Adapters.PostDisplayAdapter;
import com.example.newshub.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentProfile extends Fragment implements View.OnClickListener {
    Context mcontext;
    FragmentProfileBinding binding;
    Uri imageUri;
    DatabaseReference firebase;
    StorageReference storageReference;
    SharedPreferences sharedPreferences;
    boolean isMenuOpen=false;
    PostDisplayAdapter adapter;
    ArrayList<PostData> postDataArrayList;

    public FragmentProfile(Context mcontext) {
        this.mcontext = mcontext;
        postDataArrayList=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentProfileBinding.inflate(inflater,container,false);
        binding.btnedit.setOnClickListener(this);

        adapter=new PostDisplayAdapter(postDataArrayList,binding.getRoot().getContext(),2);

        sharedPreferences=mcontext.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
        firebase= FirebaseDatabase.getInstance().getReference("UserAccounts").child(sharedPreferences.getString("userName",null));
        storageReference= FirebaseStorage.getInstance().getReference("pics").child(sharedPreferences.getString("userName",null));

        binding.photoviewrecycle.setLayoutManager(new LinearLayoutManager(mcontext));
        binding.photoviewrecycle.setAdapter(adapter);

        adapter.setOnClickPostListener(new PostDisplayAdapter.OnClickPostListener() {
            @Override
            public void onClickPost(int position) {
                binding.progressCircularDeletePost.setVisibility(View.VISIBLE);
                new AlertDialog.Builder(mcontext)
                        .setTitle("NewsHub")
                        .setMessage("Do you really want to delete the post?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new Handler().postDelayed(()->{
                                    firebase.child("Post").child(postDataArrayList.get(position).parent).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            postDataArrayList.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            binding.progressCircularDeletePost.setVisibility(View.GONE);
                                            long count=Long.parseLong(binding.txttotalpost.getText().toString())-1;
                                            firebase.child("postCount").setValue(count);
                                            binding.txttotalpost.setText(Long.toString(count));
                                            Toast.makeText(mcontext, "Post Deleted Successfuly", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            binding.progressCircularDeletePost.setVisibility(View.GONE);
                                            Toast.makeText(mcontext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                },1000);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });
        loadDetails();

        binding.btnmore.setOnClickListener(view -> {
            if(isMenuOpen)
                binding.btnlout.setVisibility(View.GONE);
            else
                binding.btnlout.setVisibility(View.VISIBLE);
            isMenuOpen=!isMenuOpen;
        });

        binding.btnlout.setOnClickListener(view -> {
            new AlertDialog.Builder(mcontext)
                    .setTitle("NewsHub")
                    .setMessage("Do you really want to Logout?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            sharedPreferences.edit().putBoolean("Login",false).apply();
                            startActivity(new Intent(getActivity().getApplicationContext(),LogSignIn.class));
                            getActivity().finish();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        });

        loadAllPost();

        return binding.getRoot();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        loadAllPost();
//    }

    private void loadDetails() {
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("profile"))
                    Picasso.get().load(snapshot.child("profile").getValue().toString()).fit().into(binding.imgprofile);
                else
                    binding.imgprofile.setImageResource(R.drawable.icnprofile);
                binding.txtgend.setText(snapshot.child("gender").getValue().toString());
                binding.uname.setText(snapshot.child("userName").getValue().toString());
                binding.txttotalpost.setText(snapshot.child("postCount").getValue().toString());
                if((long)snapshot.child("postCount").getValue()<=0){
                    binding.photoviewrecycle.setVisibility(View.GONE);
                    binding.imggif.setVisibility(View.VISIBLE);
                    Glide.with(mcontext).load(R.raw.no_post).into(binding.imggif);
                }
                else {
                    binding.photoviewrecycle.setVisibility(View.VISIBLE);
                    binding.imggif.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent galleryOpen=new Intent();
        galleryOpen.setAction(Intent.ACTION_GET_CONTENT);
        galleryOpen.setType("image/*");
        startActivityForResult(galleryOpen,2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2&&resultCode==RESULT_OK&&data!=null){
            //binding.imgprofile.setImageURI(data.getData());

            storageReference.child("profilePic").putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child("profilePic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String,Object> map=new HashMap<>();
                            map.put("profile",uri.toString());
                            firebase.updateChildren(map);
                            firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Picasso.get().load(snapshot.child("profile").getValue().toString()).fit().into(binding.imgprofile);
                                    setVisibility(View.GONE,View.VISIBLE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            setVisibility(View.VISIBLE,View.GONE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            setVisibility(View.GONE,View.VISIBLE);
                            Toast.makeText(mcontext, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    void setVisibility(int progress,int edt){
        binding.progressimgload.setVisibility(progress);
        binding.imgprofile.setVisibility(edt);
        binding.btnedit.setVisibility(edt);
    }

    void loadAllPost(){
        postDataArrayList.clear();
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uimg;
                if(snapshot.hasChild("profile"))
                    uimg=snapshot.child("profile").getValue().toString();
                else
                    uimg=String.valueOf(R.drawable.icnprofile);
                if(snapshot.hasChild("Post")){
                    for(DataSnapshot post:snapshot.child("Post").getChildren()){
                        PostData postData=new PostData(
                            uimg,
                                sharedPreferences.getString("userName",null),
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}