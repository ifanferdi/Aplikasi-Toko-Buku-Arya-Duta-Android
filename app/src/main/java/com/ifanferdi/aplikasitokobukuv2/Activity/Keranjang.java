package com.ifanferdi.aplikasitokobukuv2.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifanferdi.aplikasitokobukuv2.Adapter.KatalogAdapter;
import com.ifanferdi.aplikasitokobukuv2.Adapter.KeranjangAdapter;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelKeranjang;
import com.ifanferdi.aplikasitokobukuv2.R;

import org.w3c.dom.Text;

import java.sql.Array;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Keranjang extends AppCompatActivity {

    ProgressDialog pd;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    List<ModelKeranjang> list, listSubtotal;
    List<ModelBuku> listBuku;
    RecyclerView rView;
    KeranjangAdapter keranjangAdapter;
    TextView pesan_kosong, text_total, text_qty;

    int subtotal = 0, subtotaljumlah= 0;

    DecimalFormat decimalFormat;
    DecimalFormatSymbols decimalFormatSymbols;

    Button btn_back, btn_checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);

        decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("Rp. ");
        decimalFormatSymbols.setMonetaryDecimalSeparator(',');
        decimalFormatSymbols.setGroupingSeparator('.');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

        pd = new ProgressDialog(this);
        pesan_kosong = findViewById(R.id.pesan_kosong);
        text_qty = findViewById(R.id.text_qty);
        text_total = findViewById(R.id.text_total);
        btn_back = findViewById(R.id.btn_back);
        btn_checkout = findViewById(R.id.btn_checkout);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(subtotal == 0){
                    Toast.makeText(Keranjang.this, "Keranjang kosong.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), CheckoutKeranjang.class);
                    intent.putExtra("subtotal", subtotal);
                    startActivity(intent);
                }
            }
        });
    }

    public void hapusKeranjang(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi hapus data");
        builder.setMessage("Apakah anda yakin ingin menghapus data ini?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.setTitle("Sedang memuat data..");
                        pd.show();
                        fStore.collection("Keranjang").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.dismiss();
                                Toast.makeText(Keranjang.this, "Berhasil menghapus buku dari keranjang!", Toast.LENGTH_SHORT).show();
                                tampilKeranjang();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Keranjang.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void tampilKeranjang() {
        subtotal = 0;
        subtotaljumlah= 0;
        rView = findViewById(R.id.keranjang_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        listBuku = new ArrayList<>();
        keranjangAdapter = new KeranjangAdapter(this, list, listBuku);
        rView.setAdapter(keranjangAdapter);

        pd.setTitle("Sedang memuat data");
        pd.show();

        fStore.collection("Keranjang")
                .whereEqualTo("id_user", user.getUid())
                .orderBy("waktu", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(final QueryDocumentSnapshot document: task.getResult()){
                    String id_buku = document.getString("id_buku");

                    if(id_buku.isEmpty()){
                        pesan_kosong.setVisibility(View.VISIBLE);
                    } else {
                        pesan_kosong.setVisibility(View.GONE);
                    }

                    list.clear();
                    pd.dismiss();
                    listBuku.clear();
                    fStore.collection("Buku").whereEqualTo("id", id_buku).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for(DocumentSnapshot doc: task.getResult()){
                                ModelKeranjang data = new ModelKeranjang(
                                        document.getString("id"),
                                        document.getString("id_user"),
                                        document.getString("id_buku"),
                                        document.getLong("harga"),
                                        document.getLong("jumlah"),
                                        document.getLong("total"),
                                        document.getTimestamp("waktu")
                                );
                                list.add(data);

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
                                listBuku.add(buku);

                            }
                            keranjangAdapter.notifyDataSetChanged();
                        }
                    });
                }
                pd.dismiss();
            }
        });

    }

    private void hitungSubtotal(){
        listSubtotal = new ArrayList<>();
        fStore.collection("Keranjang")
                .whereEqualTo("id_user", user.getUid())
                .orderBy("waktu", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                listSubtotal.clear();
                for(final QueryDocumentSnapshot document: task.getResult()){
                    ModelKeranjang data = new ModelKeranjang(
                            document.getString("id"),
                            document.getString("id_user"),
                            document.getString("id_buku"),
                            document.getLong("harga"),
                            document.getLong("jumlah"),
                            document.getLong("total"),
                            document.getTimestamp("waktu")
                    );
                    listSubtotal.add(data);
                }
                tampilTotal();
            }
        });
    }

    public void tampilTotal() {
        for (int i=0; i < listSubtotal.size(); i++){
            subtotal += listSubtotal.get(i).getTotal();
            subtotaljumlah += listSubtotal.get(i).getJumlah();
            text_total.setText(""+decimalFormat.format(subtotal));
            text_qty.setText(""+ subtotaljumlah + " item");
        }
    }

    public void tampilTotalTambah(int harga) {
        subtotal += harga;
        subtotaljumlah += 1;
        text_total.setText(""+decimalFormat.format(subtotal));
        text_qty.setText(""+ subtotaljumlah + " item");
    }

    public void tampilTotalKurang(int harga) {
        subtotal -= harga;
        subtotaljumlah -= 1;
        text_total.setText(""+decimalFormat.format(subtotal));
        text_qty.setText(""+ subtotaljumlah + " item");
    }

    @Override
    protected void onStart() {
        super.onStart();
        tampilKeranjang();
        hitungSubtotal();
    }
}