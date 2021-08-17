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
import com.google.firebase.firestore.QuerySnapshot;
import com.ifanferdi.aplikasitokobukuv2.Adapter.BukuAdapter;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelDistributor;
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

public class DataBuku extends AppCompatActivity {

    BottomNavigationView navbar;
    FloatingActionButton btn_tambah;
    Button btn_back, btn_print;
    ProgressDialog pd;
    FirebaseFirestore fStore;
    List<ModelBuku> list, listCetak;
    RecyclerView rView;
    BukuAdapter bukuAdapter;
    DecimalFormat decimalFormat;
    DecimalFormatSymbols decimalFormatSymbols;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_buku);

        btn_tambah = findViewById(R.id.btn_tambah);
        btn_back = findViewById(R.id.btn_back);
        navbar = findViewById(R.id.bottom_navbar);
        pd = new ProgressDialog(this);

        decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("Rp. ");
        decimalFormatSymbols.setMonetaryDecimalSeparator(',');
        decimalFormatSymbols.setGroupingSeparator('.');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

        rView = findViewById(R.id.buku_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));

        fStore = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        bukuAdapter = new BukuAdapter(this, list);
        rView.setAdapter(bukuAdapter);

        btn_print = findViewById(R.id.btn_print);
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DataBuku.this);
                builder.setTitle("Cetak Data Stok Buku");
                builder.setMessage("Apakah anda ingin mencetak data stok buku?")
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

        tambahBuku();
        tampilBuku();
        kembali();
        bottomNavbar();
    }

    private void getDataCetak(){
        pd.setTitle("Sedang mencetak data");
        pd.show();

        listCetak = new ArrayList<>();
        fStore.collection("Buku").orderBy("judul").whereNotEqualTo("judul", "- Pilih Buku -").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                listCetak.clear();
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
                    listCetak.add(buku);
                }
                cetakData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DataBuku.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cetakData() {
        PdfDocument pdf = new PdfDocument();
        final Paint paint = new Paint();
        Paint titlePaint = new Paint();
        SimpleDateFormat tanggal = new SimpleDateFormat("EEEE, dd MMMM yyyy");

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1400, 2010, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);

        final Canvas canvas = page.getCanvas();

        titlePaint.setTextSize(35f);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("CV Arya Duta", 1400/2, 50, titlePaint);

        titlePaint.setTextSize(25f);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Jl. Revolusi No. 9, Kel. Sukamaju, Kec. Cilodong, Kota Depok", 1400/2, 85, titlePaint);
        canvas.drawText("Telp. (021) 87901520", 1400/2, 120, titlePaint);

        canvas.drawLine(0, 150, 1400, 150, paint);
        canvas.drawLine(0, 151, 1400, 151, paint);
        canvas.drawLine(0, 152, 1400, 152, paint);
        canvas.drawLine(0, 153, 1400, 153, paint);

        paint.setTextSize(30f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Laporan Stok Buku", 1400/2, 190, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Tanggal : " + tanggal.format(new Date()), 0, 235, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("No", 10, 290, paint);
        canvas.drawText("No ISBN", 80, 290, paint);
        canvas.drawText("Judul Buku", 370, 290, paint);
        canvas.drawText("Stok", 960, 290, paint);
        canvas.drawText("Harga", 1120, 290, paint);
        canvas.drawText("Diskon", 1300, 290, paint);
        canvas.drawLine(0, 255, 1400, 255, paint);
        canvas.drawLine(0, 305, 1400, 305, paint);
        canvas.drawLine(0, 255, 0, 305, paint);
        canvas.drawLine(70, 255, 70, 305, paint);
        canvas.drawLine(360, 255, 360, 305, paint);
        canvas.drawLine(950, 255, 950, 305, paint);
        canvas.drawLine(1110, 255, 1110, 305, paint);
        canvas.drawLine(1290, 255, 1290, 305, paint);
        canvas.drawLine(1400, 255, 1400, 305, paint);

        int y = 290;
        int line1 = 255;
        int line2 = 305;

        for (int i=0; i < listCetak.size(); i++){
            y += 50;
            line1 += 50;
            line2 += 50;
            canvas.drawText(String.valueOf(i+1), 10, y, paint);
            canvas.drawText(listCetak.get(i).getNoisbn(), 80, y, paint);
            canvas.drawText(listCetak.get(i).getJudul(), 370, y, paint);
            canvas.drawText(listCetak.get(i).getStok().toString() + " item", 960, y, paint);
            canvas.drawText(decimalFormat.format(listCetak.get(i).getHarga()), 1120, y, paint);
            canvas.drawText(listCetak.get(i).getDiskon().toString() + "%", 1300, y, paint);
            canvas.drawLine(0, line2, 1400, line2, paint);
            canvas.drawLine(0, line1, 0, line2, paint);
            canvas.drawLine(70, line1, 70, line2, paint);
            canvas.drawLine(360, line1, 360, line2, paint);
            canvas.drawLine(950, line1, 950, line2, paint);
            canvas.drawLine(1110, line1, 1110, line2, paint);
            canvas.drawLine(1290, line1, 1290, line2, paint);
            canvas.drawLine(1400, line1, 1400, line2, paint);
        }

        pdf.finishPage(page);
        File file = new File(Environment.getExternalStorageDirectory(), "/Laporan Stok Buku.pdf");
        try {
            pdf.writeTo(new FileOutputStream(file));
            pd.dismiss();
            Toast.makeText(getApplicationContext(), "Berhasil mencetak data", Toast.LENGTH_SHORT).show();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private void bottomNavbar() {
        navbar.setSelectedItemId(R.id.buku);

        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.buku:
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

    public void hapusBuku(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi hapus data");
        builder.setMessage("Apakah anda yakin ingin menghapus data ini?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.setTitle("Sedang memuat data..");
                        pd.show();
                        fStore.collection("Buku").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.dismiss();
                                Toast.makeText(DataBuku.this, "Data buku berhasil dihapus!", Toast.LENGTH_SHORT).show();
                                tampilBuku();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DataBuku.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void tampilBuku() {
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
                        bukuAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DataBuku.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tambahBuku() {
        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DataBuku.this, FormBuku.class);
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
        tampilBuku();
    }
}