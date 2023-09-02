package com.developer.notesdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("UserList");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        String uid = snapshot.getValue(String.class);

                        if (uid != null) {
                            Validate(uid, userid);
                        }

                        reference.child(userid).child("UID").removeEventListener(valueEventListener);
                    } else {
                        EnterActivity();
                        reference.child(userid).child("UID").removeEventListener(valueEventListener);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SplashScreen.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            reference.child(userid).child("UID").addValueEventListener(valueEventListener);

        } else {
            EnterActivity();
        }
    }

    private void Validate(String uid, String userid) {
        if (uid.equals(userid)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, DashBoard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        } else {
            EnterActivity();
        }
    }

    private void EnterActivity() {
        FirebaseAuth.getInstance().signOut();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, EnterPhone.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }


    boolean aBoolean = true;

    @Override
    public void onBackPressed() {
        if (aBoolean) {
            aBoolean = false;
            Toast.makeText(this, "press again to exit.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    aBoolean = true;
                }
            }, 3000);
        } else {
            super.onBackPressed();
        }

    }
}