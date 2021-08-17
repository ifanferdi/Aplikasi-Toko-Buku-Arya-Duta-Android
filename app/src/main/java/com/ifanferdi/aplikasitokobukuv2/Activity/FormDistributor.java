package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelDistributor;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FormDistributor extends AppCompatActivity {

    EditText text_nama;
    EditText text_notelp;
    EditText text_alamat;
    TextView reset;
    TextView judul_form;
    ProgressDialog progressDialog;
    Button btn_simpan;
    Button btn_back;

    FirebaseFirestore fStore ;

    Bundle bundle;
    String id_distributor, nama_distributor, notelp_distributor, alamat_distributor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_distributor);

        text_nama = findViewById(R.id.text_namadistributor);
        text_notelp = findViewById(R.id.text_notelp);
        text_alamat = findViewById(R.id.text_alamat);
        reset = findViewById(R.id.reset);
        judul_form = findViewById(R.id.judul_form);
        progressDialog = new ProgressDialog(this);
        btn_simpan = findViewById(R.id.btn_simpan);
        btn_back = findViewById(R.id.btn_back);

        bundle = getIntent().getExtras();

        fStore = FirebaseFirestore.getInstance();

        if(bundle != null){
            judul_form.setText("Ubah Data Distributor");
            btn_simpan.setText("Ubah");

            id_distributor = bundle.getString("id");
            nama_distributor  = bundle.getString("nama_distributor");
            notelp_distributor = bundle.getString("notelp");
            alamat_distributor = bundle.getString("alamat");

            text_nama.setText(nama_distributor);
            text_notelp.setText(notelp_distributor);
            text_alamat.setText(alamat_distributor);
        } else {
            judul_form.setText("Tambah Data Distributor");
            btn_simpan.setText("Simpan");
            reset();
        }

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkData();
    }

    private void checkData() {
        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nama, notelp, alamat;

                nama = text_nama.getText().toString().trim();
                notelp = text_notelp.getText().toString().trim();
                alamat = text_alamat.getText().toString().trim();

                if(nama.isEmpty()){
                    text_nama.setError("Mohon isi nama distributor");
                } else if(notelp.isEmpty()){
                    text_notelp.setError("Mohon isi no telepon distributor");
                } else if(alamat.isEmpty()){
                    text_alamat.setError("Mohon isi alamat distributor");
                } else {
                    if(bundle != null){
                        ubah(id_distributor, nama, notelp, alamat);
                    } else {
                        String id = UUID.randomUUID().toString();
                        simpan(id, nama, notelp, alamat);
                    }
                }
            }
        });
    }

    private void simpan(String id, String nama, String notelp, String alamat) {
        progressDialog.setTitle("Sedang menyimpan data..");
        progressDialog.show();

        final ModelDistributor distributor = new ModelDistributor(id, nama, notelp, alamat);

        fStore.collection("Distributor").document(id).set(distributor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(FormDistributor.this, "Berhasil menyimpan data", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FormDistributor.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ubah(String id, String nama, String notelp, String alamat) {
        progressDialog.setTitle("Sedang mengubah data..");
        progressDialog.show();

        Map<String, Object> distributor = new HashMap<>();
        distributor.put("id", id);
        distributor.put("nama_distributor", nama);
        distributor.put("notelp", notelp);
        distributor.put("alamat", alamat);

        fStore.collection("Distributor").document(id).update(distributor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(FormDistributor.this, "Berhasil mengubah data", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FormDistributor.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void reset(){
        text_nama.setText("");
        text_notelp.setText("");
        text_alamat.setText("");
    }
}