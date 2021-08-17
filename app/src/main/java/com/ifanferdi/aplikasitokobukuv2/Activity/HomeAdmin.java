package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifanferdi.aplikasitokobukuv2.R;

public class HomeAdmin extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    TextView text_nama, text_email, text_role;
    Button btn_logout;
    LinearLayout data_buku, data_user, data_distributor, data_pasok, data_transaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        text_nama = findViewById(R.id.text_nama);
        text_email = findViewById(R.id.text_email);
        text_role = findViewById(R.id.text_role);

        data_buku = findViewById(R.id.data_buku);
        data_distributor = findViewById(R.id.data_distributor);
        data_pasok = findViewById(R.id.data_pasok);
        data_transaksi = findViewById(R.id.data_transaksi);
        data_user = findViewById(R.id.data_pengguna);

        data_pasok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DataPasok.class));
            }
        });

        data_transaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DataTransaksi.class));
            }
        });

        data_distributor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DataDistributor.class));
            }
        });

        data_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DataUser.class));
            }
        });

        data_buku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DataBuku.class));
            }
        });



        infoLogin();
        logout();
    }

    private void logout(){
        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeAdmin.this);
                builder.setTitle("Konfirmasi Logout");
                builder.setMessage("Apakah anda yakin ingin keluar?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                fAuth.signOut();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }
                        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).create().show();
            }
        });
    }

    private void infoLogin(){
        FirebaseUser user = fAuth.getCurrentUser();

        if(user != null){
            DocumentReference df = fStore.collection("Users").document(user.getUid());
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d("TAG", "onSuccess" + documentSnapshot.getData());
                    text_email.setText(documentSnapshot.getString("email"));
                    text_nama.setText(documentSnapshot.getString("nama"));
                    text_role.setText(documentSnapshot.getString("role"));
                }
            });
        }
    }
}