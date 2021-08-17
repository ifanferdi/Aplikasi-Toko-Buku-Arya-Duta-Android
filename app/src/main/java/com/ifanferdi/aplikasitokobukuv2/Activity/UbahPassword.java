package com.ifanferdi.aplikasitokobukuv2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifanferdi.aplikasitokobukuv2.R;

public class UbahPassword extends AppCompatActivity {

    FirebaseFirestore fStore;
    EditText password, password2;
    FirebaseUser user;
    Button simpan, batal;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_password);

        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        pd = new ProgressDialog(this);

        password = findViewById(R.id.text_password);
        password2 = findViewById(R.id.text_ulangpassword);
        simpan = findViewById(R.id.btn_simpan);
        batal = findViewById(R.id.btn_batal);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String pw = password.getText().toString().trim();
                String pw2 = password2.getText().toString().trim();
                if(pw.isEmpty()){
                    password.setError("Mohon isi password baru");
                } else if(pw2.isEmpty()){
                    password2.setError("Mohon ulangi password baru");
                } else if(!(pw.equals(pw2))) {
                    password.setError("Password tidak cocok");
                    password2.setError("Password tidak cocok");
                } else {
                    pd.setTitle("Sedang menyimpan data");
                    pd.show();

                    AuthCredential credential = EmailAuthProvider
                            .getCredential("ifan.develop@gmail.com", "ifanferdi,123");

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    user.updatePassword(pw)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        pd.dismiss();
                                                        finish();
                                                        Toast.makeText(getApplicationContext(), "Password berhasil diubah", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UbahPassword.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                }
            }
        });
    }
}