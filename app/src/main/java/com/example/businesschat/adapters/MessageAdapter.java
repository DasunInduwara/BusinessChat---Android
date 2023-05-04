package com.example.businesschat.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.businesschat.R;
import com.example.businesschat.chat.BankTransferActivity;
import com.example.businesschat.chat.ChatActivity;
import com.example.businesschat.models.Messages;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter {
    Context mContext;
    String senderRoom;
    String reciverRoom;
    ArrayList<Messages> mArrayList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    String userContactNo = "";
    Messages messages;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;
    int QUOT_SEND = 3;
    int QUOT_RECEIVE = 4;

    public MessageAdapter(Context context, ArrayList<Messages> arrayList, String senderRoom, String reciverRoom) {
        this.mContext = context;
        this.mArrayList = arrayList;
        this.senderRoom = senderRoom;
        this.reciverRoom = reciverRoom;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        sharedPreferences = mContext.getSharedPreferences("user", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        Log.i("Contacts", "onCreateView: Phone Number Check = "  + sharedPreferences.getString("phone", null));
        userContactNo = sharedPreferences.getString("phone", null);

        if (viewType == ITEM_SEND){
            View view = LayoutInflater.from(mContext).inflate(R.layout.custom_send_message_recycler, parent, false);
            return new SenderViewHolder(view);
        }else if (viewType == ITEM_RECEIVE){
            View view = LayoutInflater.from(mContext).inflate(R.layout.custom_recive_message_recycler, parent, false);
            return  new ReciverViewHolder(view);
        }else if (viewType == QUOT_SEND){
            View view = LayoutInflater.from(mContext).inflate(R.layout.custom_send_quate_recycler, parent, false);
            return  new QuotSenderViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.custom_recive_quate_recycler, parent, false);
            return  new QuotReciverViewHolder(view);
        }
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        messages = mArrayList.get(position);
        Log.i("Chats", "onDataChange: messages"  +messages.getMessage());
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            senderViewHolder.textMessage.setText(messages.getMessage());
            senderViewHolder.messageTime.setText("16.03");
        } else if (holder.getClass() == QuotSenderViewHolder.class){
            QuotSenderViewHolder quotSenderViewHolder = (QuotSenderViewHolder) holder;
            quotSenderViewHolder.productName.setText(messages.getProductName());
            quotSenderViewHolder.description.setText(messages.getDescription());
            quotSenderViewHolder.productName.setText(messages.getProductName());
            quotSenderViewHolder.price.setText("Rs: " +messages.getPrice());


            String base64String = messages.getImg();
            if (!base64String.isEmpty()){
                byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                quotSenderViewHolder.mImageView.setImageBitmap(decodedBitmap);
            }

            String bName = "Bank of Ceylon";
            String bNum = "000080765480";
            String name = "MR MAT Perera";

            if (messages != null){
                if (messages.getIsValid().equals("true")){
                    quotSenderViewHolder.paymentStatusButton.setVisibility(View.VISIBLE);
                }else if (messages.getIsValid().equals("false")){
                    quotSenderViewHolder.paymentStatusButton.setText("Invalid Payment");
                    quotSenderViewHolder.paymentStatusButton.setTextColor(Color.RED);
                    quotSenderViewHolder.paymentStatusButton.setVisibility(View.VISIBLE);
                }else{
                    if (!messages.getAccountNumber().isEmpty() && !messages.getDepositBank().isEmpty() && !messages.getAccountHolderName().isEmpty()){
                        if (bNum.equals(messages.getAccountNumber()) &&
                                bName.toUpperCase().equals(messages.getDepositBank()) &&
                                name.toUpperCase().replaceAll("\\s", "").contains(messages.getAccountHolderName().replaceAll("\\s", ""))) {
                            Log.i("Details", "QuotReciverViewHolder: Success ");
                            // do something if all three conditions are true

                            Map<String, Object> messageUpdates = new HashMap<>();
                            messageUpdates.put("isValid", "true");


                            mDatabase.getReference().child("chats").
                                    child(senderRoom)
                                    .child("messages").child(messages.getKey())
                                    .updateChildren(messageUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mDatabase.getReference().child("chats").
                                                    child(reciverRoom)
                                                    .child("messages").child(messages.getKey())
                                                    .updateChildren(messageUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                        }
                                                    });
                                        }
                                    });
                        }else {
                            Log.i("Details", "QuotReciverViewHolder: bName " + bName);
                            Log.i("Details", "QuotReciverViewHolder: bNum " + bNum);
                            Log.i("Details", "QuotReciverViewHolder: name " + name);

                            Map<String, Object> messageUpdates = new HashMap<>();
                            messageUpdates.put("isValid", "false");

                            mDatabase.getReference().child("chats").
                                    child(senderRoom)
                                    .child("messages").child(messages.getKey())
                                    .updateChildren(messageUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mDatabase.getReference().child("chats").
                                                    child(reciverRoom)
                                                    .child("messages").child(messages.getKey())
                                                    .updateChildren(messageUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
                }





            }





        }else if (holder.getClass() == QuotReciverViewHolder.class){
            QuotReciverViewHolder quotReciverViewHolder = (QuotReciverViewHolder) holder;
            quotReciverViewHolder.productName.setText(messages.getProductName());
            quotReciverViewHolder.description.setText(messages.getDescription());
            quotReciverViewHolder.price.setText(String.format("Rs: %s", messages.getPrice()));

            String base64String = messages.getImg();
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            quotReciverViewHolder.mImageView.setImageBitmap(decodedBitmap);


            if (messages != null){
                if (!messages.getAccountNumber().isEmpty() && messages.getIsValid().equals("")){
                    quotReciverViewHolder.cardPayBtn.setVisibility(View.GONE);
                    quotReciverViewHolder.bnkTransferBtn.setVisibility(View.GONE);
                }else if(messages.getIsValid().equals("false")){
                    quotReciverViewHolder.cardPayBtn.setVisibility(View.GONE);
                    quotReciverViewHolder.bnkTransferBtn.setVisibility(View.GONE);
                    quotReciverViewHolder.incompleteTextView.setVisibility(View.VISIBLE);
                }else if (messages.getIsValid().equals("true")){
                    quotReciverViewHolder.cardPayBtn.setVisibility(View.GONE);
                    quotReciverViewHolder.bnkTransferBtn.setVisibility(View.GONE);
                    quotReciverViewHolder.paymentSuccessTextView.setVisibility(View.VISIBLE);
                }

            }

        }else{
            ReciverViewHolder reciverViewHolder = (ReciverViewHolder) holder;
            reciverViewHolder.textMessage.setText(messages.getMessage());
            reciverViewHolder.messageTime.setText("16.03");
        }

    }

    @Override
    public int getItemCount() {
        if (mArrayList == null) {
            Log.d("Chats", "onDataChange: mArrayList is null");
        }else{
            Log.d("Chats", "onDataChange: mArrayList length " + mArrayList.size());
        }
        return mArrayList == null ? 0 : mArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = mArrayList.get(position);
        if (userContactNo.equals(messages.getSenderId())){
            if (messages.getType().equals("2")){
                return  QUOT_SEND;
            }else{
                return ITEM_SEND;
            }
        }else{
            if (messages.getType().equals("2")){
                return  QUOT_RECEIVE;
            }else{
                return ITEM_RECEIVE;
            }

        }
    }


    class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView textMessage, messageTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
            messageTime = itemView.findViewById(R.id.text_message_time);


        }
    }

    class ReciverViewHolder extends RecyclerView.ViewHolder{
        TextView textMessage, messageTime;
        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
            messageTime = itemView.findViewById(R.id.text_message_time);
        }
    }

    class QuotSenderViewHolder extends RecyclerView.ViewHolder{
        TextView productName, description, price;
        ImageView mImageView;
        AppCompatButton paymentStatusButton;
        public  QuotSenderViewHolder(@NonNull View iteView){
            super(iteView);
            price = iteView.findViewById(R.id.productSendPriceTextView);
            productName = iteView.findViewById(R.id.productSendNameTextView);
            description = iteView.findViewById(R.id.productSendDescriptionTextView);
            mImageView = iteView.findViewById(R.id.productImageView);
            paymentStatusButton = iteView.findViewById(R.id.paymentStatusButton);
        }
    }



    class QuotReciverViewHolder extends RecyclerView.ViewHolder{
        TextView productName, description, price, incompleteTextView, paymentSuccessTextView;
        AppCompatButton cardPayBtn, bnkTransferBtn;
        ImageView mImageView;
        public  QuotReciverViewHolder(@NonNull View iteView){
            super(iteView);
            productName = iteView.findViewById(R.id.productNameTextView);
            description = iteView.findViewById(R.id.productDescriptionTextView);
            cardPayBtn = iteView.findViewById(R.id.cardPayBtn);
            bnkTransferBtn = iteView.findViewById(R.id.bankTransferBtn);
            price = iteView.findViewById(R.id.productPriceTextView);
            incompleteTextView = iteView.findViewById(R.id.incompletePaymentText);
            paymentSuccessTextView = iteView.findViewById(R.id.paymentSuccessText);

            mImageView = iteView.findViewById(R.id.productImageView);

            String bName = "Bank of Ceylon";
            String bNum = "000080765480";
            String name = "M P G B Muthunayake";








            bnkTransferBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext.getApplicationContext(), BankTransferActivity.class);
                    intent.putExtra("senderRoom", senderRoom);
                    intent.putExtra("reciverRoom", reciverRoom);
                    intent.putExtra("message", messages);
                    view.getContext().startActivity(intent);

                }
            });



        }


    }






}
