package com.ifanferdi.aplikasitokobukuv2.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.ifanferdi.aplikasitokobukuv2.Activity.DetailKatalogBuku;
import com.ifanferdi.aplikasitokobukuv2.Activity.DetailRiwayatTransaksi;
import com.ifanferdi.aplikasitokobukuv2.Activity.RiwayatTransaksi;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelDetailTransaksi;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelTransaksi;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.RiwayatHolder> {

    RiwayatTransaksi riwayatTransaksi;
    List<ModelTransaksi> list;

    public RiwayatAdapter(RiwayatTransaksi riwayatTransaksi, List<ModelTransaksi> list) {
        this.riwayatTransaksi = riwayatTransaksi;
        this.list = list;
    }

    @NonNull
    @Override
    public RiwayatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(riwayatTransaksi).inflate(R.layout.data_riwayat, parent, false);
        return new RiwayatHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RiwayatHolder holder, int position) {
        final ModelTransaksi transaksi = list.get(position);

        SimpleDateFormat tanggal = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat jam = new SimpleDateFormat("hh:mm a");

        holder.text_tanggal.setText(tanggal.format(transaksi.getWaktu().toDate()));
        holder.text_jam.setText(jam.format(transaksi.getWaktu().toDate()));
        holder.text_notransaksi.setText(transaksi.getNo_transaksi());
        holder.text_total.setText(holder.decimalFormat.format(transaksi.getTotal_bayar()));
        holder.text_status.setText(transaksi.getStatus());

        String status = transaksi.getStatus();
        if(!(status.equals("Sedang Dikirim") || status.equals("Pesanan Diterima"))){
            holder.bg_status.setCardBackgroundColor(Color.parseColor("#FFFFE6C1"));
            holder.text_status.setTextColor(Color.parseColor("#FFEF8E00"));
        } else {
            holder.bg_status.setCardBackgroundColor(Color.parseColor("#FFC7FFC7"));
            holder.text_status.setTextColor(Color.parseColor("#FF00CD00"));
        }

        holder.card_riwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat tgl = new SimpleDateFormat("dd MMM yyyy");
                SimpleDateFormat jam = new SimpleDateFormat("hh:mm a");
                String no_transaksi = transaksi.getNo_transaksi();
                String id_user = transaksi.getId_user();
                String tanggal = tgl.format(transaksi.getWaktu().toDate());
                String waktu = jam.format(transaksi.getWaktu().toDate());
                String total_bayar = transaksi.getTotal_bayar().toString();
                String status = transaksi.getStatus();
                String bukti_pembayaran = transaksi.getBukti_pembayaran();
                String resi = transaksi.getResi();

                Intent intent = new Intent(riwayatTransaksi, DetailRiwayatTransaksi.class);
                intent.putExtra("no_transaksi", no_transaksi);
                intent.putExtra("id_user", id_user);
                intent.putExtra("tanggal", tanggal);
                intent.putExtra("waktu", waktu);
                intent.putExtra("total_bayar", total_bayar);
                intent.putExtra("status", status);
                intent.putExtra("bukti_pembayaran", bukti_pembayaran);
                intent.putExtra("resi", resi);

                riwayatTransaksi.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RiwayatHolder extends RecyclerView.ViewHolder {

        private TextView text_tanggal, text_jam, text_status, text_notransaksi, text_total;
        CardView bg_status, card_riwayat;

        private DecimalFormat decimalFormat;
        private DecimalFormatSymbols formatRupiah;

        public RiwayatHolder(@NonNull View itemView) {
            super(itemView);

            text_tanggal = itemView.findViewById(R.id.text_tanggal);
            text_jam = itemView.findViewById(R.id.text_jam);
            text_status = itemView.findViewById(R.id.text_status);
            text_notransaksi = itemView.findViewById(R.id.text_notransaksi);
            text_total = itemView.findViewById(R.id.text_total);
            bg_status = itemView.findViewById(R.id.bg_status);
            card_riwayat = itemView.findViewById(R.id.card_riwayat);

            decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            formatRupiah = new DecimalFormatSymbols();
            formatRupiah.setCurrencySymbol("Rp. ");
            formatRupiah.setMonetaryDecimalSeparator(',');
            formatRupiah.setGroupingSeparator('.');
            decimalFormat.setDecimalFormatSymbols(formatRupiah);

        }
    }
}
