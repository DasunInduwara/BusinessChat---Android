package com.example.businesschat.chat;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businesschat.R;
import com.example.businesschat.models.Messages;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankTransferActivity extends AppCompatActivity {
    AppCompatButton attachBtn;
    ProgressBar mProgressBar;
    TextView mStatusTextView;
    ImageView mImageView;
    Uri imgUri;
    String senderRoom, reciverRoom;
    TextRecognizer mTextRecognizer;
    Messages mMessage;

    String bankName = null;
    String accountHolder = null;
    String accountNumber = null;
    String depositAmount = null;

    FirebaseDatabase mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_transfer);

        mDatabase = FirebaseDatabase.getInstance();

        senderRoom = getIntent().getStringExtra("senderRoom");
        reciverRoom = getIntent().getStringExtra("reciverRoom");
        mMessage = (Messages) getIntent().getSerializableExtra("message");
        Log.d("senderRoom", "onCreate: " + senderRoom + " / " + reciverRoom) ;

        attachBtn = findViewById(R.id.attachBtn);
        mImageView = findViewById(R.id.slipImageView);

        mStatusTextView = findViewById(R.id.statusTextView);
        mProgressBar = findViewById(R.id.progressBar);

        // Create a TextRecognizerOptions object with customized settings

         mTextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);


        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(BankTransferActivity.this)
                            .crop()
                            .compress(1024)
                            .maxResultSize(1080, 1080)
                            .start();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.REQUEST_CODE) {
            imgUri = data.getData();
            if (imgUri != null) {
                attachBtn.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                mStatusTextView.setVisibility(View.VISIBLE);
                recognizeText();
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

    private void recognizeText() {

        if (imgUri != null){
            try {
                InputImage inputImage = InputImage.fromFilePath(this, imgUri);
                Bitmap bitmap=  inputImage.getBitmapInternal();
                mImageView.setImageBitmap(bitmap);
//
                mStatusTextView.setText("Identifying...");

                Task<Text> result = mTextRecognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        // Get the recognized text as a single string
                        String textString = text.getText();
                        Log.d("Filter", "Result: " + textString);

                        // check bank type
                        Pattern BOC_bankPattern = Pattern.compile("(?i)bank of ceylon");
                        Pattern HNB_bankPattern = Pattern.compile("(?i)HNB");
                        // Extract the bank name using the bank pattern
                        Matcher bankMatcher = BOC_bankPattern.matcher(textString);
                        Matcher hndBankMatcher = HNB_bankPattern.matcher(textString);

                        if (bankMatcher.find()) {
                            bankName = bankMatcher.group();
                            BOCTextFiltering(text, textString);
                        }else if (hndBankMatcher.find()){
                            bankName = hndBankMatcher.group();
                        }
                        Log.d("Filter", "Bank name: " + bankName);






                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BankTransferActivity.this, "Failed to recognize text " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkExtractedData() {
        if (bankName == null ){
            mProgressBar.setVisibility(View.GONE);
            attachBtn.setVisibility(View.VISIBLE);
            mStatusTextView.setText("Can't identify bank name. Please try again");
        }else if (depositAmount == null) {
            mProgressBar.setVisibility(View.GONE);
            attachBtn.setVisibility(View.VISIBLE);
            mStatusTextView.setText("Can't identify deposit amount. Please try again");
        }else if (accountNumber == null){
            mProgressBar.setVisibility(View.GONE);
            attachBtn.setVisibility(View.VISIBLE);
            mStatusTextView.setText("Can't identify account number. Please try again");
        }else if (accountHolder == null){
            mProgressBar.setVisibility(View.GONE);
            attachBtn.setVisibility(View.VISIBLE);
            mStatusTextView.setText("Can't identify account holder name. Please try again");
        }else{

            mStatusTextView.setText("Information identifying success...");


            Map<String, Object> messageUpdates = new HashMap<>();
            messageUpdates.put("bankSlipImg", "");
            messageUpdates.put("depositAmount", depositAmount);
            messageUpdates.put("depositBank", bankName);
            messageUpdates.put("accountHolderName", accountHolder);
            messageUpdates.put("accountNumber", accountNumber);


            mDatabase.getReference().child("chats").
                    child(senderRoom)
                    .child("messages").child(mMessage.getKey())
                    .updateChildren(messageUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDatabase.getReference().child("chats").
                                    child(reciverRoom)
                                    .child("messages").child(mMessage.getKey())
                                    .updateChildren(messageUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });
                        }
                    });

            mStatusTextView.setText("Complete..");
            onBackPressed();
        }
    }

    private void BOCTextFiltering(Text text, String textString) {
        // Define regular expression patterns for the deposit amount, account number, account holder name, and bank name
        Pattern amountPattern = Pattern.compile("(?<=RS )[0-9., ]+");
        Pattern numberPattern = Pattern.compile("(?<=TO )[0-9]+");
        // Define the regular expression pattern to search for "MR" or "MRS"
        Pattern patternName = Pattern.compile("(MR|MRS)\\s+.*", Pattern.CASE_INSENSITIVE);

        Matcher matcher = patternName.matcher(text.getText());

        // Extract the account holder name if the pattern is found
        if (matcher.find()) {
            accountHolder = matcher.group();
            Log.d("Filter", "Account holder name: " + accountHolder);
        } else {
            Log.d("Filter", "Account holder name not found");
        }

        // Extract the deposit amount using the amount pattern
        Matcher amountMatcher = amountPattern.matcher(textString);

        if (amountMatcher.find()) {
            depositAmount = amountMatcher.group().replaceAll("[^0-9]", "");
        }
        Log.d("Filter", "Deposit amount: " + depositAmount);

        // Extract the account number using the number pattern
        Matcher numberMatcher = numberPattern.matcher(textString);

        if (numberMatcher.find()) {
            accountNumber = numberMatcher.group();
        }
        Log.d("Filter", "Account number: " + accountNumber);

        // check data and upload
        checkExtractedData();

    }
}