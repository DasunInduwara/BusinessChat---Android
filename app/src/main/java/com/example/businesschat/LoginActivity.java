package com.example.businesschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.businesschat.models.UsersModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;


public class LoginActivity extends AppCompatActivity {
    AppCompatButton loginBtn;
    AppCompatButton registerBtn;
    CountryCodePicker mCountryCodePicker;
    EditText phoneNumberEditText;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        mCountryCodePicker = findViewById(R.id.countryCode_picker);
        phoneNumberEditText = findViewById(R.id.phoneEditText);

        mDatabase =FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = mDatabase.getReference("Users");

        sharedPreferences = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        pd = new ProgressDialog(LoginActivity.this);


        // check current user
//        if (mAuth.getCurrentUser() != null) {
//
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        }

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        setButtonClicks();
    }

    private void setButtonClicks() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Login", "onClick: ccp " + mCountryCodePicker.getSelectedCountryCodeWithPlus() + phoneNumberEditText.getText());
                String pNo = mCountryCodePicker.getSelectedCountryCodeWithPlus() + phoneNumberEditText.getText();
                if (phoneNumberEditText.getText().toString().isEmpty()){
                    phoneNumberEditText.setError("Please enter phone number");
                }else{
                    signIn(pNo);
                    pd.setMessage("loading");
                    pd.show();
                }

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    void signIn(String phoneNumber){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()){
                    Log.d("Login", "onDataChange: snapshot has data");

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.exists()){
                            Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("phoneNumber").equalTo(phoneNumber);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot data: snapshot.getChildren()){
                                            if (data != null) {
                                                Log.w("Login", "onDataChange: Key " + data.getKey());
                                                Log.w("Login", "onDataChange: Email " + data.child("email").getValue(String.class));
                                                Log.w("Login", "onDataChange: Name " + data.child("displayName").getValue(String.class));
                                                Log.w("Login", "onDataChange: ID " + data.child("identification").getValue(String.class));
                                                Log.w("Login", "onDataChange: UID " + data.child("uId").getValue(String.class));


                                                UsersModel usersModel = new UsersModel();
                                                usersModel.setEmail(data.child("email").getValue(String.class));
                                                usersModel.setImgUrl(data.child("imgUrl").getValue(String.class));
                                                usersModel.setDisplayName(data.child("displayName").getValue(String.class));
                                                usersModel.setPhoneNumber(data.getKey());
                                                usersModel.setIdentification(data.child("identification").getValue(String.class));
                                                usersModel.setuId(data.child("uId").getValue(String.class));

                                                mAuth.signInWithEmailAndPassword(data.child("email").getValue(String.class), phoneNumber).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()){
                                                            myEdit.putString("phone", phoneNumber);
                                                            myEdit.putString("email", data.child("email").getValue(String.class));
                                                            myEdit.commit();
                                                            Log.d("Login", "onComplete: Auth Success ");
                                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                        }else{
                                                            pd.dismiss();
                                                            Log.w("Login", "createUserWithEmail:failure", task.getException());
                                                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }else{
                                        pd.dismiss();
                                        Toast.makeText(LoginActivity.this, "Account not exist", Toast.LENGTH_SHORT).show();
                                        Log.w("Login", "onDataChange: Snapshot Empty");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                }else{
                    pd.dismiss();
                    Log.e("Login", "onDataChange: snapshot not exist & no children");
                    Toast.makeText(LoginActivity.this, "Account not exist", Toast.LENGTH_SHORT).show();
//                    SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE);
//                    pDialog.setTitleText("Your account not exist");
//                    pDialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void signUp(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                finish();
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            } catch (ApiException e) {
                Log.e("login", "onActivityResult: ", e);
                Toast.makeText(this, "something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}