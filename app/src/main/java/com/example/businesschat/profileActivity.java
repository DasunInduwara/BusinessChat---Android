package com.example.businesschat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class profileActivity extends AppCompatActivity {
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor myEdit;
    TextView emailTextView, contactNumberTextView;
    EditText accNameEditText, accNumberEditText, bankNameEditText;
    AppCompatButton editButton;
    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mSharedPreferences = getApplication().getSharedPreferences("user", MODE_PRIVATE);
        myEdit = mSharedPreferences.edit();

        emailTextView = findViewById(R.id.emailText);
        contactNumberTextView = findViewById(R.id.phoneNumberText);

        accNameEditText =  findViewById(R.id.accNameEditText);
        accNumberEditText = findViewById(R.id.accNumberEditText);
        bankNameEditText = findViewById(R.id.bankNameEditText);
        editButton = findViewById(R.id.editButton);

        String bankName = mSharedPreferences.getString("bankName","Empty");
        String accNumber = mSharedPreferences.getString("accNumber","Empty");
        String accName = mSharedPreferences.getString("accName","Empty");

        accNameEditText.setText(accName);
        accNumberEditText.setText(accNumber);
        bankNameEditText.setText(bankName);

        contactNumberTextView.setText(mSharedPreferences.getString("phone",""));
        emailTextView.setText(mSharedPreferences.getString("email",""));

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEdit = !isEdit; // toggle the isEdit flag
                accNameEditText.setEnabled(isEdit);
                accNumberEditText.setEnabled(isEdit);
                bankNameEditText.setEnabled(isEdit);
                if (isEdit){
                    editButton.setText("Save");


                }else{
                    editButton.setText("Edit");
                    myEdit.putString("accName", accNameEditText.getText().toString());
                    myEdit.putString("accNumber", accNumberEditText.getText().toString());
                    myEdit.putString("bankName", bankNameEditText.getText().toString());
                    myEdit.commit();

                }

            }
        });




    }
}