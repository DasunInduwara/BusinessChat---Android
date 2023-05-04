package com.example.businesschat.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.businesschat.R;
import com.example.businesschat.chat.ChatActivity;
import com.example.businesschat.models.ContactsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    Context mContext;
    ArrayList<ContactsModel> mArrayList;

    public ContactListAdapter(Context context, ArrayList<ContactsModel> arrayList) {
        this.mContext = context;
        this.mArrayList = arrayList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_contact_recycler, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ContactsModel contactsModel = mArrayList.get(position);
        Log.i("AdapterView", "onBindViewHolder: email = " + contactsModel.getContactName());
        holder.messageTime.setText(contactsModel.getMessageTime());
        Picasso.get()
                .load(contactsModel.getImageUrl())
                .placeholder(R.drawable.businessman)
                .into(holder.userImage);

        holder.userName.setText(contactsModel.getContactName());
        holder.messageTime.setText(contactsModel.getMessageTime());
        holder.lastMessage.setText(contactsModel.getLastMessage());
        holder.lastMessageCount.setText(contactsModel.getUnseenMessages());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("name", contactsModel.getContactName());
                intent.putExtra("image", contactsModel.getImageUrl());
                intent.putExtra("id", contactsModel.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView userImage;
        public TextView userName, lastMessage, lastMessageCount, messageTime;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            lastMessageCount = itemView.findViewById(R.id.unseenMessageCount);
            messageTime = itemView.findViewById(R.id.messageTime);
        }
    }
}
