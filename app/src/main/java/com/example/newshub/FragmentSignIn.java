package com.example.newshub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.newshub.databinding.FragmentSignInBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FragmentSignIn extends Fragment implements View.OnClickListener {
    FragmentSignInBinding binding;
    Context mcontext;
    DatabaseReference firebase,user_ac;
    SharedPreferences userDetails;

    public FragmentSignIn(Context mcontext) {
        this.mcontext = mcontext;
        userDetails= mcontext.getSharedPreferences("userDetails",Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentSignInBinding.inflate(inflater,container,false);

        binding.btnsignin.setOnClickListener(this);

        firebase=FirebaseDatabase.getInstance().getReference("UserCredentials");
        user_ac=FirebaseDatabase.getInstance().getReference("UserAccounts");

        binding.txtscpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!binding.txtspass.getText().toString().equals(editable.toString())) {
                    binding.txtscpass.setError("Password Missmatch");
                    binding.btnsignin.setVisibility(View.GONE);
                }
                else
                    binding.btnsignin.setVisibility(View.VISIBLE);

            }
        });
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName=binding.txtsusername.getText().toString();
                if(snapshot.hasChild(userName))
                    binding.txtsusername.setError("Username Already exists");
                else{
                    HashMap<String,Object> map=new HashMap<>();
                    map.put(userName,binding.txtscpass.getText().toString());
                    firebase.updateChildren(map);

                    userDetails
                            .edit()
                            .putBoolean("Login",true)
                            .putString("userName",userName)
                            .apply();
                    RadioButton radgend=binding.getRoot().findViewById(binding.radgend.getCheckedRadioButtonId());
                    HashMap<String,Object> usercred=new HashMap<>();
                    usercred.put("userName",userName);
                    usercred.put("gender",radgend.getText());
                    usercred.put("postCount",0);
                    usercred.put("community",false);
                    user_ac.child(userName).updateChildren(usercred);
                    startActivity(new Intent(mcontext,HomeScreen.class));
                    getActivity().finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}