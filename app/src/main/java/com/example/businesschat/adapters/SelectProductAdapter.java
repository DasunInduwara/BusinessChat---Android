package com.example.businesschat.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.businesschat.R;
import com.example.businesschat.models.Messages;
import com.example.businesschat.models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;


public class SelectProductAdapter extends RecyclerView.Adapter<SelectProductAdapter.SelectProductViewHolder>{
    Context mContext;
    ArrayList<ProductModel> mArrayList;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor myEdit;
    FirebaseDatabase mDatabase;
    String senderUID;
    String SenderRoom;
    String ReciverRoom;

    public SelectProductAdapter(Context context, ArrayList<ProductModel> arrayList, String SenderRoom, String ReciverRoom) {
        this.mContext = context;
        this.mArrayList = arrayList;
        this.SenderRoom = SenderRoom;
        this.ReciverRoom = ReciverRoom;
    }

    @NonNull
    @Override
    public SelectProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mDatabase = FirebaseDatabase.getInstance();
        mSharedPreferences = mContext.getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        myEdit = mSharedPreferences.edit();

        senderUID = mSharedPreferences.getString("phone", null);


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_select_product_recyler_item_view, parent, false);
        return new SelectProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectProductViewHolder holder, int position) {
        ProductModel productModel = mArrayList.get(position);
        holder.productDescription.setText(productModel.getProductDescription());
        holder.productName.setText(productModel.getProductName());
        holder.productPrice.setText(productModel.getProductPrice());

        String base64String = productModel.getProductImg();
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        holder.mImageView.setImageBitmap(decodedBitmap);

        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("myChildNode").push();
                String key = ref.getKey();


                Date date = new Date();
                Messages messages = new Messages("","", senderUID, "2",productModel.getProductPrice(),productModel.getProductName(),productModel.getProductDescription(),productModel.getProductImg(),"","","","","","",date.getTime());
                mDatabase.getReference().child("chats").
                        child(SenderRoom)
                        .child("messages").child(key)
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mDatabase.getReference().child("chats").
                                        child(ReciverRoom)
                                        .child("messages").child(key)
                                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                ((Activity) mContext).finish();
                                                ((Activity) mContext).finish();
                                            }
                                        });
                            }
                        });

            }
        });


    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public static class SelectProductViewHolder  extends RecyclerView.ViewHolder {
        TextView productName, productDescription, productPrice;
        ImageView mImageView;
        AppCompatButton mButton;
        public SelectProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productNameText);
            productDescription = itemView.findViewById(R.id.productDescriptionText);
            productPrice = itemView.findViewById(R.id.productPriceText);
            mButton = itemView.findViewById(R.id.sendBtn);
            mImageView = itemView.findViewById(R.id.imageViewProduct);
        }
    }
}


