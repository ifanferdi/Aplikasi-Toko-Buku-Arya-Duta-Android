package com.ifanferdi.aplikasitokobukuv2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.Table;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifanferdi.aplikasitokobukuv2.Adapter.BukuAdapter;
import com.ifanferdi.aplikasitokobukuv2.Adapter.DistributorAdapter;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelDistributor;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataDistributor extends AppCompatActivity {

    BottomNavigationView navbar;
    FloatingActionButton btn_tambah;
    Button btn_back, btn_print;
    ProgressDialog pd;
    FirebaseFirestore fStore;
    List<ModelDistributor> list, listCetak;
    RecyclerView rView;
    DistributorAdapter distributorAdapter;

    String[] dataPrint;

    private BaseAdapter printAdapter = new BaseAdapter(){

        @Override
        public int getCount() {
            return listCetak.size();
        }

        @Override
        public Object getItem(int i) {
            return listCetak.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ModelDistributor data = listCetak.get(i);

            return view;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_distributor);

        btn_tambah = findViewById(R.id.btn_tambah);
        btn_back = findViewById(R.id.btn_back);
        navbar = findViewById(R.id.bottom_navbar);

        pd = new ProgressDialog(this);

        rView = findViewById(R.id.distributor_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));

        fStore = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        distributorAdapter = new DistributorAdapter(this, list);
        rView.setAdapter(distributorAdapter);

        btn_print = findViewById(R.id.btn_print);
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DataDistributor.this);
                builder.setTitle("Cetak Data Distributor");
                builder.setMessage("Apakah anda ingin mencetak data distributor?")
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

        tambahDistributor();
        tampilDistributor();
        bottomNavbar();
        kembali();
    }

    private void getDataCetak(){
        pd.setTitle("Sedang mencetak data");
        pd.show();

        listCetak = new ArrayList<>();
        fStore.collection("Distributor").orderBy("nama_distributor").whereNotEqualTo("nama_distributor", "- Pilih Distributor -").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                listCetak.clear();
                for(DocumentSnapshot doc: task.getResult()){
                    ModelDistributor distributor = new ModelDistributor(
                            doc.getString("id"),
                            doc.getString("nama_distributor"),
                            doc.getString("notelp"),
                            doc.getString("alamat")
                    );
                    listCetak.add(distributor);
                }
                cetakData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DataDistributor.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cetakData() {
        PdfDocument pdf = new PdfDocument();
        final Paint paint = new Paint();
        Paint titlePaint = new Paint();
        SimpleDateFormat tanggal = new SimpleDateFormat("EEEE, dd MMMM yyyy");

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
        canvas.drawText("Laporan Data Distributor", 1200/2, 190, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Tanggal : " + tanggal.format(new Date()), 0, 235, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("No", 10, 290, paint);
        canvas.drawText("Nama Distributor", 70, 290, paint);
        canvas.drawText("No Telepon", 450, 290, paint);
        canvas.drawText("Alamat", 680, 290, paint);
        canvas.drawLine(0, 255, 1200, 255, paint);
        canvas.drawLine(0, 305, 1200, 305, paint);
        canvas.drawLine(0, 255, 0, 305, paint);
        canvas.drawLine(60, 255, 60, 305, paint);
        canvas.drawLine(440, 255, 440, 305, paint);
        canvas.drawLine(670, 255, 670, 305, paint);
        canvas.drawLine(1200, 255, 1200, 305, paint);

        int y = 290;
        int line1 = 255;
        int line2 = 305;

        for (int i=0; i < listCetak.size(); i++){
            y += 50;
            line1 += 50;
            line2 += 50;
            canvas.drawText(String.valueOf(i+1), 15, y, paint);
            canvas.drawText(String.valueOf(listCetak.get(i).getNama_distributor()), 70, y, paint);
            canvas.drawText(String.valueOf(listCetak.get(i).getNotelp()), 450, y, paint);
            canvas.drawText(String.valueOf(listCetak.get(i).getAlamat()), 680, y, paint);
            canvas.drawLine(0, line2, 1200, line2, paint);
            canvas.drawLine(0, line1, 0, line2, paint);
            canvas.drawLine(60, line1, 60, line2, paint);
            canvas.drawLine(440, line1, 440, line2, paint);
            canvas.drawLine(670, line1, 670, line2, paint);
            canvas.drawLine(1200, line1, 1200, line2, paint);
        }

        pdf.finishPage(page);
        File file = new File(Environment.getExternalStorageDirectory(), "/Laporan Data Distributor.pdf");
        try {
            pdf.writeTo(new FileOutputStream(file));
            pd.dismiss();
            Toast.makeText(getApplicationContext(), "Berhasil mencetak data", Toast.LENGTH_SHORT).show();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private void bottomNavbar() {
        navbar.setSelectedItemId(R.id.distributor);

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

    public void hapusDistributor(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi hapus data");
        builder.setMessage("Apakah anda yakin ingin menghapus data ini?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.setTitle("Sedang memuat data..");
                        pd.show();
                        fStore.collection("Distributor").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                tampilDistributor();
                                pd.dismiss();
                                Toast.makeText(DataDistributor.this, "Data distributor berhasil dihapus!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DataDistributor.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void tampilDistributor() {
        pd.setTitle("Sedang memuat data");
        pd.show();
        fStore.collection("Distributor").orderBy("nama_distributor").whereNotEqualTo("nama_distributor", "- Pilih Distributor -").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        pd.dismiss();
                        for(DocumentSnapshot doc: task.getResult()){
                            ModelDistributor distributor = new ModelDistributor(
                                    doc.getString("id"),
                                    doc.getString("nama_distributor"),
                                    doc.getString("notelp"),
                                    doc.getString("alamat")
                            );
                            list.add(distributor);
                        }
                        distributorAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DataDistributor.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tambahDistributor() {
        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DataDistributor.this, FormDistributor.class);
                startActivity(intent);
            }
        });
    }

    private void kembali(){
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void onStart(){
        super.onStart();
        tampilDistributor();
    }
}