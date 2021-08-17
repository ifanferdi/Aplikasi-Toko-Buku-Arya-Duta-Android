package com.ifanferdi.aplikasitokobukuv2.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ifanferdi.aplikasitokobukuv2.Adapter.DetailRiwayatAdapter;
import com.ifanferdi.aplikasitokobukuv2.Adapter.RiwayatAdapter;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelDetailTransaksi;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelKeranjang;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelTransaksi;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailRiwayatTransaksi extends AppCompatActivity {

    private ProgressDialog pd;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    private Button btn_back, btn_upload_bukti;

    private TextView text_status, text_tanggal, text_notransaksi, text_resi, text_nama, text_notelp,
            text_alamat, text_total, text_biaya_pengiriman, text_totalpembayaran;

    private LinearLayout riwayat_pembelian, pengiriman;
    private View divider24, div;

    private RecyclerView rView;
    private List<ModelBuku> listBuku;
    private List<ModelDetailTransaksi> list;

    private DecimalFormat decimalFormat;
    private DecimalFormatSymbols decimalFormatSymbols;
    private DetailRiwayatAdapter detailRiwayatAdapter;

    private Bundle bundle;
    private String no_transaksi, id_user, tanggal, waktu, total_bayar, status, bukti_pembayaran, resi;
    private int IMG_REQUEST_ID = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_riwayat_transaksi);

        text_status = findViewById(R.id.text_status);
        text_tanggal = findViewById(R.id.text_tanggal);
        text_notransaksi = findViewById(R.id.text_notransaksi);
        text_resi = findViewById(R.id.text_resi);
        text_nama = findViewById(R.id.text_penerima);
        text_notelp = findViewById(R.id.text_notelp);
        text_alamat = findViewById(R.id.text_alamat);
        text_total = findViewById(R.id.text_subtotal);
        text_biaya_pengiriman = findViewById(R.id.text_biayapengiriman);
        text_totalpembayaran = findViewById(R.id.text_totalpembayaran);
        btn_back = findViewById(R.id.btn_back);
        btn_upload_bukti = findViewById(R.id.btn_upload);
        riwayat_pembelian = findViewById(R.id.riwayat_pembelian);
        pengiriman = findViewById(R.id.pengiriman);
        divider24 = findViewById(R.id.divider24);
        div = findViewById(R.id.div);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        pd = new ProgressDialog(this);

        decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("Rp. ");
        decimalFormatSymbols.setMonetaryDecimalSeparator(',');
        decimalFormatSymbols.setGroupingSeparator('.');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

        bundle = getIntent().getExtras();
        if(bundle != null){
            pd.setTitle("Sedang memuat data");
            pd.show();

            no_transaksi = bundle.getString("no_transaksi");
            id_user = bundle.getString("id_user");
            tanggal = bundle.getString("tanggal");
            waktu = bundle.getString("waktu");
            total_bayar = bundle.getString("total_bayar");
            status = bundle.getString("status");
            bukti_pembayaran = bundle.getString("bukti_pembayaran");
            resi = bundle.getString("resi");

            if(status.equals("Belum Dibayar")){
                riwayat_pembelian.setVisibility(View.VISIBLE);
                div.setVisibility(View.VISIBLE);
                btn_upload_bukti.setText("Upload Bukti Pembayaran");
                btn_upload_bukti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), UploadBuktiPembayaran.class);
                        intent.putExtra("no_transaksi", no_transaksi);
                        startActivity(intent);
                    }
                });
            } else if(status.equals("Sedang Dikirim")){
                riwayat_pembelian.setVisibility(View.VISIBLE);
                div.setVisibility(View.VISIBLE);
                btn_upload_bukti.setText("Upload Bukti Pembayaran");
                btn_upload_bukti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pesananSelesai();
                    }
                });
            } else {
                riwayat_pembelian.setVisibility(View.GONE);
                div.setVisibility(View.GONE);
            }

            if(status.equals("Sedang Dikirim") || status.equals("Pesanan Diterima")){
                pengiriman.setVisibility(View.VISIBLE);
                divider24.setVisibility(View.VISIBLE);
                text_status.setTextColor(Color.parseColor("#FF00CD00"));
            } else {
                pengiriman.setVisibility(View.GONE);
                divider24.setVisibility(View.GONE);
                text_status.setTextColor(Color.parseColor("#FFEF8E00"));
            }

            text_status.setText(status);
            text_tanggal.setText(tanggal + ", " + waktu);
            text_notransaksi.setText(no_transaksi);
            text_totalpembayaran.setText(decimalFormat.format(Integer.parseInt(total_bayar)));
            text_total.setText(decimalFormat.format(Integer.parseInt(total_bayar) - 15000));
            text_biaya_pengiriman.setText(decimalFormat.format(15000));
            text_resi.setText(resi);


            tampilPenerima();
            tampilProduk(no_transaksi);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void pesananSelesai() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(DetailRiwayatTransaksi.this);
        builder.setTitle("Konfirmasi Terima Pesanan");
        builder.setMessage("Apakah pesanan telah anda terima?").setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pd.setTitle("Sedang menyimpan data");
                pd.show();
                fStore.collection("Transaksi").document(no_transaksi).update("status", "Pesanan Diterima").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        finish();
                        Toast.makeText(DetailRiwayatTransaksi.this, "Pesanan telah diterima", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailRiwayatTransaksi.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void tampilProduk(String no_transaksi) {
        rView = findViewById(R.id.riwayat_transaksi_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        listBuku = new ArrayList<>();
        detailRiwayatAdapter = new DetailRiwayatAdapter(this, list, listBuku);
        rView.setAdapter(detailRiwayatAdapter);

        fStore.collection("DetailTransaksi").whereEqualTo("no_transaksi", no_transaksi).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(final DocumentSnapshot doc: task.getResult()){
                    String id_buku = doc.getString("id_buku");
                    list.clear();
                    listBuku.clear();
                    fStore.collection("Buku").whereEqualTo("id", id_buku).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            pd.dismiss();
                            for (DocumentSnapshot document: task.getResult()) {
                                ModelDetailTransaksi data = new ModelDetailTransaksi(
                                        doc.getString("id_detail_transaksi"),
                                        doc.getString("no_transaksi"),
                                        doc.getString("id_buku"),
                                        doc.getLong("harga"),
                                        doc.getLong("jumlah"),
                                        doc.getLong("total")
                                );
                                list.add(data);
                                ModelBuku buku = new ModelBuku(
                                        document.getString("id"),
                                        document.getString("kode"),
                                        document.getString("judul"),
                                        document.getString("noisbn"),
                                        document.getString("penulis"),
                                        document.getString("penerbit"),
                                        document.getString("tahun"),
                                        document.getLong("stok"),
                                        document.getString("kategori"),
                                        document.getLong("harga"),
                                        document.getLong("diskon"),
                                        document.getString("keterangan"),
                                        document.getString("image")
                                );
                                listBuku.add(buku);
                            }
                            detailRiwayatAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailRiwayatTransaksi.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailRiwayatTransaksi.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tampilPenerima() {
        fStore.collection("Users").whereEqualTo("id", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot document : task.getResult()){
                    text_nama.setText(document.getString("nama"));
                    text_notelp.setText(document.getString("notelp"));
                    text_alamat.setText(document.getString("alamat"));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fStore.collection("Transaksi").whereEqualTo("no_transaksi", no_transaksi).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot document: task.getResult()){
                    status = document.getString("status");
                    resi = document.getString("resi");
                    text_resi.setText(resi);
                    text_status.setText(status);

                    if(status.equals("Belum Dibayar")){
                        riwayat_pembelian.setVisibility(View.VISIBLE);
                        div.setVisibility(View.VISIBLE);
                        btn_upload_bukti.setText("Upload Bukti Pembayaran");
                        btn_upload_bukti.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), UploadBuktiPembayaran.class);
                                intent.putExtra("no_transaksi", no_transaksi);
                                startActivity(intent);
                            }
                        });
                    } else if(status.equals("Sedang Dikirim")){
                        riwayat_pembelian.setVisibility(View.VISIBLE);
                        div.setVisibility(View.VISIBLE);
                        btn_upload_bukti.setText("Pesanan Diterima");
                        btn_upload_bukti.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pesananSelesai();
                            }
                        });
                    } else {
                        riwayat_pembelian.setVisibility(View.GONE);
                        div.setVisibility(View.GONE);
                    }

                    if(status.equals("Sedang Dikirim") || status.equals("Pesanan Diterima")){
                        pengiriman.setVisibility(View.VISIBLE);
                        divider24.setVisibility(View.VISIBLE);
                        text_status.setTextColor(Color.parseColor("#FF00CD00"));
                    } else {
                        pengiriman.setVisibility(View.GONE);
                        divider24.setVisibility(View.GONE);
                        text_status.setTextColor(Color.parseColor("#FFEF8E00"));
                    }
                }
            }
        });
    }
}