package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    EditText text_email, text_password;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView btn_daftar, notif_verify, text_reset;

    LayoutInflater inflater;
    AlertDialog.Builder dialog;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        notif_verify = findViewById(R.id.notif_verify);
        text_email = findViewById(R.id.text_email);
        text_password = findViewById(R.id.text_password);
        text_reset = findViewById(R.id.text_resetpassword);
        btn_login = findViewById(R.id.btn_masuk);
        btn_daftar = findViewById(R.id.btn_daftar);

        notif_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notif_verify.setVisibility(View.GONE);
            }
        });

        dialog = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();

        goToDaftar();
        login();
        resetPassword(inflater);
    }

    private void resetPassword(final LayoutInflater inflater) {
        text_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final View v = inflater.inflate(R.layout.forgot_password, null);
                dialog.setTitle("Reset Password?")
                        .setMessage("Masukkan email untuk mereset password")
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText email = v.findViewById(R.id.email_forgot);
                                if(email.getText().toString().isEmpty()){
                                    email.setError("Mohon isi email anda!");
                                } else {
                                    fAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(LoginActivity.this, "Reset email terkirim.\nSilahkan cek email anda.", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).setNegativeButton("Batal", null)
                        .setView(v).create().show();
            }
        });
    }

    private void login(){
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = text_email.getText().toString();
                String password = text_password.getText().toString();

                if(email.isEmpty()){
                    text_email.setError("Mohon isi email anda");
                } else if(password.isEmpty()){
                    text_password.setError("Mohon isi password anda");
                } else {
                    pd.setTitle("Sedang memuat...");
                    pd.show();
                    fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if(fAuth.getCurrentUser().isEmailVerified()){
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                                checkUserAccessLevel(authResult.getUser().getUid());
                            } else {
                                pd.dismiss();
                                Resources resources = getResources();
                                Drawable bg_red = ResourcesCompat.getDrawable(resources, R.drawable.bg_solid_red, null);
                                notif_verify.setBackground(bg_red);
                                notif_verify.setText("Login Gagal. \nEmail anda belum terverifikasi!");
                                notif_verify.setTextColor(Color.parseColor("#651614"));
                                notif_verify.setVisibility(View.VISIBLE);
                                text_email.setText("");
                                text_password.setText("");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Gagal!\nUsername dan password tidak cocok", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void checkUserAccessLevel(String uid) {
        DocumentReference df = fStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "onSuccess" + documentSnapshot.getData());
                if(Objects.equals(documentSnapshot.getString("role"), "Admin")){
                    startActivity(new Intent(getApplicationContext(), HomeAdmin.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), KatalogBuku.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Login Gagal \n " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToDaftar(){
        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    protected void onStart(){
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            if(!user.isEmailVerified()){
                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        notif_verify.setVisibility(View.VISIBLE);
                    }
                });
                fAuth.signOut();
            } else {
                checkUserAccessLevel(user.getUid());
            }
        }

    }

}