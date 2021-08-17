package com.ifanferdi.aplikasitokobukuv2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelDistributor;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelPasok;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FormPasok extends AppCompatActivity {

    TextView reset;
    EditText text_jumlah;
    FirebaseFirestore fStore;
    ProgressDialog progressDialog;
    Bundle bundle;
    Button btn_simpan, btn_back;

    String distributor, judulBuku, idBuku;

    private Spinner spinner_distributor, spinner_buku;
    private List<ModelBuku> bukuList;
    private List<ModelDistributor> distributorList;

    private BaseAdapter sipnnerAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return distributorList.size();
        }

        @Override
        public Object getItem(int i) {
            return distributorList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            SpinnerHolder holder;
            View spinnerView = view;

            if(spinnerView == null){
                spinnerView = getLayoutInflater().inflate(R.layout.spinner_form_pasok, viewGroup, false);
                holder = new SpinnerHolder();
                holder.nama = spinnerView.findViewById(R.id.text_nama);
                spinnerView.setTag(holder);
            } else {
                holder = (SpinnerHolder) spinnerView.getTag();
            }

            final ModelDistributor data = distributorList.get(i);
            holder.nama.setText(data.getNama_distributor());

            spinner_distributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(distributorList.get(i).getNama_distributor().equals("- Pilih Distributor -")){
                        distributor = "";
                    } else {
                        distributor = distributorList.get(i).getNama_distributor();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    distributor = "";
                }
            });

            return spinnerView;
        }

        class SpinnerHolder{
            private TextView nama;
        }
    };

    private BaseAdapter spinnerBukuAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return bukuList.size();
        }

        @Override
        public Object getItem(int i) {
            return bukuList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            SpinnerBukuHolder holder;
            View sView = view;

            if(sView == null){
                sView = getLayoutInflater().inflate(R.layout.spinner_form_buku, viewGroup, false);
                holder = new SpinnerBukuHolder();
                holder.judul = sView.findViewById(R.id.judulBuku);
                sView.setTag(holder);
            } else {
                holder = (SpinnerBukuHolder) sView.getTag();
            }

            final ModelBuku data = bukuList.get(i);
            holder.judul.setText(data.getJudul());

            spinner_buku.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(bukuList.get(i).getJudul().equals("- Pilih Buku -")){
                        judulBuku = "";
                    } else {
                        judulBuku = bukuList.get(i).getJudul();
                        idBuku = bukuList.get(i).getId();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    judulBuku = "";
                }
            });

            return sView;
        }

        class SpinnerBukuHolder {
            private TextView judul;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pasok);

        fStore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        btn_back = findViewById(R.id.btn_back);
        btn_simpan = findViewById(R.id.btn_simpan);
        text_jumlah = findViewById(R.id.text_jumlah);
        reset = findViewById(R.id.reset);

        bundle = getIntent().getExtras();


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

        DistributorList();
        BukuList();
        reset();
        checkData();
    }

    private void BukuList() {
        bukuList = new ArrayList<>();
        spinner_buku = findViewById(R.id.spinner_buku);
        spinner_buku.setAdapter(spinnerBukuAdapter);
        fStore.collection("Buku").orderBy("judul").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                bukuList.clear();
                for(DocumentSnapshot doc: task.getResult()){
                    ModelBuku modelBuku = new ModelBuku(
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
                    bukuList.add(modelBuku);
                }
                spinnerBukuAdapter.notifyDataSetChanged();
            }
        });
    }

    private void DistributorList() {
        distributorList = new ArrayList<>();
        spinner_distributor = findViewById(R.id.spinner_distributor);
        spinner_distributor.setAdapter(sipnnerAdapter);
        fStore.collection("Distributor").orderBy("nama_distributor").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                distributorList.clear();
                for(DocumentSnapshot distributor: task.getResult()){
                    ModelDistributor modelDistributor = new ModelDistributor(
                            distributor.getString("id"),
                            distributor.getString("nama_distributor"),
                            distributor.getString("notelp"),
                            distributor.getString("alamat")
                    );
                    distributorList.add(modelDistributor);
                }
                sipnnerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void reset() {
        text_jumlah.setText("");
        DistributorList();
        BukuList();
    }

    private void checkData() {
        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id, jumlah;
                final Timestamp tanggal;

                id = UUID.randomUUID().toString();
                jumlah = text_jumlah.getText().toString().trim();
                tanggal = new Timestamp(new Date());
                if(judulBuku.isEmpty()){
                    Toast.makeText(FormPasok.this, "Pilih Buku", Toast.LENGTH_SHORT).show();
                } else if(distributor.isEmpty()){
                    Toast.makeText(FormPasok.this, "Pilih Distributor", Toast.LENGTH_SHORT).show();
                } else if(jumlah.isEmpty()){
                    text_jumlah.setError("Mohon isi jumlah pasok buku");
                } else {
                    simpan(id, judulBuku, Integer.parseInt(jumlah), tanggal, distributor, idBuku);
                }
            }
        });
    }

    private void simpan(final String id, final String judulBuku, final int jumlah, final Timestamp tanggal, final String distributor, final String idBuku) {
        progressDialog.setTitle("Sedang menyimpan data..");
        progressDialog.show();

        fStore.collection("Buku").whereEqualTo("id", idBuku).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc: task.getResult()){
                    int stok = Integer.parseInt(doc.getLong("stok").toString());
                    final int stok_baru = stok + jumlah;
                    ModelPasok pasok = new ModelPasok(id, judulBuku, distributor, Long.parseLong(String.valueOf(jumlah)), tanggal);

                    fStore.collection("Pasok").document(id).set(pasok).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            fStore.collection("Buku").document(idBuku).update("stok", stok_baru).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    Toast.makeText(FormPasok.this, "Berhasil menyimpan data", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

}