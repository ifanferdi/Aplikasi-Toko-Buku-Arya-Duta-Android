package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfilPengguna extends AppCompatActivity {

    BottomNavigationView navbar;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    View divider;
    LayoutInflater inflater;
    AlertDialog.Builder dialog;
    RadioGroup group_jk;
    RadioButton laki, perempuan;
    Uri image_uri;
    ImageView image_user,text_cart;
    Button btn_cart;

    ProgressDialog pd;
    private int IMG_REQUEST_ID = 10;

    TextView judul;
    EditText email, nama, kelamin, telp, alamat;

    Button simpan, batal, ubahProfil, ubahPassword, keluar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_pengguna);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = fAuth.getCurrentUser();

        navbar = findViewById(R.id.bottom_navbar);
        pd = new ProgressDialog(this);
        text_cart = findViewById(R.id.text_cart);

        judul = findViewById(R.id.textView2);
        email = findViewById(R.id.text_email);
        nama = findViewById(R.id.text_nama);
        telp = findViewById(R.id.text_telp);
        alamat = findViewById(R.id.text_alamat);
        kelamin = findViewById(R.id.text_kelamin);
        simpan = findViewById(R.id.btn_simpan);
        batal = findViewById(R.id.btn_batal);
        ubahProfil = findViewById(R.id.btn_ubahprofil);
        ubahPassword = findViewById(R.id.btn_ubahpassword);
        keluar = findViewById(R.id.btn_keluar);
        divider = findViewById(R.id.divider10);
        group_jk = findViewById(R.id.group_jk);
        laki = findViewById(R.id.radio_laki);
        perempuan = findViewById(R.id.radio_perempuan);
        btn_cart = findViewById(R.id.btn_cart);

        image_user = findViewById(R.id.upload_user);
        
        dialog = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Keranjang.class));
            }
        });

        hidden();
        bottomNavbar();
        ubahProfilPengguna();
        logout();
        dataPengguna();
        batalUbahProfil();
        simpanUbahProfil();
        ubahPasswordPengguna();
    }

    private void hidden() {
        email.setEnabled(false);
        nama.setEnabled(false);
        kelamin.setEnabled(false);
        telp.setEnabled(false);
        alamat.setEnabled(false);
        email.setTextColor(Color.parseColor("#616161"));
        nama.setTextColor(Color.parseColor("#616161"));
        kelamin.setTextColor(Color.parseColor("#616161"));
        telp.setTextColor(Color.parseColor("#616161"));
        alamat.setTextColor(Color.parseColor("#616161"));
        simpan.setVisibility(View.GONE);
        batal.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
        group_jk.setVisibility(View.GONE);
        kelamin.setVisibility(View.VISIBLE);
        ubahPassword.setEnabled(true);
        keluar.setEnabled(true);
        email.setFocusable(false);
        nama.setFocusable(false);
        kelamin.setFocusable(false);
        telp.setFocusable(false);
        alamat.setFocusable(false);
    }

    private void simpanUbahProfil() {

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailBaru, namaBaru, telpBaru, alamatBaru, kelaminBaru;

                emailBaru = email.getText().toString().trim();
                namaBaru = nama.getText().toString().trim();
                telpBaru = telp.getText().toString().trim();
                alamatBaru = alamat.getText().toString().trim();

                final int jk = group_jk.getCheckedRadioButtonId();

                if(emailBaru.isEmpty()){
                    email.setError("Mohon isi email anda");
                } else if(namaBaru.isEmpty()){
                    nama.setError("Mohon isi nama anda");
                } else if(telpBaru.isEmpty()){
                    telp.setError("Mohon isi no telepon anda");
                } else if(alamatBaru.isEmpty()){
                    alamat.setError("Mohon isi alamat anda");
                } else if(!(jk == laki.getId()) && !(jk == perempuan.getId())){
                    Toast.makeText(ProfilPengguna.this, "Mohon pilih jenis kelamin anda", Toast.LENGTH_SHORT).show();
                } else {
                    if(jk == laki.getId()){
                        kelaminBaru = laki.getText().toString().trim();
                    } else {
                        kelaminBaru = perempuan.getText().toString().trim();
                    }
                    pd.setTitle("Sedang mengubah data..");
                    pd.show();
                    
                    if(image_uri != null){
                        ubahWithImage(emailBaru, namaBaru, telpBaru, alamatBaru, kelaminBaru);
                    } else {
                        ubah(emailBaru, namaBaru, telpBaru, alamatBaru, kelaminBaru);
                    }
                }
            }
        });

    }

    private void ubah(String emailBaru, String namaBaru, String telpBaru, String alamatBaru, String kelaminBaru) {
        Map<String,Object> user = new HashMap<>();
        user.put("email", emailBaru);
        user.put("nama", namaBaru);
        user.put("notelp", telpBaru);
        user.put("alamat", alamatBaru);
        user.put("jenis_kelamin", kelaminBaru);

        fStore.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Data berhasil diubah!", Toast.LENGTH_SHORT).show();
                        dataPengguna();
                        hidden();
                    }
                });
    }

    private void ubahWithImage(final String emailBaru, final String namaBaru, final String telpBaru, final String alamatBaru, final String kelaminBaru) {

        final StorageReference save_image = storageReference.child("ProfilePicture/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        save_image.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                save_image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final Map<String,Object> user = new HashMap<>();
                        user.put("email", emailBaru);
                        user.put("nama", namaBaru);
                        user.put("notelp", telpBaru);
                        user.put("alamat", alamatBaru);
                        user.put("jenis_kelamin", kelaminBaru);
                        user.put("image", uri.toString());

                        fStore.collection("Users")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .update(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(), "Data berhasil diubah!", Toast.LENGTH_SHORT).show();
                                        dataPengguna();
                                        hidden();
                                    }
                                });
                    }
                });
            }
        });
    }

    private void ubahPasswordPengguna() {
        ubahPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UbahPassword.class));
            }
        });
    }

    private void batalUbahProfil() {
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataPengguna();
                hidden();
            }
        });
    }

    private void logout(){
        keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ProfilPengguna.this);
                builder.setTitle("Konfirmasi Logout");
                builder.setMessage("Apakah anda yakin ingin keluar?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                fAuth.signOut();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }
                        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).create().show();
            }
        });
    }

    private void ubahProfilPengguna() {
        ubahProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nama.isEnabled()){
                    hidden();
                } else {
                    pilihGambar();
                    email.setFocusableInTouchMode(true);
                    nama.setFocusableInTouchMode(true);
                    telp.setFocusableInTouchMode(true);
                    alamat.setFocusableInTouchMode(true);
                    email.setEnabled(false);
                    nama.setEnabled(true);
                    kelamin.setEnabled(true);
                    telp.setEnabled(true);
                    alamat.setEnabled(true);
                    email.setTextColor(Color.BLACK);
                    nama.setTextColor(Color.BLACK);
                    kelamin.setTextColor(Color.BLACK);
                    telp.setTextColor(Color.BLACK);
                    alamat.setTextColor(Color.BLACK);
                    simpan.setVisibility(View.VISIBLE);
                    batal.setVisibility(View.VISIBLE);
                    divider.setVisibility(View.VISIBLE);
                    group_jk.setVisibility(View.VISIBLE);
                    kelamin.setVisibility(View.GONE);
                    if(kelamin.getText().toString().equals("Laki-laki")){
                        laki.setChecked(true);
                    } else {
                        perempuan.setChecked(true);
                    }
                    ubahPassword.setEnabled(false);
                    keluar.setEnabled(false);
                }
            }
        });
    }

    private void dataPengguna() {
        FirebaseUser user = fAuth.getCurrentUser();
        if(user != null){
            DocumentReference df = fStore.collection("Users").document(user.getUid());
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d("TAG", "onSuccess" + documentSnapshot.getData());
                    email.setText(documentSnapshot.getString("email"));
                    nama.setText(documentSnapshot.getString("nama"));
                    kelamin.setText(documentSnapshot.getString("jenis_kelamin"));
                    telp.setText(documentSnapshot.getString("notelp"));
                    alamat.setText(documentSnapshot.getString("alamat"));
                    Glide.with(ProfilPengguna.this).load(documentSnapshot.getString("image")).into(image_user);
                }
            });
        }
    }

    private void bottomNavbar() {
        navbar.setSelectedItemId(R.id.profil);

        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        finish();
                        startActivity(new Intent(getApplicationContext(), KatalogBuku.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.riwayat:
                        finish();
                        startActivity(new Intent(getApplicationContext(), RiwayatTransaksi.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profil:
                        return true;
                }
                return false;
            }
        });
    }

    private void pilihGambar() {
        image_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent image = new Intent();
                image.setAction(Intent.ACTION_GET_CONTENT);
                image.setType("image/*");
                startActivityForResult(Intent.createChooser(image, "Pilih Gambar"), IMG_REQUEST_ID);
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
                image_user.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cart(){
        text_cart.setVisibility(View.GONE);
        fStore.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                final String idUser = documentSnapshot.getString("id");

                fStore.collection("Keranjang")
                        .whereEqualTo("id_user", idUser)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(DocumentSnapshot document : task.getResult()){
                                        if(!document.getLong("jumlah").toString().isEmpty()){
                                            text_cart.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }
                        });
            }
        });
    }

    protected void onStart() {
        super.onStart();
        cart();
    }
}