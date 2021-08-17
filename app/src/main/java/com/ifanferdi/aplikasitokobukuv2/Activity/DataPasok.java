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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifanferdi.aplikasitokobukuv2.Adapter.PasokAdapter;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelPasok;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataPasok extends AppCompatActivity {

    BottomNavigationView navbar;
    FloatingActionButton btn_tambah;
    Button btn_back, btn_print;
    ProgressDialog pd;
    FirebaseFirestore fStore;
    List<ModelPasok> list, listCetak;
    RecyclerView rView;
    PasokAdapter pasokAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_pasok);

        btn_tambah = findViewById(R.id.btn_tambah);
        btn_back = findViewById(R.id.btn_back);
        navbar = findViewById(R.id.bottom_navbar);
        btn_print = findViewById(R.id.btn_print);

        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(DataPasok.this);
                builder.setTitle("Cetak Data Pasok");
                builder.setMessage("Apakah anda ingin mencetak data pasok?")
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

        pd = new ProgressDialog(this);

        fStore = FirebaseFirestore.getInstance();
        
        tambahPasok();
        tampilPasok();
        bottomNavbar();
        kembali();
    }
    
    private void getDataCetak(){
        pd.setTitle("Sedang mencetak data");
        pd.show();

        listCetak = new ArrayList<>();
        fStore.collection("Pasok").orderBy("tanggal", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                listCetak.clear();
                for(DocumentSnapshot doc: task.getResult()){
                    ModelPasok pasok = new ModelPasok(
                            doc.getString("id"),
                            doc.getString("judul_buku"),
                            doc.getString("nama_distributor"),
                            doc.getLong("jumlah"),
                            doc.getTimestamp("tanggal")
                    );
                    listCetak.add(pasok);
                }
                cetakData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DataPasok.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cetakData() {
        PdfDocument pdf = new PdfDocument();
        final Paint paint = new Paint();
        Paint titlePaint = new Paint();
        SimpleDateFormat tanggal = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        SimpleDateFormat tanggal_pasok = new SimpleDateFormat("dd MMMM yyyy");

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

        canvas.drawLine(40, 150, 1160, 150, paint);
        canvas.drawLine(40, 151, 1160, 151, paint);
        canvas.drawLine(40, 152, 1160, 152, paint);
        canvas.drawLine(40, 153, 1160, 153, paint);

        paint.setTextSize(30f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Laporan Pasok Buku", 1200/2, 190, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Tanggal : " + tanggal.format(new Date()), 40, 235, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("No", 50, 290, paint);
        canvas.drawText("Tanggal", 110, 290, paint);
        canvas.drawText("Jumlah", 290, 290, paint);
        canvas.drawText("Judul Buku", 420, 290, paint);
        canvas.drawText("Distributor", 920, 290, paint);
        canvas.drawLine(40, 255, 1160, 255, paint);
        canvas.drawLine(40, 305, 1160, 305, paint);
        canvas.drawLine(40, 255, 40, 305, paint);
        canvas.drawLine(100, 255, 100, 305, paint);
        canvas.drawLine(280, 255, 280, 305, paint);
        canvas.drawLine(410, 255, 410, 305, paint);
        canvas.drawLine(910, 255, 910, 305, paint);
        canvas.drawLine(1160, 255, 1160, 305, paint);

        int y = 290;
        int line1 = 255;
        int line2 = 305;

        for (int i=0; i < listCetak.size(); i++){
            y += 50;
            line1 += 50;
            line2 += 50;
            canvas.drawText(String.valueOf(i+1), 55, y, paint);
            canvas.drawText(tanggal_pasok.format(listCetak.get(i).getTanggal().toDate()), 110, y, paint);
            canvas.drawText(listCetak.get(i).getJumlah().toString() + " item", 290, y, paint);
            canvas.drawText(String.valueOf(listCetak.get(i).getJudul_buku()), 420, y, paint);
            canvas.drawText(String.valueOf(listCetak.get(i).getNama_distributor()), 920, y, paint);
            canvas.drawLine(40, line2, 1160, line2, paint);
            canvas.drawLine(40, line1, 40, line2, paint);
            canvas.drawLine(100, line1, 100, line2, paint);
            canvas.drawLine(280, line1, 280, line2, paint);
            canvas.drawLine(410, line1, 410, line2, paint);
            canvas.drawLine(910, line1, 910, line2, paint);
            canvas.drawLine(1160, line1, 1160, line2, paint);
        }

        pdf.finishPage(page);
        File file = new File(Environment.getExternalStorageDirectory(), "/Laporan Data Pasok.pdf");
        try {
            pdf.writeTo(new FileOutputStream(file));
            pd.dismiss();
            Toast.makeText(getApplicationContext(), "Berhasil mencetak data", Toast.LENGTH_SHORT).show();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private void bottomNavbar() {
        navbar.setSelectedItemId(R.id.pasok);

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

    public void hapusPasok(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi hapus data");
        builder.setMessage("Apakah anda yakin ingin menghapus data ini?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.setTitle("Sedang memuat data..");
                        pd.show();
                        fStore.collection("Pasok").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                tampilPasok();
                                pd.dismiss();
                                Toast.makeText(DataPasok.this, "Data Pasok berhasil dihapus!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DataPasok.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void tampilPasok() {
        rView = findViewById(R.id.pasok_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        pasokAdapter = new PasokAdapter(this, list);
        rView.setAdapter(pasokAdapter);pd.setTitle("Sedang memuat data");
        pd.show();
        fStore.collection("Pasok").orderBy("tanggal", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                list.clear();
                for(final QueryDocumentSnapshot pasok: task.getResult()){
                    pd.dismiss();
                    ModelPasok modelPasok = new ModelPasok(
                            pasok.getString("id"),
                            pasok.getString("judul_buku"),
                            pasok.getString("nama_distributor"),
                            pasok.getLong("jumlah"),
                            pasok.getTimestamp("tanggal")
                    );
                    list.add(modelPasok);
                }
                pasokAdapter.notifyDataSetChanged();
                pd.dismiss();
            }
        });
    }

    private void tambahPasok() {
        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DataPasok.this, FormPasok.class);
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
        tampilPasok();
    }

}