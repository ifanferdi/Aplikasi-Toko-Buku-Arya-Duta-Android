package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifanferdi.aplikasitokobukuv2.Adapter.UserAdapter;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelUser;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.util.ArrayList;
import java.util.List;

public class DataUser extends AppCompatActivity {

    Button btn_back;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    UserAdapter userAdapter;
    List<ModelUser> list;
    RecyclerView rView;

    ProgressDialog pd;
    BottomNavigationView navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_user);

        navbar = findViewById(R.id.bottom_navbar);
        rView = findViewById(R.id.recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        list = new ArrayList<>();
        userAdapter = new UserAdapter(this, list);
        rView.setAdapter(userAdapter);

        pd = new ProgressDialog(this);

        tampilData();
        kembali();
        bottomNavbar();
    }

    private void bottomNavbar() {
        navbar.setSelectedItemId(R.id.pengguna);

        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.buku:
                        finish();
                        startActivity(new Intent(getApplicationContext(), DataBuku.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.transaki:
                        finish();
                        startActivity(new Intent(getApplicationContext(), DataTransaksi.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.distributor:
                        finish();
                        startActivity(new Intent(getApplicationContext(), DataDistributor.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.pasok:
                        finish();
                        startActivity(new Intent(getApplicationContext(), DataPasok.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.pengguna:
                        return true;
                }
                return false;
            }
        });
    }

    public void tampilData(){
        pd.setTitle("Sedang memuat data...");
        pd.show();

        fStore.collection("Users").orderBy("nama").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                pd.dismiss();
                list.clear();
                for(DocumentSnapshot doc: task.getResult()){
                    ModelUser um = new ModelUser(
                            doc.getString("id"),
                            doc.getString("nama"),
                            doc.getString("jenis_kelamin"),
                            doc.getString("notelp"),
                            doc.getString("alamat"),
                            doc.getString("email"),
                            doc.getString("role")
                    );
                    list.add(um);
                }
                userAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(DataUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void kembali(){
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void hapusData(final String id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi hapus data");
        builder.setMessage("Apakah anda yakin ingin menghapus data ini?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.setTitle("Sedang menghapus data..");
                        pd.show();
                        fStore.collection("Users").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.dismiss();
                                Toast.makeText(DataUser.this, "Data user berhasil dihapus!", Toast.LENGTH_SHORT).show();
                                tampilData();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(DataUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).create().show();
    }

    protected void onStart(){
        super.onStart();
        tampilData();
    }

}