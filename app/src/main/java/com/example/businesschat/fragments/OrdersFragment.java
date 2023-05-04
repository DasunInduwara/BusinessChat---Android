package com.example.businesschat.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.businesschat.R;
import com.example.businesschat.adapters.ProductAdapter;
import com.example.businesschat.models.ProductModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    FirebaseDatabase mDatabase;
    RecyclerView productListRecycleView;
    DatabaseReference mDatabaseReference;
    ProductAdapter mProductAdapter;
    ArrayList<ProductModel> mProductModelList;
    SharedPreferences sharedPreferences;
    String userContactNo = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_orders, container, false);


        sharedPreferences = requireContext().getSharedPreferences("user", MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("stores");
        userContactNo = sharedPreferences.getString("phone", null);


        SetUpUI(v);
        return v;
    }

    private void SetUpUI(View v) {
        mProductModelList = new ArrayList<>();
        productListRecycleView = v.findViewById(R.id.productRecyclerView);
        productListRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mProductAdapter =  new ProductAdapter(requireContext(), mProductModelList);
        productListRecycleView.setAdapter(mProductAdapter);

        mDatabaseReference.child(userContactNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.exists()){
                            ProductModel model = dataSnapshot.getValue(ProductModel.class);

                            Log.i("Product", "onDataChange: name" + model.getProductName());
                            mProductModelList.add(model);
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