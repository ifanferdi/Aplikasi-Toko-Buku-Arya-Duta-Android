package com.ifanferdi.aplikasitokobukuv2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifanferdi.aplikasitokobukuv2.R;

public class LihatBuktiPembayaran extends AppCompatActivity {

    private ImageView image_bukti;
    private Button btn_back;
    private Bundle bundle;
    private String no_transaksi;
    private FirebaseFirestore fStore;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_bukti_pembayaran);

        image_bukti = findViewById(R.id.image_bukti);
        btn_back = findViewById(R.id.btn_back);
        fStore = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);
        pd.setTitle("Sedang memuat data");
        pd.show();

        bundle = getIntent().getExtras();
        if(bundle != null){
            no_transaksi = bundle.getString("no_transaksi");

            fStore.collection("Transaksi").whereEqualTo("no_transaksi", no_transaksi).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot doc: task.getResult()){
                        String bukti = doc.getString("bukti_pembayaran");
                        Glide.with(getApplicationContext()).load(bukti).into(image_bukti);
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LihatBuktiPembayaran.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}