package com.example.businesschat.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.businesschat.R;
import com.example.businesschat.chat.BankTransferActivity;
import com.example.businesschat.models.ProductModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;


public class AddProductFragment extends Fragment {
    ImageView productImageView;
    AppCompatButton cameraBtn, saveBtn;
    EditText productName, productDescription, productPrice;
    SharedPreferences mSharedPreferences;
    String base64String, phoneNumber;
    FirebaseDatabase mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        SetUpUI(v);
        return v;
    }

    private void SetUpUI(View v) {
        mSharedPreferences = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        phoneNumber = mSharedPreferences.getString("phone","");
        productImageView = v.findViewById(R.id.productImageView);
        cameraBtn = v.findViewById(R.id.chooseImageBtn);
        saveBtn = v.findViewById(R.id.saveBtn);
        productName = v.findViewById(R.id.productNameEditText);
        productDescription = v.findViewById(R.id.productDescriptionEditText);
        productPrice = v.findViewById(R.id.productPriceEditText);
        mDatabase = FirebaseDatabase.getInstance();

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchImagePicker();
            }
        });


        // save product
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                // generate key
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("myChildNode").push();
                String key = ref.getKey();

                if (base64String == null && productName.getText().toString().isEmpty() && productPrice.getText().toString().isEmpty() && productDescription.getText().toString().isEmpty()){
                    Toast.makeText(requireActivity(), "Please enter all information's", Toast.LENGTH_SHORT).show();
                    productName.setError("Please enter name");
                    productPrice.setError("Please enter price");
                    productDescription.setError("Please enter description");
                }else {
                    ProductModel productModel = new ProductModel();
                    productModel.setProductImg(base64String);
                    productModel.setProductName(productName.getText().toString());
                    productModel.setProductPrice(productPrice.getText().toString());
                    productModel.setProductDescription(productDescription.getText().toString());

                    mDatabase.getReference().child("stores").
                            child(phoneNumber)
                            .child(key).setValue(productModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(requireContext(), "Product Save Successful", Toast.LENGTH_LONG).show();
                                        base64String = null;
                                        productImageView.setImageDrawable(getResources().getDrawable(R.drawable.list_product));
                                        productName.setText("");
                                        productPrice.setText("");
                                        productDescription.setText("");
                                    }else{
                                        Toast.makeText(requireContext(), "Product Save unsuccessful. Please try again", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                }
            }
        });
    }


    // In your Fragment's onCreate method
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                onActivityResult(ImagePicker.REQUEST_CODE, result.getResultCode(), result.getData());
            });

    // Call this method to launch the activity when needed
    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }

    // Call this method to handle the result
    private void handleImagePickerResult(Intent data) {
        Uri imgUri = data.getData();
        if (imgUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imgUri);
                productImageView.setImageBitmap(bitmap);
                base64String = encodeImageToBase64(bitmap);
                Log.d("Add Product", "Base64 encoded image: " + base64String);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(requireActivity(), "Error! Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Override this method to handle other results, if necessary
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == RESULT_OK) {
            handleImagePickerResult(data);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireActivity(), "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeImageToBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


}

