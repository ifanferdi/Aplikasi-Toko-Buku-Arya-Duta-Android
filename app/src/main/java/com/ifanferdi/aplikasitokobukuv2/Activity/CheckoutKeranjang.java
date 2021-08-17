package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifanferdi.aplikasitokobukuv2.Adapter.CheckoutAdapter;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelDetailTransaksi;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelKeranjang;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelTransaksi;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CheckoutKeranjang extends AppCompatActivity {

    private TextView text_nama_telp,
            text_alamat,
            text_subtotal,
            text_biayapengiriman,
            text_totalpembayaran,
            text_totalpembayaran2;
    private Button btn_pesan, btn_back;
    private RecyclerView rView;
    private Bundle bundle;
    private ProgressDialog pd;

    private KatalogBuku katalogBuku;
    private Keranjang keranjang;
    private List<ModelBuku> listBuku;
    private List<ModelKeranjang> list;

    private CheckoutAdapter checkoutAdapter;

    private int subtotal = 0;
    private int biaya_pengiriman = 15000, total_pembayaran = 0;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;

    private DecimalFormat decimalFormat;
    private DecimalFormatSymbols decimalFormatSymbols;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_keranjang);

        text_nama_telp = findViewById(R.id.text_nama_telp);
        text_alamat = findViewById(R.id.text_alamat);
        text_subtotal = findViewById(R.id.text_subtotal);
        text_biayapengiriman = findViewById(R.id.text_biayapengiriman);
        text_totalpembayaran = findViewById(R.id.text_totalpembayaran);
        text_totalpembayaran2 = findViewById(R.id.text_totalpembayaran2);
        btn_pesan  = findViewById(R.id.btn_pesan);
        btn_back = findViewById(R.id.btn_back);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("Rp. ");
        decimalFormatSymbols.setMonetaryDecimalSeparator(',');
        decimalFormatSymbols.setGroupingSeparator('.');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

        pd = new ProgressDialog(this);

        text_biayapengiriman.setText(decimalFormat.format(biaya_pengiriman));

        bundle = getIntent().getExtras();
        if(bundle != null){
            subtotal = bundle.getInt("subtotal");
            total_pembayaran = subtotal + biaya_pengiriman;
            text_subtotal.setText(decimalFormat.format(subtotal));
            text_totalpembayaran.setText(decimalFormat.format(total_pembayaran));
            text_totalpembayaran2.setText(decimalFormat.format(total_pembayaran));
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getDataUser();
        tampilCheckout();
        simpanTransaksi();
    }

    private void tampilCheckout() {
        rView = findViewById(R.id.checkout_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        listBuku = new ArrayList<>();
        checkoutAdapter = new CheckoutAdapter(this, list, listBuku);
        rView.setAdapter(checkoutAdapter);

        pd.setTitle("Sedang memuat data");
        pd.show();

        fStore.collection("Keranjang").whereEqualTo("id_user", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(final QueryDocumentSnapshot document: task.getResult()){
                    String id_buku = document.getString("id_buku");
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
                            checkoutAdapter.notifyDataSetChanged();
                        }
                    });
                }
                pd.dismiss();
            }
        });
    }

    private void getDataUser() {
        if(user != null){
            DocumentReference df = fStore.collection("Users").document(user.getUid());
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d("TAG", "onSuccess" + documentSnapshot.getData());
                    text_nama_telp.setText(documentSnapshot.getString("nama") + " | " + documentSnapshot.getString("notelp"));
                    text_alamat.setText(documentSnapshot.getString("alamat"));
                }
            });
        }
    }

    private void simpanTransaksi(){

        final String no_transaksi = UUID.randomUUID().toString();
        final String id_user = user.getUid();
        Timestamp waktu = new Timestamp(new Date());
        Long total_bayar = Long.parseLong(String.valueOf(total_pembayaran));
        String status = "Belum Dibayar";
        String bukti_pembayaran = "";
        String resi = "";

        final ModelTransaksi modelTransaksi = new ModelTransaksi(no_transaksi, id_user, waktu, total_bayar, status, bukti_pembayaran, resi);

        btn_pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setTitle("Sedang menyimpan data");
                pd.show();
                fStore.collection("Transaksi").document(no_transaksi).set(modelTransaksi).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        fStore.collection("Keranjang").whereEqualTo("id_user", id_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(final QueryDocumentSnapshot document: task.getResult()){
                                    final String id = document.getString("id");
                                    final String id_detail_transaksi = UUID.randomUUID().toString();
                                    final String id_buku = document.getString("id_buku");
                                    Long harga = document.getLong("harga");
                                    final Long jumlah = document.getLong("jumlah");
                                    Long total = document.getLong("total");
                                    ModelDetailTransaksi modelDetailTransaksi = new ModelDetailTransaksi(id_detail_transaksi, no_transaksi, id_buku, harga, jumlah, total);
                                    fStore.collection("DetailTransaksi").document(id_detail_transaksi).set(modelDetailTransaksi).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            fStore.collection("Keranjang").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    fStore.collection("Buku").whereEqualTo("id", id_buku).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            for (DocumentSnapshot document : task.getResult()){
                                                                int stok = Integer.parseInt(document.getLong("stok").toString());
                                                                final int stok_baru = stok - Integer.parseInt(String.valueOf(jumlah));
                                                                fStore.collection("Buku").document(id_buku).update("stok", stok_baru).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        pd.dismiss();
                                                                        Toast.makeText(CheckoutKeranjang.this, "Pesanan berhasil disimpan.", Toast.LENGTH_SHORT).show();
                                                                        finish();
                                                                        startActivity(new Intent(getApplicationContext(), RiwayatTransaksi.class));
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(CheckoutKeranjang.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CheckoutKeranjang.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CheckoutKeranjang.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}