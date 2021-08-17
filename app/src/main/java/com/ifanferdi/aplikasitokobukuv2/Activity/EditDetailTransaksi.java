package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.util.HashMap;

public class EditDetailTransaksi extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private Button btn_back, btn_simpan;
    private Spinner spinner_status;
    private EditText text_resi;
    private Bundle bundle;

    String status, resi, no_transaksi;
    DetailTransaksi detailTransaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_detail_transaksi);

        fStore = FirebaseFirestore.getInstance();
        btn_back = findViewById(R.id.btn_back);
        btn_simpan = findViewById(R.id.btn_simpan);
        spinner_status = findViewById(R.id.spinner_status);
        text_resi = findViewById(R.id.text_resi);

        bundle = getIntent().getExtras();
        if(bundle != null){
            no_transaksi = bundle.getString("no_transaksi");
            status = bundle.getString("status");
            resi = bundle.getString("resi");

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_status.setAdapter(adapter);
            if (status != null) {
                int spinnerPosition = adapter.getPosition(status);
                spinner_status.setSelection(spinnerPosition);
            }

            text_resi.setText(resi);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String status = spinner_status.getSelectedItem().toString().trim();
                final String resi = text_resi.getText().toString().trim();

                HashMap<String, Object> data = new HashMap<>();
                data.put("status", status);
                data.put("resi", resi);

                fStore.collection("Transaksi").document(no_transaksi).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        Toast.makeText(EditDetailTransaksi.this, "Berhasil mengubah data transaksi", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditDetailTransaksi.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}