package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifanferdi.aplikasitokobukuv2.R;

public class FormUser extends DialogFragment {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String role, id, pilih;

    RadioGroup group_akses;
    RadioButton radio_admin, radio_user;
    Button btn_save;

    DataUser dataUser;

    ProgressDialog pd;

    public FormUser(String role, String id, String pilih, DataUser dataUser) {
        this.role = role;
        this.id = id;
        this.pilih = pilih;
        this.dataUser = dataUser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.form_user, container, false);

        group_akses = view.findViewById(R.id.group_akses);
        radio_admin = view.findViewById(R.id.radio_admin);
        radio_user = view.findViewById(R.id.radio_user);
        btn_save = view.findViewById(R.id.btn_save);

        if(role.equals("Admin")){
            radio_admin.setChecked(true);
        } else {
            radio_user.setChecked(true);
        }

        pd = new ProgressDialog(view.getContext());

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int role = group_akses.getCheckedRadioButtonId();

                if(!(role == radio_admin.getId()) && !(role == radio_user.getId())){
                    Toast.makeText(view.getContext(), "Mohon Pilih Akses User", Toast.LENGTH_SHORT).show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(dataUser);
                    builder.setTitle("Konfirmasi ubah data");
                    builder.setMessage("Apakah anda yakin ingin mengubah data ini?")
                            .setCancelable(false)
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String akses, admin, user;
                                    admin = radio_admin.getText().toString();
                                    user = radio_user.getText().toString();

                                    if(role == radio_admin.getId()){
                                        akses = admin;
                                    } else {
                                        akses = user;
                                    }
                                    pd.setTitle("Sedang mengubah data..");
                                    pd.show();

                                    if(pilih.equals("Ubah")){
                                        fStore.collection("Users")
                                                .document(id)
                                                .update( "role", akses)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        pd.dismiss();
                                                        dismiss();
                                                        Toast.makeText(view.getContext(), "Data user berhasil diubah!", Toast.LENGTH_SHORT).show();
                                                        dataUser.tampilData();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).create().show();
                }
            }

        });

        return view;
    }

    public void onStart(){
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null){
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

}
