package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText text_nama, text_alamat, text_telp, text_email, text_password, text_password2;
    RadioGroup group_kelamin;
    RadioButton radio_laki, radio_perempuan;
    Button daftar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    LoginActivity loginActivity;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        text_nama = findViewById(R.id.editText_nama);
        group_kelamin = findViewById(R.id.group_jk);
        radio_laki = findViewById(R.id.radio_laki);
        radio_perempuan = findViewById(R.id.radio_perempuan);
        text_alamat = findViewById(R.id.editText_alamat);
        text_telp = findViewById(R.id.editText_notelp);
        text_email = findViewById(R.id.editText_email);
        text_password = findViewById(R.id.editText_password);
        text_password2 = findViewById(R.id.editText_repeat_password);
        daftar = findViewById(R.id.btn_daftar);

        daftar();

    }

    private void daftar(){

        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nama, laki, perempuan, alamat, telp, email, password, password2, jenis_kelamin;

                nama = text_nama.getText().toString().trim();
                laki = radio_laki.getText().toString().trim();
                perempuan = radio_perempuan.getText().toString().trim();
                alamat = text_alamat.getText().toString().trim();
                telp = text_telp.getText().toString().trim();
                email = text_email.getText().toString().trim();
                password = text_password.getText().toString().trim();
                password2 = text_password2.getText().toString().trim();

                int jk = group_kelamin.getCheckedRadioButtonId();

                if(nama.isEmpty()){
                    text_nama.setError("Mohon Isi Nama Anda");
                } else if(!(jk == radio_laki.getId()) && !(jk == radio_perempuan.getId())){
                    Toast.makeText(RegisterActivity.this, "Mohon Pilih Jenis Kelamin Anda", Toast.LENGTH_SHORT).show();
                } else if(alamat.isEmpty()){
                    text_alamat.setError("Mohon isi alamat anda");
                } else if(telp.isEmpty()){
                    text_telp.setError("Mohon isi no telepon anda");
                } else if(email.isEmpty()){
                    text_email.setError("Mohon isi email anda");
                } else if(password.isEmpty()){
                    text_password.setError("Mohon isi password anda");
                } else if(password2.isEmpty()){
                    text_password2.setError("Mohon ulangi password anda");
                } else  if(!password.equals(password2)){
                    text_password.setError("Password tidak cocok!");
                    text_password2.setError("Password tidak cocok!");
                } else {
                    if(jk == radio_laki.getId()){
                        jenis_kelamin = laki;
                    } else {
                        jenis_kelamin = perempuan;
                    }

                    pd.setTitle("Sedang memuat...");
                    pd.show();

                    fAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();

                            Map<String,Object> userData = new HashMap<>();
                            userData.put("id", user.getUid());
                            userData.put("nama", nama);
                            userData.put("jenis_kelamin", jenis_kelamin);
                            userData.put("alamat", alamat);
                            userData.put("notelp", telp);
                            userData.put("email", email);
                            userData.put("role", "User");
                            userData.put("image", "https://firebasestorage.googleapis.com/v0/b/tokobuku-88f1a.appspot.com/o/ProfilePicture%2Fdefault_picture.png?alt=media&token=0c4ef93b-664b-4b79-9a53-282f59392ee3");
                            fStore.collection("Users").document(user.getUid()).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Pendaftaran Berhasil! \n Mohon verifikasi email anda", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "Pendaftaran Gagal \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}