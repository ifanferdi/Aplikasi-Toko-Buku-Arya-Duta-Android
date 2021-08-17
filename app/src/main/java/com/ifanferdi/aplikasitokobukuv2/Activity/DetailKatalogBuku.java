package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class DetailKatalogBuku extends AppCompatActivity {

    Button btn_back, btn_keranjang;
    TextView text_judul, text_judulbuku, text_namabuku, text_hargabuku, text_diskon, text_noisbn;
    TextView text_stok, text_penulis, text_penerbit, text_tahun, text_kategori, text_keterangan;
    ImageView image_buku;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser user;
    StorageReference storageReference;

    String id_buku, kode_buku, judul_buku, noisbn_buku, penulis_buku, penerbit_buku, tahun_buku,
            stok_buku, kategori_buku, img_buku, keterangan_buku;
    int harga_buku, diskon_buku;
    Button btn_cart;

    DecimalFormat decimalFormat;
    DecimalFormatSymbols formatRupiah;
    ImageView text_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_katalog_buku);

        decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        formatRupiah = new DecimalFormatSymbols();
        formatRupiah.setCurrencySymbol("Rp. ");
        formatRupiah.setMonetaryDecimalSeparator(',');
        formatRupiah.setGroupingSeparator('.');
        decimalFormat.setDecimalFormatSymbols(formatRupiah);

        text_cart = findViewById(R.id.text_cart);
        btn_back = findViewById(R.id.btn_back);
        btn_keranjang = findViewById(R.id.btn_keranjang);

        text_judul = findViewById(R.id.text_judul);
        text_judulbuku = findViewById(R.id.text_judulbuku);
        text_namabuku  = findViewById(R.id.text_namabuku);
        text_hargabuku = findViewById(R.id.text_hargabuku);
        text_diskon = findViewById(R.id.text_diskon);
        text_stok = findViewById(R.id.text_stok);
        text_noisbn = findViewById(R.id.text_noisbn);
        text_penulis = findViewById(R.id.text_penulis);
        text_penerbit = findViewById(R.id.text_penerbit);
        text_tahun  = findViewById(R.id.text_tahun);
        text_kategori = findViewById(R.id.text_kategori);
        text_keterangan = findViewById(R.id.text_keterangan);
        image_buku = findViewById(R.id.imageView2);
        btn_cart = findViewById(R.id.btn_cart);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        final Bundle bundle = getIntent().getExtras();
        if(bundle != null) {

            id_buku = bundle.getString("id");
            kode_buku = bundle.getString("kode");
            judul_buku = bundle.getString("judul");
            noisbn_buku = bundle.getString("noisbn");
            penulis_buku = bundle.getString("penulis");
            penerbit_buku = bundle.getString("penerbit");
            tahun_buku = bundle.getString("tahun");
            stok_buku = bundle.getString("stok");
            kategori_buku = bundle.getString("kategori");
            harga_buku = Integer.parseInt(bundle.getString("harga"));
            diskon_buku = Integer.parseInt(bundle.getString("diskon"));
            keterangan_buku = bundle.getString("keterangan");
            img_buku = bundle.getString("image");

            final int hitung_harga = harga_buku - (harga_buku * diskon_buku / 100);
            text_hargabuku.setText(decimalFormat.format(hitung_harga));
            if (diskon_buku == 0){
                text_diskon.setVisibility(View.INVISIBLE);
            } else {
                text_diskon.setText("-" + diskon_buku + "%");
            }
            Glide.with(this).load(img_buku).into(image_buku);
            text_judulbuku.setText(judul_buku);
            text_namabuku.setText(judul_buku);
            text_stok.setText(stok_buku);
            text_judul.setText(judul_buku);
            text_noisbn.setText(noisbn_buku);
            text_penulis.setText(penulis_buku);
            text_penerbit.setText(penerbit_buku);
            text_tahun.setText(tahun_buku);
            text_kategori.setText(kategori_buku);
            text_keterangan.setText(keterangan_buku);

            btn_keranjang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FormKeranjang formKeranjang = new FormKeranjang(fAuth.getCurrentUser(), id_buku,
                            DetailKatalogBuku.this, hitung_harga);
                    formKeranjang.show(getSupportFragmentManager(), "form");
                }
            });

            btn_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), Keranjang.class));
                }
            });
        }


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cart();
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


    protected void onStart() {
        super.onStart();
        cart();
    }

}
