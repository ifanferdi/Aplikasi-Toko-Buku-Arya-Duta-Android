package com.ifanferdi.aplikasitokobukuv2.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifanferdi.aplikasitokobukuv2.Activity.DataUser;
import com.ifanferdi.aplikasitokobukuv2.Activity.FormUser;
import com.ifanferdi.aplikasitokobukuv2.Model.ModelUser;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    DataUser dataUser;
    List<ModelUser> mList;

    public UserAdapter(DataUser dataUser, List<ModelUser> mList){
        this.dataUser = dataUser;
        this.mList = mList;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(dataUser).inflate(R.layout.data_user, parent, false);
        return new UserHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserHolder holder, int i) {
        final ModelUser data = mList.get(i);
        holder.text_nama.setText(data.getNama());
        holder.text_jeniskelamin.setText(data.getJenis_kelamin());
        holder.text_notelp.setText(data.getNotelp());
        holder.text_alamat.setText(data.getAlamat());
        holder.text_email.setText(data.getEmail());
        holder.text_role.setText(data.getRole());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(dataUser);
                String[] option = {"Ubah", "Hapus"};
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            FragmentManager manager = dataUser.getSupportFragmentManager();
                            FormUser dialogForm = new FormUser(data.getRole(), data.getId(), "Ubah", dataUser);
                            dialogForm.show(manager,"form");
                        }
                        if(i == 1){
                            dataUser.hapusData(data.getId());
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

    public static class UserHolder extends RecyclerView.ViewHolder{

        TextView text_email, text_nama, text_jeniskelamin, text_notelp, text_alamat, text_role;
        View view;

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;

            text_nama = itemView.findViewById(R.id.text_nama);
            text_jeniskelamin = itemView.findViewById(R.id.text_kelamin);
            text_notelp = itemView.findViewById(R.id.text_notelp);
            text_alamat = itemView.findViewById(R.id.text_alamat);
            text_email = itemView.findViewById(R.id.text_email);
            text_role = itemView.findViewById(R.id.text_role);
        }
    }
}
