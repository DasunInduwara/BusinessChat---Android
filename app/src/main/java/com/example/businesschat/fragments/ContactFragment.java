package com.example.businesschat.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.businesschat.R;
import com.example.businesschat.adapters.ContactListAdapter;
import com.example.businesschat.models.ContactsModel;
import com.example.businesschat.profileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ContactFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    RecyclerView contactListRecycleView;
    ContactListAdapter contactListAdapter;
    DatabaseReference mDatabaseReference;
    ImageButton mImageButton;
    ArrayList<ContactsModel> mContactsModelArrayList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    String userContactNo = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_list, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("Users");



        sharedPreferences = requireContext().getSharedPreferences("user", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        Log.i("Contacts", "onCreateView: Phone Number Check = "  + sharedPreferences.getString("phone", null));
        userContactNo = sharedPreferences.getString("phone", null);

        SetUpUI(v);
        return v;
    }

    private void SetUpUI(View v) {
        contactListRecycleView = v.findViewById(R.id.chatRecyclerView);
        contactListRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mContactsModelArrayList = new ArrayList<>();
        contactListAdapter = new ContactListAdapter(getContext(), mContactsModelArrayList);
        mImageButton = v.findViewById(R.id.settingImgBtn);
        contactListRecycleView.setAdapter(contactListAdapter);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), profileActivity.class);
                startActivity(intent);
            }
        });

        GetUserContacts(v);
    }

    private void GetUserContacts(View v) {

        // get contacts from firebase
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.exists()){
                            Log.e("Users", "onDataChange Check Key: " + dataSnapshot.getKey());
                            if (!userContactNo.equals(dataSnapshot.getKey().toString())){
                                ContactsModel contactsModel= new ContactsModel();

                                Log.i("Users", "onDataChange Email: " + dataSnapshot.child("email").getValue(String.class));
                                contactsModel.setContactName(dataSnapshot.child("displayName").getValue(String.class));
                                contactsModel.setId(dataSnapshot.getKey());
                                contactsModel.setImageUrl(dataSnapshot.child("imgUrl").getValue(String.class));
                                contactsModel.setMessageTime("20.21");
                                contactsModel.setUnseenMessages("1");
                                contactsModel.setLastMessage("this is last Message");
                                mContactsModelArrayList.add(contactsModel);
                            }
                        }

                    }
                    contactListAdapter.notifyDataSetChanged();
                }else{
                    Log.i("TAG", "onDataChange: snapshot empty");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}