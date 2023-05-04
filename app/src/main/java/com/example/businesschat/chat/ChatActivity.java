package com.example.businesschat.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businesschat.R;
import com.example.businesschat.SelectProductActivity;
import com.example.businesschat.adapters.MessageAdapter;
import com.example.businesschat.fragments.OrdersFragment;
import com.example.businesschat.models.Messages;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String userName, imageUrl, reciverUID, senderUID;
    CircleImageView mCircleImageView;
    TextView userNameTextView;
    EditText messageEditText;
    ImageView backButton, callButton, emojiButton, attachmentButton, messageSendButton , productImageView ;
    String SenderRoom, ReciverRoom;
    Uri imgUri;

    //...

    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;

    RecyclerView messageAdapter;
    ArrayList<Messages> mMessagesArrayList;
    MessageAdapter adapter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //...
        attachmentButton = findViewById(R.id.addAttachmentBtn);

        userName = getIntent().getStringExtra("name");
        imageUrl = getIntent().getStringExtra("image");
        reciverUID = getIntent().getStringExtra("id");

        sharedPreferences = getApplication().getSharedPreferences("user", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        Log.i("Contacts", "onCreateView: Phone Number Check = "  + sharedPreferences.getString("phone", null));
        senderUID = sharedPreferences.getString("phone", null);

        mCircleImageView = findViewById(R.id.userImageView);
        userNameTextView = findViewById(R.id.userNameTextView);
        messageEditText = findViewById(R.id.messageEditText);
        messageSendButton = findViewById(R.id.messageSendBtn);

        SenderRoom = senderUID + reciverUID;
        ReciverRoom = reciverUID + senderUID;


        messageAdapter = findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        mMessagesArrayList = new ArrayList<>();
        adapter = new MessageAdapter(ChatActivity.this, mMessagesArrayList, SenderRoom, ReciverRoom);
        messageAdapter.setAdapter(adapter);

        userNameTextView.setText(userName);
        Picasso.get().load(imageUrl).into(mCircleImageView);


        DatabaseReference chatReference = mDatabase.getReference().child("chats").child(SenderRoom).child("messages");

        // get chats from firebase
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Messages messages = new Messages();
                    messages.setKey(dataSnapshot.getKey());
                    messages.setMessage(dataSnapshot.child("message").getValue(String.class));
                    messages.setSenderId(dataSnapshot.child("senderId").getValue(String.class));
                    messages.setType(dataSnapshot.child("type").getValue(String.class));
                    messages.setPrice(dataSnapshot.child("price").getValue(String.class));
                    messages.setProductName(dataSnapshot.child("productName").getValue(String.class));
                    messages.setDescription(dataSnapshot.child("description").getValue(String.class));
                    messages.setImg(dataSnapshot.child("img").getValue(String.class));
                    if (dataSnapshot.child("timeStamp").getValue(Long.class) != null ){
                        messages.setTimeStamp(dataSnapshot.child("timeStamp").getValue(Long.class));
                    }else{
                        messages.setTimeStamp(00);
                    }

                    messages.setBankSlipImg(dataSnapshot.child("bankSlipImg").getValue(String.class));
                    messages.setDepositAmount(dataSnapshot.child("depositAmount").getValue(String.class));
                    messages.setDepositBank(dataSnapshot.child("depositBank").getValue(String.class));
                    messages.setAccountHolderName(dataSnapshot.child("accountHolderName").getValue(String.class));
                    messages.setAccountNumber(dataSnapshot.child("accountNumber").getValue(String.class));
                    messages.setIsValid(dataSnapshot.child("isValid").getValue(String.class));

                    mMessagesArrayList.add(messages);
                    Log.d("Chats", "onDataChange: message length " + mMessagesArrayList.size());
                    Log.d("Chats", "onDataChange: message length " + mMessagesArrayList.get(0).getMessage());
                    messageAdapter.smoothScrollToPosition(messageAdapter.getBottom());
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = messageEditText.getText().toString();

                if (msg.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Please Enter Valid Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                messageEditText.setText("");
                mMessagesArrayList.clear();
                Date date = new Date();
                Messages messages = new Messages("",msg, senderUID, "1","","","","","","","","","","",date.getTime());

                mDatabase.getReference().child("chats").
                        child(SenderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mDatabase.getReference().child("chats").
                                        child(ReciverRoom)
                                        .child("messages")
                                        .push()
                                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });
            }
        });



        //.... attachment on click

        attachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ChatActivity.this);
                bottomSheetDialog.setContentView(R.layout.chat_layout_bottom_sheet);

                ImageView imageView = bottomSheetDialog.findViewById(R.id.Product);
                bottomSheetDialog.show();

                AddQuot(bottomSheetDialog, imageView);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.REQUEST_CODE) {
            imgUri = data.getData();
            if (imgUri != null) {
                InputImage inputImage = null;
                try {
                    inputImage = InputImage.fromFilePath(this, imgUri);
                    Bitmap bitmap=  inputImage.getBitmapInternal();
                    productImageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(this, "Error! Image not selected", Toast.LENGTH_SHORT).show();
            }


        }else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void AddQuot(BottomSheetDialog bottomSheetDialog, ImageView imageView) {


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =  new Intent(ChatActivity.this, SelectProductActivity.class);
                intent.putExtra("senderRoom", SenderRoom);
                intent.putExtra("reciverRoom", ReciverRoom);
                startActivity(intent);

                bottomSheetDialog.dismiss();


            }
        });
    }
}