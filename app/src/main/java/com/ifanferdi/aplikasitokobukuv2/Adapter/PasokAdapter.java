package com.ifanferdi.aplikasitokobukuv2.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.ifanferdi.aplikasitokobukuv2.Activity.DataPasok;
import com.ifanferdi.aplikasitokobukuv2.Activity.FormPasok;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelBuku;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelDistributor;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelPasok;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class PasokAdapter extends RecyclerView.Adapter<PasokAdapter.PasokHolder> {
    DataPasok dataPasok;
    List<ModelPasok> mList;

    public PasokAdapter(DataPasok dataPasok, List<ModelPasok> mList) {
        this.dataPasok = dataPasok;
        this.mList = mList;
    }

    @NonNull
    @Override
    public PasokHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(dataPasok).inflate(R.layout.data_pasok, parent, false);
        return new PasokHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PasokHolder holder, int position) {
        final ModelPasok modelPasok = mList.get(position);

        holder.text_namadistributor.setText(modelPasok.getNama_distributor());
        holder.text_buku.setText(modelPasok.getJudul_buku());
        holder.text_jumlah.setText(modelPasok.getJumlah().toString() + " pcs");
        holder.text_tanggal.setText(holder.sdf.format(modelPasok.getTanggal().toDate()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(dataPasok);
                String[] option = {"Hapus"};
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            dataPasok.hapusPasok(modelPasok.getId());
                        }
                    }
                }).create().show();;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class PasokHolder extends RecyclerView.ViewHolder {

        TextView text_namadistributor, text_buku, text_jumlah, text_tanggal;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

        public PasokHolder(@NonNull View itemView) {
            super(itemView);

            text_namadistributor = itemView.findViewById(R.id.text_namadistributor);
            text_buku = itemView.findViewById(R.id.text_buku);
            text_jumlah = itemView.findViewById(R.id.text_jumlah);
            text_tanggal = itemView.findViewById(R.id.text_tanggal);
        }

    }
}
