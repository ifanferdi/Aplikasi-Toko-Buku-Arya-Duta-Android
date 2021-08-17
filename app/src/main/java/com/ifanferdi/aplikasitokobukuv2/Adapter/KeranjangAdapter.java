package com.ifanferdi.aplikasitokobukuv2.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifanferdi.aplikasitokobukuv2.Activity.DataBuku;
import com.ifanferdi.aplikasitokobukuv2.Activity.Keranjang;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelKeranjang;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeranjangAdapter extends RecyclerView.Adapter<KeranjangAdapter.KeranjangHolder>{
    Keranjang keranjang;
    List<ModelKeranjang> mList;
    List<ModelBuku> mListBuku;

    public KeranjangAdapter(Keranjang keranjang, List<ModelKeranjang> mList, List<ModelBuku> mListBuku) {
        this.keranjang = keranjang;
        this.mList = mList;
        this.mListBuku = mListBuku;
    }

    @NonNull
    @Override
    public KeranjangHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(keranjang).inflate(R.layout.data_keranjang, parent, false);
        return new KeranjangHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final KeranjangHolder holder, int position) {
        final ModelKeranjang data = mList.get(position);
        final ModelBuku buku = mListBuku.get(position);

        int total = Integer.parseInt(data.getTotal().toString());
        int jumlah = Integer.parseInt(data.getJumlah().toString());

        holder.text_judul.setText(buku.getJudul());
        holder.text_total.setText(holder.decimalFormat.format(total));
        holder.text_jumlah.setText(String.valueOf(jumlah));
        holder.text_jumlah2.setText("(" + jumlah + " x ");
        holder.text_harga.setText(holder.decimalFormat.format(data.getHarga()) + ")");
        Glide.with(keranjang).load(buku.getImage()).into(holder.image_buku);

        holder.btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.jml_beli = Integer.parseInt(holder.text_jumlah.getText().toString());
                holder.jml_beli++;
                holder.text_jumlah.setText(""+holder.jml_beli);
                holder.text_jumlah2.setText("(" + holder.jml_beli + " x ");
                holder.text_total.setText(""+holder.decimalFormat.format(holder.jml_beli * data.getHarga()));
                holder.ubahJumlah(data.getId(), data.getHarga(), holder.jml_beli);

                keranjang.tampilTotalTambah(Integer.parseInt(data.getHarga().toString()));
            }
        });

        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.text_jumlah.getText().equals("1")){
                    holder.jml_beli = 1;
                } else {
                    holder.jml_beli = Integer.parseInt(holder.text_jumlah.getText().toString());
                    holder.jml_beli--;
                    keranjang.tampilTotalKurang(Integer.parseInt(data.getHarga().toString()));
                }

                holder.text_jumlah.setText(""+holder.jml_beli);
                holder.text_jumlah2.setText("(" + holder.jml_beli + " x ");
                holder.text_total.setText(""+holder.decimalFormat.format(holder.jml_beli * data.getHarga()));
                holder.ubahJumlah(data.getId(), data.getHarga(), holder.jml_beli);
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keranjang.hapusKeranjang(data.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class KeranjangHolder extends RecyclerView.ViewHolder{

        TextView text_judul, text_jumlah, text_total, text_harga, text_jumlah2;
        ImageView image_buku;
        View view;
        int jml_beli = 0;

        Button btn_minus, btn_plus, btn_delete;
        int subtotal = 0, subtotaljumlah= 0;

        DecimalFormat decimalFormat;
        DecimalFormatSymbols decimalFormatSymbols;

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        public KeranjangHolder(@NonNull View itemView){
            super(itemView);

            view = itemView;

            text_judul = itemView.findViewById(R.id.text_judul);
            text_total = itemView.findViewById(R.id.text_total);
            text_jumlah = itemView.findViewById(R.id.text_jumlah);
            text_jumlah2 = itemView.findViewById(R.id.text_jumlah2);
            text_harga = itemView.findViewById(R.id.text_harga);
            image_buku = itemView.findViewById(R.id.image_buku);
            btn_minus = itemView.findViewById(R.id.btn_minus);
            btn_plus = itemView.findViewById(R.id.btn_plus);
            btn_delete = itemView.findViewById(R.id.btn_delete);

            decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol("Rp. ");
            decimalFormatSymbols.setMonetaryDecimalSeparator(',');
            decimalFormatSymbols.setGroupingSeparator('.');
            decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        }

        private void ubahJumlah(String id, Long harga, int jml) {
            Map<String,Object> ubah = new HashMap<>();
            ubah.put("jumlah", Long.parseLong(String.valueOf(jml)));
            ubah.put("total", Long.parseLong(String.valueOf(harga*jml)));
            fStore.collection("Keranjang").document(id).update(ubah);
        }
    }

}