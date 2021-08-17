package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.io.IOException;
import java.util.HashMap;

public class UploadBuktiPembayaran extends AppCompatActivity {

    private Button btn_upload;
    private ImageView image_upload;
    private StorageReference storageReference;
    private FirebaseFirestore fStore;
    private Uri image_uri;
    private Bundle bundle;
    private String no_transaksi;
    private int IMG_REQUEST_ID = 10;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_bukti_pembayaran);

        btn_upload = findViewById(R.id.btn_upload);
        image_upload = findViewById(R.id.image_upload);
        pd = new ProgressDialog(this);
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        bundle = getIntent().getExtras();
        if(bundle != null){
            no_transaksi = bundle.getString("no_transaksi");

            btn_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(image_uri != null){
                        uploadImage(no_transaksi);
                    } else {
                        Toast.makeText(UploadBuktiPembayaran.this, "Mohon upload bukti pembayaran", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihGambar();
            }
        });
        pilihGambar();
    }

    private void uploadImage(final String no_transaksi) {
        pd.setTitle("Sedang menyimpan data");
        pd.show();

        final StorageReference save_image = storageReference.child("BuktiTransaksi/" + no_transaksi);

        try {
            save_image.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    save_image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            pd.dismiss();

                            HashMap<String, Object> data = new HashMap<>();
                            data.put("bukti_pembayaran", uri.toString());
                            data.put("status", "Menunggu Konfirmasi Pembayaran");
                            fStore.collection("Transaksi").document(no_transaksi).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    finish();
                                    Toast.makeText(UploadBuktiPembayaran.this, "Bukti pembayaran berhasil diupload", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(UploadBuktiPembayaran.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Toast.makeText(UploadBuktiPembayaran.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void pilihGambar() {
        Intent image = new Intent();
        image.setAction(Intent.ACTION_GET_CONTENT);
        image.setType("image/*");
        startActivityForResult(Intent.createChooser(image, "Pilih Gambar"), IMG_REQUEST_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMG_REQUEST_ID && resultCode == RESULT_OK && data != null && data.getData() != null){
            image_uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_uri);
                image_upload.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}