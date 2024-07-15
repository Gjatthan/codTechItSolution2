package com.example.newshub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newshub.databinding.FragmentLogInBinding;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class FragmentLogIn extends Fragment implements View.OnClickListener {

    FragmentLogInBinding binding;
    Context mcontext;
    DatabaseReference firebase;
    SharedPreferences userDetails;

    public FragmentLogIn(Context mcontext) {
        this.mcontext = mcontext;
        userDetails= mcontext.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentLogInBinding.inflate(inflater,container,false);
        firebase= FirebaseDatabase.getInstance().getReference("UserCredentials");
        binding.btnlogin.setOnClickListener(this);
        return binding.getRoot();
    }


    @Override
    public void onClick(View view) {
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(binding.txtusername.getText().toString())){
                    if(snapshot.child(binding.txtusername.getText().toString()).getValue().toString().equals(binding.txtpass.getText().toString())){
                        userDetails
                                .edit()
                                .putBoolean("Login",true)
                                .putString("userName",binding.txtusername.getText().toString())
                                .apply();
                        startActivity(new Intent(getContext(),HomeScreen.class));
                        getActivity().finish();
                    }
                    else
                        binding.txtpass.setError("Wrong Password");
                }
                else
                    binding.txtusername.setError("Invalid Username");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}