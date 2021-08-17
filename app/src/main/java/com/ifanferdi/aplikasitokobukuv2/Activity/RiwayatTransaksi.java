package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifanferdi.aplikasitokobukuv2.Adapter.RiwayatAdapter;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelTransaksi;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.util.ArrayList;
import java.util.List;

public class RiwayatTransaksi extends AppCompatActivity {

    private BottomNavigationView navbar;
    private ProgressDialog pd;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    private List<ModelTransaksi> list;
    private RecyclerView rView;
    private RiwayatAdapter riwayatAdapter;
    ImageView text_cart;
    Button btn_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_transaksi);

        navbar = findViewById(R.id.bottom_navbar);
        pd = new ProgressDialog(this);
        text_cart = findViewById(R.id.text_cart);
        btn_cart = findViewById(R.id.btn_cart);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        rView = findViewById(R.id.riwayat_recycler_view);

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Keranjang.class));
            }
        });

        bottomNavbar();
        cart();
    }

    private void tampilRiwayat() {
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        riwayatAdapter = new RiwayatAdapter(this, list);
        rView.setAdapter(riwayatAdapter);

        pd.setTitle("Sedang memuat data");
        pd.show();

        fStore.collection("Transaksi")
                .whereEqualTo("id_user", user.getUid())
                .orderBy("waktu", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                list.clear();
                pd.dismiss();
                for(QueryDocumentSnapshot doc: task.getResult()){
                    ModelTransaksi transaksi = new ModelTransaksi(
                            doc.getString("no_transaksi"),
                            doc.getString("id_user"),
                            doc.getTimestamp("waktu"),
                            doc.getLong("total_bayar"),
                            doc.getString("status"),
                            doc.getString("bukti_pembayaran"),
                            doc.getString("resi")
                    );
                    list.add(transaksi);
                }
                riwayatAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RiwayatTransaksi.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void bottomNavbar() {
        navbar.setSelectedItemId(R.id.riwayat);

        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        finish();
                        startActivity(new Intent(getApplicationContext(), KatalogBuku.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.riwayat:
                        return true;
                    case R.id.profil:
                        finish();
                        startActivity(new Intent(getApplicationContext(), ProfilPengguna.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    private void cart(){
        text_cart.setVisibility(View.GONE);
        fStore.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                final String idUser = documentSnapshot.getString("id");

                fStore.collection("Keranjang")
                        .whereEqualTo("id_user", idUser)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(DocumentSnapshot document : task.getResult()){
                                        if(!document.getLong("jumlah").toString().isEmpty()){
                                            text_cart.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        tampilRiwayat();
    }
}