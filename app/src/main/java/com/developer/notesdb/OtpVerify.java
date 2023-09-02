package com.developer.notesdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.TimeUnit;

public class OtpVerify extends AppCompatActivity {

    private String phone_number, verification_code_by_system;
    private TextView textView, verify;
    private TextInputLayout textInputLayout_otp;
    private ConstraintLayout constraintLayout;
    private FirebaseAuth mAuth;
    private final SingleAlert singleAlert = new SingleAlert(OtpVerify.this, "Verifying");
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        verification_code_by_system = "";

        constraintLayout = findViewById(R.id.otp_constraint_layout);

        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();

        constraintLayout.startAnimation(AnimationUtils.loadAnimation(OtpVerify.this, R.anim.updown));

        phone_number = getIntent().getStringExtra("passed_number");

        textView = findViewById(R.id.textView_display_phone_number);

        verify = findViewById(R.id.textView_verify_otp);

        textInputLayout_otp = findViewById(R.id.textInputLayout_otp);

        if (phone_number != null) {
            textView.setText("We will sent one time password to\n" + phone_number);
            verify_phoneumber(phone_number);
        } else {
            Toast.makeText(this, "Try Again Later!", Toast.LENGTH_SHORT).show();
        }

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = textInputLayout_otp.getEditText().getText().toString().trim();
                if (otp.isEmpty()) {
                    textInputLayout_otp.setError("Can't be Empty");
                } else if (otp.length() < 6) {
                    textInputLayout_otp.setError("Enter Valid OTP");
                } else {
                    textInputLayout_otp.setError(null);
                    verifyCode(otp);
                }
            }
        });

    }

    private void verify_phoneumber(String phone_num) {
        singleAlert.startdialog();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "" + phone_num,
                120,
                TimeUnit.SECONDS,
                this,
                mcallbacks);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String code, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            verification_code_by_system = code;
            singleAlert.stopdialog();
            Toast.makeText(OtpVerify.this, "code send!", Toast.LENGTH_SHORT).show();
            super.onCodeSent(code, forceResendingToken);
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode().trim();
            textInputLayout_otp.getEditText().setText(code);
            singleAlert.stopdialog();
            verifyCode(code);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            singleAlert.stopdialog();
            Toast.makeText(OtpVerify.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code_passed) {
        singleAlert.startdialog();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_code_by_system, code_passed);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    reference = firebaseDatabase.getReference("UserList");
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if (uid != null) {
                        valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    With_out_count(uid);
                                } else {
                                    With_count(uid);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                        reference.child(uid).child("count").addValueEventListener(valueEventListener);
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(OtpVerify.this, "Error Occurred,Try Later", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    singleAlert.stopdialog();
                    Toast.makeText(OtpVerify.this, "Failed to Login!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void With_out_count(String uid) {
        reference.child(uid).child("UID").setValue(uid.trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    singleAlert.stopdialog();
                    Intent intent = new Intent(OtpVerify.this, DashBoard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    reference.child(uid).child("count").removeEventListener(valueEventListener);
                    startActivity(intent);
                    Toast.makeText(OtpVerify.this, "Login Success!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    FirebaseAuth.getInstance().signOut();
                    reference.child(uid).child("count").removeEventListener(valueEventListener);
                    Toast.makeText(OtpVerify.this, "Error Occurred,Try Later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void With_count(String uid) {
        reference.child(uid).child("UID").setValue(uid.trim());
        reference.child(uid).child("count").setValue(638398).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    singleAlert.stopdialog();
                    Intent intent = new Intent(OtpVerify.this, DashBoard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    reference.child(uid).child("count").removeEventListener(valueEventListener);
                    startActivity(intent);
                    Toast.makeText(OtpVerify.this, "Login Success!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    FirebaseAuth.getInstance().signOut();
                    reference.child(uid).child("count").removeEventListener(valueEventListener);
                    Toast.makeText(OtpVerify.this, "Error Occurred,Try Later", Toast.LENGTH_SHORT).show();
                }
            }
        });

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