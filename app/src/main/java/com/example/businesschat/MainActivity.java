package com.example.businesschat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.businesschat.databinding.ActivityMainBinding;
import com.example.businesschat.fragments.ContactFragment;
import com.example.businesschat.fragments.OrdersFragment;
import com.example.businesschat.fragments.AddProductFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        replaceFragment(new ContactFragment());
        mBinding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.chat:
                    replaceFragment(new ContactFragment());
                    break;
                case R.id.add:
                    replaceFragment(new AddProductFragment());
                    break;
                case R.id.orders:
                    replaceFragment(new OrdersFragment());
                    break;
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}