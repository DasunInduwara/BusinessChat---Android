package com.example.businesschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businesschat.models.UsersModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    CountryCodePicker cCPCountryPicker;
    EditText displayNameEditText, identificationEditText, phoneEditText;
    AppCompatButton continueToRegisterBtn;
    FirebaseAuth mAuth;
    Account mAccount;
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    final static String TAG = "Register_Activity";
    private String email;
    private ProgressDialog mProgressDialog;
    private String imageUrl = "https://firebasestorage.googleapis.com/v0/b/businesschat-dd1fe.appspot.com/o/assests%2Fuser.png?alt=media&token=c72207bb-56aa-4284-94c8-c841e4822a84";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setCancelable(false);

        // config google sign in
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        cCPCountryPicker = findViewById(R.id.countryCode_picker);
        displayNameEditText = findViewById(R.id.displayNameEditText);
        identificationEditText = findViewById(R.id.identificationEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        continueToRegisterBtn = findViewById(R.id.continueBtn);

        sharedPreferences = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            Log.d(TAG, "onCreate: User Email success");
            email = account.getEmail();
        }



        continueToRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onCreate: country code: " + cCPCountryPicker.getDefaultCountryCode());
                Log.i(TAG, "onCreate: phone number: " + phoneEditText.getText());

                if (email.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Can't find your email", Toast.LENGTH_SHORT).show();
                }else if (phoneEditText.getText().toString().isEmpty()){
                    phoneEditText.setError("Please enter your phone number");
                }else if(displayNameEditText.getText().toString().isEmpty()){
                    displayNameEditText.setError("Please enter display name");
                }else if (identificationEditText.getText().toString().isEmpty()) {
                    identificationEditText.setError("Please enter identification");
                }else{
                    Log.i(TAG, "onClick: create acc details" + email.toString()+" / "+ phoneEditText.getText().toString());
                    mProgressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email.toString(), cCPCountryPicker.getSelectedCountryCodeWithPlus() + phoneEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: authentication user created");
                                DatabaseReference databaseReference = mDatabase.getReference().child("Users").child(Objects.requireNonNull(cCPCountryPicker.getSelectedCountryCodeWithPlus() + phoneEditText.getText().toString()));

                                UsersModel usersModel = new UsersModel(
                                        mAuth.getUid(),
                                        cCPCountryPicker.getSelectedCountryCodeWithPlus() + phoneEditText.getText().toString(),
                                        email.toString(),
                                        displayNameEditText.getText().toString(),
                                        identificationEditText.getText().toString(),
                                        imageUrl );

                                databaseReference.setValue(usersModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            myEdit.putString("phone", cCPCountryPicker.getSelectedCountryCodeWithPlus() + phoneEditText.getText().toString());
                                            myEdit.putString("email", email);
                                            myEdit.commit();
                                            mProgressDialog.dismiss();
                                            Toast.makeText(RegisterActivity.this, "You have registered successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }else{
                                            mProgressDialog.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Error creating a user", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }else{
                                mProgressDialog.dismiss();
                                Log.d(TAG, "onComplete: authentication user create failed => " + task.getException());
                                Toast.makeText(RegisterActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }


            }
        });

    }

}