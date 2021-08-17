package com.ifanferdi.aplikasitokobukuv2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifanferdi.aplikasitokobukuv2.Adapter.DetailAdapter;
import com.ifanferdi.aplikasitokobukuv2.Adapter.DetailRiwayatAdapter;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelDetailTransaksi;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailTransaksi extends AppCompatActivity {

    private ProgressDialog pd;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    private Button btn_back, btn_bukti, btn_edit;

    private TextView text_status, text_tanggal, text_notransaksi, text_resi, text_nama, text_notelp,
            text_alamat, text_total, text_biaya_pengiriman, text_totalpembayaran;

    private LinearLayout bukti_pembelian;
    private View div;

    private RecyclerView rView;
    private List<ModelBuku> listBuku;
    private List<ModelDetailTransaksi> list;

    private DecimalFormat decimalFormat;
    private DecimalFormatSymbols decimalFormatSymbols;
    private DetailAdapter detailAdapter;

    private Bundle bundle;
    private String no_transaksi, id_user, tanggal, waktu, total_bayar, status, bukti_pembayaran, resi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);

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
        btn_bukti = findViewById(R.id.btn_bukti);
        btn_edit = findViewById(R.id.btn_edit);
        bukti_pembelian = findViewById(R.id.bukti_pembelian);
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

            if(status.equals("Menunggu Konfirmasi Pembayaran")){
                bukti_pembelian.setVisibility(View.VISIBLE);
                div.setVisibility(View.VISIBLE);
            } else {
                bukti_pembelian.setVisibility(View.GONE);
                div.setVisibility(View.GONE);
            }

            if(status.equals("Sedang Dikirim") || status.equals("Pesanan Diterima")){
                text_status.setTextColor(Color.parseColor("#FF00CD00"));
            } else {
                text_status.setTextColor(Color.parseColor("#FFEF8E00"));
            }

            text_status.setText(status);
            text_tanggal.setText(tanggal + ", " + waktu);
            text_notransaksi.setText(no_transaksi);
            text_totalpembayaran.setText(decimalFormat.format(Integer.parseInt(total_bayar)));
            text_total.setText(decimalFormat.format(Integer.parseInt(total_bayar) - 15000));
            text_biaya_pengiriman.setText(decimalFormat.format(15000));
            text_resi.setText(resi);

            tampilPenerima(id_user);
            tampilProduk(no_transaksi);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditDetailTransaksi.class);
                intent.putExtra("no_transaksi",no_transaksi);
                intent.putExtra("status",status);
                intent.putExtra("resi", resi);
                startActivity(intent);
            }
        });

        btn_bukti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LihatBuktiPembayaran.class);
                intent.putExtra("no_transaksi", no_transaksi);
                startActivity(intent);
            }
        });
    }

    private void tampilProduk(String no_transaksi) {
        rView = findViewById(R.id.detail_transaksi_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        listBuku = new ArrayList<>();
        detailAdapter = new DetailAdapter(this, list, listBuku);
        rView.setAdapter(detailAdapter);

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
                            detailAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailTransaksi.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailTransaksi.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tampilPenerima(String id_user) {
        fStore.collection("Users").whereEqualTo("id", id_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                    if(status.equals("Menunggu Konfirmasi Pembayaran")){
                        bukti_pembelian.setVisibility(View.VISIBLE);
                        div.setVisibility(View.VISIBLE);
                    } else {
                        bukti_pembelian.setVisibility(View.GONE);
                        div.setVisibility(View.GONE);
                    }

                    if(text_status.getText().equals("Sedang Dikirim") || text_status.getText().equals("Pesanan Diterima")){
                        text_status.setTextColor(Color.parseColor("#FF00CD00"));
                    } else {
                        text_status.setTextColor(Color.parseColor("#FFEF8E00"));
                    }
                }
            }
        });
    }
}