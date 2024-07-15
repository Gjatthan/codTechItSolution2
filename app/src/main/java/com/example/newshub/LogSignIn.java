package com.example.newshub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.CompoundButton;

import com.example.newshub.databinding.ActivityLogSignInBinding;
import com.google.firebase.FirebaseApp;

public class LogSignIn extends AppCompatActivity {

    ActivityLogSignInBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityLogSignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);

        replaceLayout(new FragmentLogIn(binding.getRoot().getContext()),"Don't have Account?\nCreate one.");

        binding.btnlogsign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b)
                    replaceLayout(new FragmentSignIn(binding.getRoot().getContext()),"Back to Login");
                else
                    replaceLayout(new FragmentLogIn(binding.getRoot().getContext()),"Don't have Account?\nCreate one.");
            }
        });
    }

    void replaceLayout(Fragment fragment,String text){
        getSupportFragmentManager().beginTransaction()
                .replace(binding.framelogsign.getId(),fragment)
                .setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out )
                .commit();
        binding.btnlogsign.setText(text);
    }
}