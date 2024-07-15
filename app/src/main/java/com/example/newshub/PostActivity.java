package com.example.newshub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.newshub.databinding.ActivityPostBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class PostActivity extends AppCompatActivity implements TextWatcher{
    ActivityPostBinding binding;
    Uri fileUri;
    File file;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference,databaseReferenceCommunity;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        sharedPreferences=getSharedPreferences("userDetails",MODE_PRIVATE);
        binding.txtuserid.setText(sharedPreferences.getString("userName",null));

        databaseReference= FirebaseDatabase.getInstance().getReference("UserAccounts").child(sharedPreferences.getString("userName",null));
        databaseReferenceCommunity=FirebaseDatabase.getInstance().getReference("Community");
        storageReference= FirebaseStorage.getInstance().getReference("Posts");

        binding.txtdecrpt.addTextChangedListener(this);
        binding.txttitle.addTextChangedListener(this);
    }

    public void onClickUploadPic(View v){
        binding.layoutcam.setVisibility(View.VISIBLE);
        binding.layoutgal.setVisibility(View.VISIBLE);
    }
    public void onClickCamOrGal(View v){
        if(v.getId()==binding.btncam.getId()){
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            file = new File(getExternalCacheDir(),
                    String.valueOf(System.currentTimeMillis()) + ".jpg");
            fileUri = Uri.fromFile(file);
            camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(camera_intent, 1);
        }
        else{
            Intent i=new Intent();
            i.setAction(Intent.ACTION_GET_CONTENT);
            i.setType("image/*");
            startActivityForResult(i,2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==2)
                fileUri=data.getData();
            binding.layoutcam.setVisibility(View.GONE);
            binding.layoutgal.setVisibility(View.GONE);
            binding.imgviewincident.setImageURI(fileUri);
            binding.imgviewincident.setAdjustViewBounds(true);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(binding.txttitle.getText().length()>0&&binding.txtdecrpt.getText().length()>0)
            binding.btnpost.setVisibility(View.VISIBLE);
        else
            binding.btnpost.setVisibility(View.GONE);
    }

    public void onClickPost(View v){
        binding.txttitle.setEnabled(false);
        binding.txtdecrpt.setEnabled(false);
        v.setEnabled(false);
        binding.imgviewincident.setEnabled(false);
        binding.progresspost.setVisibility(View.VISIBLE);

        Date date=new Date();
        String key="Post"+System.currentTimeMillis()+new Random().nextInt();
        storageReference.child(key).putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(key).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("imgurl",uri.toString());
                        map.put("title",binding.txttitle.getText().toString());
                        map.put("description",binding.txtdecrpt.getText().toString());
                        String date="recently";
                        if(binding.checkdatetime.isChecked())
                            date=new SimpleDateFormat("dd MMM yyyy HH:mm:ss z").format(new Date());
                        map.put("datetime",date);

                        databaseReference.child("Post").child(key).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        databaseReference.child("postCount").setValue((long)snapshot.child("postCount").getValue()+1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                HashMap<String,Object> m=new HashMap<>();
                                m.put(databaseReference.getKey(),"");
                                databaseReferenceCommunity.updateChildren(m);
                                Toast.makeText(PostActivity.this, "Post Uploaded successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }
        })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        Toast.makeText(PostActivity.this, "OnProgress", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG",e.getMessage());
                    }
                });


    }
}