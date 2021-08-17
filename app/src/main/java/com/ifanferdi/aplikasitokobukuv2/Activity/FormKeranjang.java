package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelKeranjang;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.util.Date;
import java.util.UUID;


public class FormKeranjang extends DialogFragment {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    String id_buku;
    DetailKatalogBuku detailKatalogBuku;
    int harga_satuan;
    TextView jumlah;
    Button btn_keranjang, btn_minus, btn_plus;
    int jml_beli = 0;

    public FormKeranjang(FirebaseUser user, String id_buku, DetailKatalogBuku detailKatalogBuku, int harga_satuan) {
        this.user = user;
        this.id_buku = id_buku;
        this.detailKatalogBuku = detailKatalogBuku;
        this.harga_satuan = harga_satuan;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.form_keranjang, container, false);

        btn_minus = view.findViewById(R.id.btn_minus);
        btn_plus = view.findViewById(R.id.btn_plus);
        jumlah = view.findViewById(R.id.text_jumlah);
        btn_keranjang = view.findViewById(R.id.btn_keranjang);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        jumlah.setText(""+jml_beli);

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jml_beli++;
                jumlah.setText(""+jml_beli);
            }
        });
        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(jumlah.getText().equals("0")){
                    jml_beli = 0;
                } else {
                    jml_beli--;
                }
                jumlah.setText(""+jml_beli);
            }
        });

        btn_keranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(jumlah.getText().equals("0")){
                    Toast.makeText(detailKatalogBuku, "Masukkan jumlah pembelian", Toast.LENGTH_SHORT).show();
                } else {

                    fStore.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            final String idUser = documentSnapshot.getString("id");
                            fStore.collection("Buku").whereEqualTo("id", id_buku).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for(QueryDocumentSnapshot document : task.getResult()){
                                            String id = UUID.randomUUID().toString();
                                            Long jml = Long.parseLong(jumlah.getText().toString());
                                            Long harga  = document.getLong("harga") - (document.getLong("harga") * document.getLong("diskon") / 100);
                                            Timestamp waktu = new Timestamp(new Date());
                                            Long total_harga = jml * harga_satuan;

                                            final ModelKeranjang keranjang =
                                                    new ModelKeranjang(id, idUser, id_buku, harga, jml, total_harga, waktu);

                                            fStore.collection("Keranjang")
                                                    .document(id)
                                                    .set(keranjang)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(detailKatalogBuku, "Keranjang telah ditambahkan", Toast.LENGTH_SHORT).show();
                                                            dismiss();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(detailKatalogBuku, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    });




                }
            }
        });

        return view;
    }

    public void onStart(){
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null){
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
