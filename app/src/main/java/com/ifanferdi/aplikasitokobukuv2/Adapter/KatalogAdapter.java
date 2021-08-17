package com.ifanferdi.aplikasitokobukuv2.Adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ifanferdi.aplikasitokobukuv2.Activity.DetailKatalogBuku;
import com.ifanferdi.aplikasitokobukuv2.Activity.FormBuku;
import com.ifanferdi.aplikasitokobukuv2.Activity.KatalogBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class KatalogAdapter extends RecyclerView.Adapter<KatalogAdapter.KatalogHolder> {

    KatalogBuku katalogBuku;
    List<ModelBuku> mList;

    public KatalogAdapter(KatalogBuku katalogBuku, List<ModelBuku> mList) {
        this.katalogBuku = katalogBuku;
        this.mList = mList;
    }

    @NonNull
    @Override
    public KatalogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(katalogBuku).inflate(R.layout.data_katalog_buku, parent, false);
        return new KatalogHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull KatalogHolder holder, int position) {
        final ModelBuku data = mList.get(position);
        holder.text_judul.setText(data.getJudul());
        Glide.with(katalogBuku).load(data.getImage()).into(holder.imageView);
        if (data.getDiskon() == 0){
            holder.bg_diskon.setVisibility(View.GONE);
            holder.text_harga.setText(holder.decimalFormat.format(data.getHarga()));
        } else {
            holder.text_harga.setText(holder.decimalFormat.format(data.getHarga() - (data.getHarga() * data.getDiskon() / 100)));
            holder.bg_diskon.setVisibility(View.VISIBLE);
            holder.text_diskon.setText("-" + data.getDiskon() + "%");
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                Intent intent = new Intent(katalogBuku, DetailKatalogBuku.class);
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

                katalogBuku.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class KatalogHolder extends RecyclerView.ViewHolder {

        TextView text_judul, text_harga, text_diskon;
        LinearLayout bg_diskon;
        ImageView imageView;
        View view;
        CardView cardView;
        DecimalFormat decimalFormat;
        DecimalFormatSymbols decimalFormatSymbols;

        public KatalogHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            cardView = itemView.findViewById(R.id.card_katalog_buku);
            text_judul = itemView.findViewById(R.id.text_judul);
            text_harga = itemView.findViewById(R.id.text_harga);
            imageView = itemView.findViewById(R.id.image_buku);
            text_diskon = itemView.findViewById(R.id.text_diskon);
            bg_diskon = itemView.findViewById(R.id.bg_diskon);

            decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol("Rp. ");
            decimalFormatSymbols.setMonetaryDecimalSeparator(',');
            decimalFormatSymbols.setGroupingSeparator('.');
            decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        }
    }
}
