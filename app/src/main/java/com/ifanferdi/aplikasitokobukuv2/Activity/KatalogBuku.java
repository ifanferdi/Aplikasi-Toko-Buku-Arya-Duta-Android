package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.ifanferdi.aplikasitokobukuv2.Adapter.KatalogAdapter;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.util.ArrayList;
import java.util.List;

public class KatalogBuku extends AppCompatActivity {

    ProgressDialog pd;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    List<ModelBuku> list;
    RecyclerView rView;
    KatalogAdapter katalogAdapter;
    BottomNavigationView navbar;

    TextView text_nama, text_email, text_role, text_verify;
    ImageView text_cart;
    Button btn_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog_buku);

        text_nama = findViewById(R.id.text_nama);
        text_email = findViewById(R.id.text_email);
        text_role = findViewById(R.id.text_role);
        text_cart = findViewById(R.id.text_cart);
        btn_cart = findViewById(R.id.btn_cart);

        navbar = findViewById(R.id.bottom_navbar);

        pd = new ProgressDialog(this);

        rView = findViewById(R.id.katalog_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        list = new ArrayList<>();
        katalogAdapter = new KatalogAdapter(this, list);
        rView.setLayoutManager(new GridLayoutManager(this, 2));
        rView.setAdapter(katalogAdapter);

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Keranjang.class));
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

    private void bottomNavbar() {
        navbar.setSelectedItemId(R.id.home);

        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        return true;
                    case R.id.riwayat:
                        finish();
                        startActivity(new Intent(getApplicationContext(), RiwayatTransaksi.class));
                        overridePendingTransition(0,0);
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

    public void tampilKatalogBuku() {
        pd.setTitle("Sedang memuat data");
        pd.show();
        fStore.collection("Buku").orderBy("judul").whereNotEqualTo("judul", "- Pilih Buku -").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        pd.dismiss();
                        for(DocumentSnapshot doc: task.getResult()){
                            ModelBuku buku = new ModelBuku(
                                    doc.getString("id"),
                                    doc.getString("kode"),
                                    doc.getString("judul"),
                                    doc.getString("noisbn"),
                                    doc.getString("penulis"),
                                    doc.getString("penerbit"),
                                    doc.getString("tahun"),
                                    doc.getLong("stok"),
                                    doc.getString("kategori"),
                                    doc.getLong("harga"),
                                    doc.getLong("diskon"),
                                    doc.getString("keterangan"),
                                    doc.getString("image")
                            );
                            list.add(buku);
                        }
                        katalogAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(KatalogBuku.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onStart() {
        super.onStart();
        bottomNavbar();
        tampilKatalogBuku();
        cart();
    }

}