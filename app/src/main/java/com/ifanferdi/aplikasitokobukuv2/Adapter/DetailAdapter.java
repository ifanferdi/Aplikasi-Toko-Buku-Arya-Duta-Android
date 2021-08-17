package com.ifanferdi.aplikasitokobukuv2.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ifanferdi.aplikasitokobukuv2.Activity.DataTransaksi;
import com.ifanferdi.aplikasitokobukuv2.Activity.DetailTransaksi;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelDetailTransaksi;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailHolder> {

    DetailTransaksi detailTransaksi;
    List<ModelDetailTransaksi> list;
    List<ModelBuku> listBuku;

    public DetailAdapter(DetailTransaksi detailTransaksi, List<ModelDetailTransaksi> list, List<ModelBuku> listBuku) {
        this.detailTransaksi = detailTransaksi;
        this.list = list;
        this.listBuku = listBuku;
    }

    @NonNull
    @Override
    public DetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(detailTransaksi).inflate(R.layout.data_detail_riwayat, parent, false);
        return new DetailHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailHolder holder, int position) {
        final ModelDetailTransaksi data = list.get(position);
        final ModelBuku buku = listBuku.get(position);

        holder.text_judul.setText(buku.getJudul());
        holder.text_total.setText(holder.decimalFormat.format(data.getTotal()));
        holder.text_jumlah.setText(data.getJumlah().toString() + " item x ");
        holder.text_harga.setText(holder.decimalFormat.format(data.getHarga()));
        Glide.with(detailTransaksi).load(buku.getImage()).into(holder.image_buku);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DetailHolder extends RecyclerView.ViewHolder {

        private TextView text_judul, text_harga, text_jumlah, text_total;
        private ImageView image_buku;
        private DecimalFormat decimalFormat;
        private DecimalFormatSymbols decimalFormatSymbols;

        public DetailHolder(@NonNull View itemView) {
            super(itemView);

            text_judul = itemView.findViewById(R.id.text_judul);
            text_harga = itemView.findViewById(R.id.text_harga);
            text_jumlah = itemView.findViewById(R.id.text_jumlah);
            text_total = itemView.findViewById(R.id.text_total);
            image_buku = itemView.findViewById(R.id.image_buku);
            decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol("Rp. ");
            decimalFormatSymbols.setMonetaryDecimalSeparator(',');
            decimalFormatSymbols.setGroupingSeparator('.');
            decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        }
    }
}
