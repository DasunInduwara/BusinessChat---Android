package com.example.businesschat.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.businesschat.R;
import com.example.businesschat.models.ProductModel;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    Context mContext;
    ArrayList<ProductModel> mArrayList;

    public ProductAdapter(Context context, ArrayList<ProductModel> arrayList) {
        this.mContext = context;
        this.mArrayList = arrayList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product_recycler_vew, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel productModel = mArrayList.get(position);
        holder.productPrice.setText(productModel.getProductPrice());
        holder.productName.setText(productModel.getProductName());
        holder.productDescription.setText(productModel.getProductDescription());
        String base64String = productModel.getProductImg();

        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        holder.mImageView.setImageBitmap(decodedBitmap);
    }


    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        ImageView mImageView;
        TextView productName, productPrice, productDescription;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageViewProduct);
            productDescription = itemView.findViewById(R.id.productDescriptionText);
            productName = itemView.findViewById(R.id.productNameText);
            productPrice = itemView.findViewById(R.id.productPriceText);
        }
    }
}
