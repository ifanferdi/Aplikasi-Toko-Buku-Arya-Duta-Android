package com.ifanferdi.aplikasitokobukuv2.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ifanferdi.aplikasitokobukuv2.Activity.DataBuku;
import com.ifanferdi.aplikasitokobukuv2.Activity.FormBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class BukuAdapter extends RecyclerView.Adapter<BukuAdapter.BukuHolder> {

    DataBuku dataBuku;
    List<ModelBuku> mList;

    public BukuAdapter(DataBuku dataBuku, List<ModelBuku> mList) {
        this.dataBuku = dataBuku;
        this.mList = mList;
    }

    @NonNull
    @Override
    public BukuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(dataBuku).inflate(R.layout.data_buku, parent, false);
        return new BukuHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BukuHolder holder, int position) {
        final ModelBuku data = mList.get(position);
        holder.text_judul.setText(data.getJudul());
        holder.text_penulis.setText(data.getPenulis());
        holder.text_penerbit.setText(data.getPenerbit());
        holder.text_tahun.setText("(" + data.getTahun() + ")");
        holder.text_stok.setText(data.getStok().toString());
        holder.text_kategori.setText(data.getKategori());
        holder.text_harga.setText(holder.decimalFormat.format(data.getHarga()));
        holder.text_diskon.setText(data.getDiskon().toString()+ "%");
        holder.text_diskon.setText(data.getDiskon().toString()+ "%");
        Glide.with(dataBuku).load(data.getImage()).into(holder.image_buku);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(dataBuku);
                String[] option = {"Ubah", "Hapus"};
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            String id = data.getId();
                            String kode = data.getKode();
                            String judul = data.getJudul();
                            String noisbn = data.getNoisbn();
                            String penulis = data.getPenulis();
                            String penerbit = data.getPenerbit();
                            String tahun = data.getTahun();
                            String stok = data.getStok().toString();
                            String kategori = data.getKategori();
                            String harga = data.getHarga().toString();
                            String diskon = data.getDiskon().toString();
                            String keterangan = data.getKeterangan();
                            String image = data.getImage();

                            Intent intent = new Intent(dataBuku, FormBuku.class);
                            intent.putExtra("id", id);
                            intent.putExtra("kode", kode);
                            intent.putExtra("judul", judul);
                            intent.putExtra("noisbn", noisbn);
                            intent.putExtra("penulis", penulis);
                            intent.putExtra("penerbit", penerbit);
                            intent.putExtra("tahun", tahun);
                            intent.putExtra("stok", stok);
                            intent.putExtra("kategori", kategori);
                            intent.putExtra("harga", harga);
                            intent.putExtra("diskon", diskon);
                            intent.putExtra("keterangan", keterangan);
                            intent.putExtra("image", image);

                            dataBuku.startActivity(intent);
                        }
                        if(i == 1){
                            dataBuku.hapusBuku(data.getId());
                        }
                    }
                }).create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class BukuHolder extends RecyclerView.ViewHolder{

        TextView text_judul, text_penulis, text_penerbit, text_tahun, text_stok;
        TextView text_kategori, text_harga, text_diskon;
        ImageView image_buku;
        View view;

        DecimalFormat decimalFormat;
        DecimalFormatSymbols formatRupiah;

        public BukuHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;

            text_judul = itemView.findViewById(R.id.text_judul);
            text_penulis = itemView.findViewById(R.id.text_penulis);
            text_penerbit = itemView.findViewById(R.id.text_penerbit);
            text_tahun = itemView.findViewById(R.id.text_tahun);
            text_stok = itemView.findViewById(R.id.text_stok);
            text_kategori = itemView.findViewById(R.id.text_kategori);
            text_harga = itemView.findViewById(R.id.text_harga);
            text_diskon = itemView.findViewById(R.id.text_diskon);
            image_buku = itemView.findViewById(R.id.image_buku);

            decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            formatRupiah = new DecimalFormatSymbols();
            formatRupiah.setCurrencySymbol("Rp. ");
            formatRupiah.setMonetaryDecimalSeparator(',');
            formatRupiah.setGroupingSeparator('.');
            decimalFormat.setDecimalFormatSymbols(formatRupiah);
        }
    }
}