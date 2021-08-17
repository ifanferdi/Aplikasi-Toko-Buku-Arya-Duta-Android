package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifanferdi.aplikasitokobukuv2.Adapter.TransaksiAdapter;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelDetailTransaksi;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelTransaksi;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataTransaksi extends AppCompatActivity {

    private BottomNavigationView navbar;
    private FloatingActionButton btn_tambah;
    private Button btn_back, btn_print;
    private ProgressDialog pd;
    FirebaseAuth fAuth;
    FirebaseUser user;
    private FirebaseFirestore fStore;
    private List<ModelTransaksi> list, listCetak;
    private List<ModelDetailTransaksi> listCetak2;
    private List<ModelBuku> listCetak3;
    private RecyclerView rView;
    private TransaksiAdapter transaksiAdapter;
    DecimalFormat decimalFormat;
    DecimalFormatSymbols decimalFormatSymbols;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transaksi);

        btn_back = findViewById(R.id.btn_back);
        navbar = findViewById(R.id.bottom_navbar);
        pd = new ProgressDialog(this);

        decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("Rp. ");
        decimalFormatSymbols.setMonetaryDecimalSeparator(',');
        decimalFormatSymbols.setGroupingSeparator('.');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

        rView = findViewById(R.id.transaksi_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        transaksiAdapter = new TransaksiAdapter(this, list);
        rView.setAdapter(transaksiAdapter);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_print = findViewById(R.id.btn_print);
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DataTransaksi.this);
                builder.setTitle("Cetak Data Transaksi");
                builder.setMessage("Apakah anda ingin mencetak data transaksi?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getDataCetak();
                            }
                        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).create().show();
            }
        });

        bottomNavbar();
    }

    public void tampilTransaksi() {
        pd.setTitle("Sedang memuat data");
        pd.show();
        fStore.collection("Transaksi").orderBy("waktu", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                list.clear();
                pd.dismiss();
                for(DocumentSnapshot doc: task.getResult()){
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
                transaksiAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DataTransaksi.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDataCetak(){
        pd.setTitle("Sedang mencetak data");
        pd.show();

        listCetak = new ArrayList<>();
        listCetak2 = new ArrayList<>();
        listCetak3 = new ArrayList<>();
        fStore.collection("DetailTransaksi").orderBy("no_transaksi", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                listCetak2.clear();
                for(final DocumentSnapshot document: task.getResult()){
                    ModelDetailTransaksi data = new ModelDetailTransaksi(
                            document.getString("id_detail_transaksi"),
                            document.getString("no_transaksi"),
                            document.getString("id_buku"),
                            document.getLong("harga"),
                            document.getLong("jumlah"),
                            document.getLong("total")
                    );
                    listCetak2.add(data);
                    listCetak3.clear();
                    fStore.collection("Buku").whereEqualTo("id", document.getString("id_buku")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot document2: task.getResult()) {
                                ModelBuku buku = new ModelBuku(
                                        document2.getString("id"),
                                        document2.getString("kode"),
                                        document2.getString("judul"),
                                        document2.getString("noisbn"),
                                        document2.getString("penulis"),
                                        document2.getString("penerbit"),
                                        document2.getString("tahun"),
                                        document2.getLong("stok"),
                                        document2.getString("kategori"),
                                        document2.getLong("harga"),
                                        document2.getLong("diskon"),
                                        document2.getString("keterangan"),
                                        document2.getString("image")
                                );
                                listCetak3.add(buku);
                                listCetak.clear();
                                fStore.collection("Transaksi")
                                        .whereEqualTo("no_transaksi", document.getString("no_transaksi"))
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (DocumentSnapshot doc: task.getResult()){
                                            ModelTransaksi transaksi = new ModelTransaksi(
                                                    doc.getString("no_transaksi"),
                                                    doc.getString("id_user"),
                                                    doc.getTimestamp("waktu"),
                                                    doc.getLong("total_bayar"),
                                                    doc.getString("status"),
                                                    doc.getString("bukti_pembayaran"),
                                                    doc.getString("resi")
                                            );
                                            listCetak.add(transaksi);
                                        }
                                        cetakData();
                                    }
                                });
                            }

                        }
                    });
                }
            }
        });
    }

    private void cetakData() {
        PdfDocument pdf = new PdfDocument();
        final Paint paint = new Paint();
        Paint titlePaint = new Paint();
        SimpleDateFormat tanggal = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        SimpleDateFormat tgl = new SimpleDateFormat("dd-MM-yyyy");

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);

        final Canvas canvas = page.getCanvas();

        titlePaint.setTextSize(35f);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("CV Arya Duta", 1200/2, 50, titlePaint);

        titlePaint.setTextSize(25f);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Jl. Revolusi No. 9, Kel. Sukamaju, Kec. Cilodong, Kota Depok", 1200/2, 85, titlePaint);
        canvas.drawText("Telp. (021) 87901520", 1200/2, 120, titlePaint);

        canvas.drawLine(0, 150, 1200, 150, paint);
        canvas.drawLine(0, 151, 1200, 151, paint);
        canvas.drawLine(0, 152, 1200, 152, paint);
        canvas.drawLine(0, 153, 1200, 153, paint);

        paint.setTextSize(30f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Laporan Data Transaksi", 1200/2, 190, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Tanggal : " + tanggal.format(new Date()), 0, 235, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("No", 10, 290, paint);
        canvas.drawText("Tanggal", 70, 290, paint);
        canvas.drawText("Judul Buku", 250, 290, paint);
        canvas.drawText("Jumlah", 750, 290, paint);
        canvas.drawText("Total Harga", 950, 290, paint);
        canvas.drawLine(0, 255, 1200, 255, paint);
        canvas.drawLine(0, 305, 1200, 305, paint);
        canvas.drawLine(0, 255, 0, 305, paint);
        canvas.drawLine(60, 255, 60, 305, paint);
        canvas.drawLine(230, 255, 230, 305, paint);
        canvas.drawLine(730, 255, 730, 305, paint);
        canvas.drawLine(930, 255, 930, 305, paint);
        canvas.drawLine(1200, 255, 1200, 305, paint);

        int y = 290;
        int line1 = 255;
        int line2 = 305;

        int x = 290;
        for(int i=0; i<listCetak.size(); i++){
            x += 50;
            canvas.drawText(tgl.format(listCetak.get(i).getWaktu().toDate()), 70, x, paint);
        }

        for(int j=0; j<listCetak2.size(); j++){
            y += 50;
            line1 += 50;
            line2 += 50;
            canvas.drawText(listCetak2.get(j).getJumlah().toString() + " item", 750, y, paint);
            canvas.drawText(decimalFormat.format(listCetak2.get(j).getTotal()), 950, y, paint);
            canvas.drawText(String.valueOf(j+1), 10, y, paint);
            canvas.drawLine(0, line2, 1200, line2, paint);
            canvas.drawLine(0, line1, 0, line2, paint);
            canvas.drawLine(60, line1, 60, line2, paint);
            canvas.drawLine(230, line1, 230, line2, paint);
            canvas.drawLine(730, line1, 730, line2, paint);
            canvas.drawLine(930, line1, 930, line2, paint);
            canvas.drawLine(1200, line1, 1200, line2, paint);
        }

        int z = 290;
        for(int k=0; k<listCetak3.size(); k++){
            z += 50;
            canvas.drawText(listCetak3.get(k).getJudul(), 250, z, paint);
        }

        pdf.finishPage(page);
        File file = new File(Environment.getExternalStorageDirectory(), "/Laporan Data Transaksi.pdf");
        try {
            pdf.writeTo(new FileOutputStream(file));
            pd.dismiss();
            Toast.makeText(getApplicationContext(), "Berhasil mencetak data", Toast.LENGTH_SHORT).show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void bottomNavbar() {
        navbar.setSelectedItemId(R.id.transaki);

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
                        finish();
                        startActivity(new Intent(getApplicationContext(), DataUser.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        tampilTransaksi();
    }
}