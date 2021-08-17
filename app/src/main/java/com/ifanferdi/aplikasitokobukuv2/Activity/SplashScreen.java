package com.ifanferdi.aplikasitokobukuv2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifanferdi.aplikasitokobukuv2.R;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN = 2000;

    Animation top_animation, bottom_animation;
    ImageView image;
    TextView tokobuku;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        top_animation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottom_animation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        image = findViewById(R.id.image_logo);
        tokobuku = findViewById(R.id.text_tokobuku);

        image.setAnimation(top_animation);
        tokobuku.setAnimation(bottom_animation);

        fStore = FirebaseFirestore.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null){
                    DocumentReference df = fStore.collection("Users").document(user.getUid());
                    df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d("TAG", "onSuccess" + documentSnapshot.getData());
                            if(Objects.equals(documentSnapshot.getString("role"), "Admin")){
                                startActivity(new Intent(getApplicationContext(), HomeAdmin.class));
                                finish();
                            } else {
                                startActivity(new Intent(getApplicationContext(), KatalogBuku.class));
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SplashScreen.this, "Login Gagal \n " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                }
            }
        }, SPLASH_SCREEN);
    }
}