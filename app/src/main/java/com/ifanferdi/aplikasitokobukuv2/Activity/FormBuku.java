package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FormBuku extends AppCompatActivity {

    public static EditText text_kode;
    EditText text_judul;
    EditText text_noisbn;
    EditText text_penulis;
    EditText text_penerbit;
    EditText text_tahun;
    EditText text_stok;
    EditText text_kategori, text_harga, text_diskon, text_keterangan;
    TextView reset, judul_form;
    ImageView upload_buku;
    Uri image_uri;
    DataBuku dataBuku;
    Button btn_back, btn_simpan;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    private int IMG_REQUEST_ID = 10;

    String id_buku, kode_buku, judul_buku, noisbn_buku, penulis_buku, penerbit_buku, tahun_buku,
            stok_buku, kategori_buku, harga_buku, diskon_buku, keterangan_buku, image_buku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_buku);

        text_kode = findViewById(R.id.text_kodebuku);
        text_judul = findViewById(R.id.text_judul);
        text_noisbn = findViewById(R.id.text_noisbn);
        text_penulis = findViewById(R.id.text_penulis);
        text_penerbit = findViewById(R.id.text_penerbit);
        text_tahun = findViewById(R.id.text_tahun);
        text_stok = findViewById(R.id.text_stok);
        text_kategori = findViewById(R.id.text_kategori);
        text_harga = findViewById(R.id.text_harga);
        text_diskon = findViewById(R.id.text_diskon);
        text_keterangan = findViewById(R.id.text_keterangan);

        btn_simpan = findViewById(R.id.btn_simpan);
        btn_back = findViewById(R.id.btn_back);
        reset = findViewById(R.id.reset);

        upload_buku = findViewById(R.id.upload_buku);

        judul_form  = findViewById(R.id.judul_form);

        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        final Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            judul_form.setText("Ubah Data Buku");
            btn_simpan.setText("Ubah");

            id_buku = bundle.getString("id");
            kode_buku = bundle.getString("kode");
            judul_buku = bundle.getString("judul");
            noisbn_buku = bundle.getString("noisbn");
            penulis_buku = bundle.getString("penulis");
            penerbit_buku = bundle.getString("penerbit");
            tahun_buku = bundle.getString("tahun");
            stok_buku = bundle.getString("stok");
            kategori_buku = bundle.getString("kategori");
            harga_buku = bundle.getString("harga");
            diskon_buku = bundle.getString("diskon");
            keterangan_buku = bundle.getString("keterangan");
            image_buku = bundle.getString("image");

            text_kode.setText(kode_buku);
            text_judul.setText(judul_buku);
            text_noisbn.setText(noisbn_buku);
            text_penulis.setText(penulis_buku);
            text_penerbit.setText(penerbit_buku);
            text_tahun.setText(tahun_buku);
            text_kategori.setText(kategori_buku);
            text_harga.setText(harga_buku);
            text_stok.setText(stok_buku);
            text_diskon.setText(diskon_buku);
            text_keterangan.setText(keterangan_buku);
            Glide.with(this).load(image_buku).into(upload_buku);
        } else {
            judul_form.setText("Tambah Data Buku");
            btn_simpan.setText("Simpan");
            reset();
        }

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

        kembali();
        checkData();
    }

    private void reset() {
        text_kode.setText("");
        text_judul.setText("");
        text_noisbn.setText("");
        text_penulis.setText("");
        text_penerbit.setText("");
        text_tahun.setText("");
        text_kategori.setText("");
        text_harga.setText("");
        text_stok.setText("");
        text_diskon.setText("");
        text_keterangan.setText("");
        Drawable drawable = getResources().getDrawable(R.drawable.ic_image);
        upload_buku.setImageDrawable(drawable);
    }

    private void checkData(){
        pilihGambar();

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String kode, judul, noisbn, penulis, penerbit, tahun, kategori;
                final String stok, diskon, harga, keterangan;

                kode = text_kode.getText().toString().trim();
                judul = text_judul.getText().toString().trim();
                noisbn = text_noisbn.getText().toString().trim();
                penulis = text_penulis.getText().toString().trim();
                penerbit = text_penerbit.getText().toString().trim();
                tahun = text_tahun.getText().toString().trim();
                kategori = text_kategori.getText().toString().trim();
                harga = text_harga.getText().toString().trim();
                stok = text_stok.getText().toString().trim();
                diskon = text_diskon.getText().toString().trim();
                keterangan = text_keterangan.getText().toString().trim();

                if(kode.isEmpty()){
                    text_kode.setError("Mohon isi kode buku");
                } else if(judul.isEmpty()){
                    text_judul.setError("Mohon isi judul buku");
                } else if(noisbn.isEmpty()){
                    text_noisbn.setError("Mohon isi nomor ISBN");
                } else if(penulis.isEmpty()){
                    text_penulis.setError("Mohon isi penulis buku");
                } else if(penerbit.isEmpty()){
                    text_penerbit.setError("Mohon isi penerbit buku");
                } else if(tahun.isEmpty()){
                    text_tahun.setError("Mohon isi tahun terbit");
                } else if(stok.isEmpty()){
                    text_stok.setError("Mohon isi stok buku");
                } else if(kategori.isEmpty()){
                    text_kategori.setError("Mohon isi kategori buku");
                } else if(harga.isEmpty()){
                    text_harga.setError("Mohon isi harga buku");
                } else if(diskon.isEmpty()){
                    text_diskon.setError("Mohon isi diskon buku");
                } else if(keterangan.isEmpty()){
                    text_keterangan.setError("Mohon isi keterangan buku");
                } else {
                    Bundle bundle = getIntent().getExtras();
                    if(bundle !=  null){
                        if(image_uri != null){
                            ubahWithImage(id_buku, kode, judul, noisbn, penulis, penerbit, tahun, kategori, stok, diskon, harga, keterangan, image_uri);
                        } else {
                            ubah(id_buku, kode, judul, noisbn, penulis, penerbit, tahun, kategori, stok, diskon, harga, keterangan);
                        }
                    } else {
                        if(image_uri != null){
                            String id = UUID.randomUUID().toString();
                            simpan(id, kode, judul, noisbn, penulis, penerbit, tahun, kategori, stok, diskon, harga, keterangan, image_uri);
                        } else {
                            Toast.makeText(FormBuku.this, "Mohon masukkan gambar!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void pilihGambar() {
        upload_buku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent image = new Intent();
                image.setAction(Intent.ACTION_GET_CONTENT);
                image.setType("image/*");
                startActivityForResult(Intent.createChooser(image, "Pilih Gambar Buku"), IMG_REQUEST_ID);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMG_REQUEST_ID && resultCode == RESULT_OK && data != null && data.getData() != null){
            image_uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_uri);
                upload_buku.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void ubah(String id, String kode, String judul, String noisbn, String penulis, String penerbit,
                      String tahun, String kategori, String stok, String diskon, String harga, String keterangan) {
        progressDialog.setTitle("Sedang mengubah data..");
        progressDialog.show();

        Map<String,Object> buku = new HashMap<>();
        buku.put("id", id);
        buku.put("kode", kode);
        buku.put("judul", judul);
        buku.put("noisbn", noisbn);
        buku.put("penulis", penulis);
        buku.put("penerbit", penerbit);
        buku.put("tahun", tahun);
        buku.put("kategori", kategori);
        buku.put("stok", Long.parseLong(stok));
        buku.put("diskon", Long.parseLong(diskon));
        buku.put("harga", Long.parseLong(harga));
        buku.put("keterangan", keterangan);

        fStore.collection("Buku")
                .document(id)
                .update(buku)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FormBuku.this, "Berhasil mengubah data", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(dataBuku, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ubahWithImage(final String id, final String kode, final String judul, final String noisbn, final String penulis, final String penerbit,
                               final String tahun, final String kategori, final String stok, final String diskon, final String harga, final String keterangan, final Uri image_uri) {
        progressDialog.setTitle("Sedang mengubah data..");
        progressDialog.show();

        final StorageReference save_image = storageReference.child("Buku/" + id);

        save_image.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                save_image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.dismiss();

                        Map<String,Object> buku = new HashMap<>();
                        buku.put("id", id);
                        buku.put("kode", kode);
                        buku.put("judul", judul);
                        buku.put("noisbn", noisbn);
                        buku.put("penulis", penulis);
                        buku.put("penerbit", penerbit);
                        buku.put("tahun", tahun);
                        buku.put("kategori", kategori);
                        buku.put("stok", Long.parseLong(stok));
                        buku.put("diskon", Long.parseLong(diskon));
                        buku.put("harga", Long.parseLong(harga));
                        buku.put("keterangan", keterangan);
                        buku.put("image", uri.toString());

                        fStore.collection("Buku")
                                .document(id)
                                .update(buku)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(FormBuku.this, "Berhasil mengubah data", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FormBuku.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(FormBuku.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void simpan(final String id, final String kode, final String judul, final String noisbn, final String penulis, final String penerbit,
                        final String tahun, final String kategori, final String stok, final String diskon, final String harga, final String keterangan, final Uri image_uri) {
        progressDialog.setTitle("Sedang menyimpan data..");
        progressDialog.show();

        final StorageReference save_image = storageReference.child("Buku/" + id);

        try {
            save_image.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    save_image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressDialog.dismiss();

                            final ModelBuku buku = new ModelBuku(id, kode, judul, noisbn, penulis, penerbit, tahun,
                                    Long.parseLong(stok), kategori, Long.parseLong(harga), Long.parseLong(diskon), keterangan, uri.toString());

                            fStore.collection("Buku")
                                    .document(id)
                                    .set(buku)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(FormBuku.this, "Berhasil menyimpan data", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(FormBuku.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(FormBuku.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Toast.makeText(dataBuku, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void kembali(){
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}