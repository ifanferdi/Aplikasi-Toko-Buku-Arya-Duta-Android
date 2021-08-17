package com.ifanferdi.aplikasitokobukuv2.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ifanferdi.aplikasitokobukuv2.Activity.CheckoutKeranjang;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelKeranjang;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutHolder> {

    CheckoutKeranjang checkoutKeranjang;
    List<ModelKeranjang> mList;
    List<ModelBuku> mListBuku;

    public CheckoutAdapter(CheckoutKeranjang checkoutKeranjang, List<ModelKeranjang> mList, List<ModelBuku> mListBuku) {
        this.checkoutKeranjang = checkoutKeranjang;
        this.mList = mList;
        this.mListBuku = mListBuku;
    }

    @NonNull
    @Override
    public CheckoutAdapter.CheckoutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(checkoutKeranjang).inflate(R.layout.data_checkout, parent, false);
        return new CheckoutAdapter.CheckoutHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutAdapter.CheckoutHolder holder, int position) {
        final ModelKeranjang data = mList.get(position);
        final ModelBuku buku = mListBuku.get(position);

        holder.text_judul.setText(buku.getJudul());
        holder.text_total.setText(holder.decimalFormat.format(data.getTotal()));
        holder.text_jumlah.setText(data.getJumlah().toString() + " item");
        holder.text_harga.setText(holder.decimalFormat.format(data.getHarga()) + " x ");
        Glide.with(checkoutKeranjang).load(buku.getImage()).into(holder.image_buku);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class CheckoutHolder extends RecyclerView.ViewHolder {

        private TextView text_judul, text_harga, text_jumlah, text_total;
        private ImageView image_buku;
        private DecimalFormat decimalFormat;
        private DecimalFormatSymbols decimalFormatSymbols;

        public CheckoutHolder(@NonNull View itemView) {
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
