package com.example.businesschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.businesschat.adapters.SelectProductAdapter;
import com.example.businesschat.chat.ChatActivity;
import com.example.businesschat.models.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectProductActivity extends AppCompatActivity {
    ArrayList<ProductModel> mModelArrayList;
    FirebaseDatabase mDatabase;
    DatabaseReference mDatabaseReference;
    RecyclerView mRecyclerView;
    SelectProductAdapter mProductAdapter;
    SharedPreferences sharedPreferences;
    String userContactNo = "", sender, reciver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);

        sender = getIntent().getStringExtra("senderRoom");
        reciver = getIntent().getStringExtra("reciverRoom");

        mModelArrayList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.productRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mProductAdapter = new SelectProductAdapter(SelectProductActivity.this, mModelArrayList, sender, reciver);
        mRecyclerView.setAdapter(mProductAdapter);

        sharedPreferences = getApplication().getSharedPreferences("user", MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("stores");
        userContactNo = sharedPreferences.getString("phone", null);



        mDatabaseReference.child(userContactNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.exists()){
                            ProductModel model = dataSnapshot.getValue(ProductModel.class);

                            Log.i("Product", "onDataChange: name" + model.getProductName());
                            mModelArrayList.add(model);
                        }
                    }
                    mProductAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}