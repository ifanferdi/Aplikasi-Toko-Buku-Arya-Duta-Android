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

import com.ifanferdi.aplikasitokobukuv2.Activity.DataDistributor;
import com.ifanferdi.aplikasitokobukuv2.Activity.FormDistributor;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelDistributor;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class DistributorAdapter extends RecyclerView.Adapter<DistributorAdapter.DistributorHolder> {
    DataDistributor dataDistributor;
    List<ModelDistributor> mList;

    public DistributorAdapter(DataDistributor dataDistributor, List<ModelDistributor> mList) {
        this.dataDistributor = dataDistributor;
        this.mList = mList;
    }

    @NonNull
    @Override
    public DistributorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(dataDistributor).inflate(R.layout.data_distributor, parent, false);
        return new DistributorHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DistributorHolder holder, int position) {
        final ModelDistributor data = mList.get(position);

        holder.text_nama.setText(data.getNama_distributor());
        holder.text_notelp.setText(data.getNotelp());
        holder.text_alamat.setText(data.getAlamat());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(dataDistributor);
                String[] option = {"Ubah", "Hapus"};
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            String id = data.getId();
                            String nama = data.getNama_distributor();
                            String notelp = data.getNotelp();
                            String alamat = data.getAlamat();

                            Intent intent = new Intent(dataDistributor, FormDistributor.class);
                            intent.putExtra("id", id);
                            intent.putExtra("nama_distributor", nama);
                            intent.putExtra("notelp", notelp);
                            intent.putExtra("alamat", alamat);

                            dataDistributor.startActivity(intent);
                        }
                        if(i==1){
                            dataDistributor.hapusDistributor(data.getId());
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

    public class DistributorHolder extends RecyclerView.ViewHolder {

        TextView text_nama, text_notelp, text_alamat;

        public DistributorHolder(@NonNull View itemView) {
            super(itemView);

            text_nama = itemView.findViewById(R.id.text_namadistributor);
            text_notelp = itemView.findViewById(R.id.text_notelp);
            text_alamat = itemView.findViewById(R.id.text_alamat);

        }
    }
}
