package com.developer.notesdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnterPhone extends AppCompatActivity {

    private TextInputLayout phonenumber;
    private TextView click_next;
    private ConstraintLayout constraintLayout;
    private View view;
    private ViewGroup viewGroup;
    private Button sign_google;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;
    private final SingleAlert singleAlert = new SingleAlert(EnterPhone.this, "PleaseWait");
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone);
        phonenumber = findViewById(R.id.text_input_phone_number);
        click_next = findViewById(R.id.phone_textview_click_next);
        constraintLayout = findViewById(R.id.enter_phone_constrain_top);
        sign_google = findViewById(R.id.button_google_sign_in);

        Text_watcher_call();

        reference = FirebaseDatabase.getInstance().getReference("UserList");

        view = LayoutInflater.from(EnterPhone.this).inflate(R.layout.decision, viewGroup, false);
        Button NO = view.findViewById(R.id.button_no);
        Button YES = view.findViewById(R.id.button_yes);
        AlertDialog alertDialog = new AlertDialog.Builder(EnterPhone.this).setCancelable(false).create();
        alertDialog.setView(view);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        constraintLayout.startAnimation(AnimationUtils.loadAnimation(EnterPhone.this, R.anim.updown));

        click_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim_phone = phonenumber.getEditText().getText().toString().trim();
                if (trim_phone.isEmpty()) {
                    phonenumber.setError("Can't be Empty");
                } else if (trim_phone.length() < 10) {
                    phonenumber.setError("Enter Valid Number");
                } else {
                    alertDialog.show();

                    NO.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    YES.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(EnterPhone.this, OtpVerify.class);
                            intent.putExtra("passed_number", "+91" + phonenumber.getEditText().getText().toString());
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });

                }

            }
        });

        sign_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleAlert.startdialog();
                signIn();
            }
        });

        Create_Request();
    }

    private void Create_Request() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.db_google))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                singleAlert.stopdialog();
                Toast.makeText(this, "Error Occurred!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(EnterPhone.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

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
                        singleAlert.stopdialog();
                        Toast.makeText(EnterPhone.this, "SignIn Failed", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    singleAlert.stopdialog();
                    Toast.makeText(EnterPhone.this, "SignIn Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Text_watcher_call() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String val = phonenumber.getEditText().getText().toString().trim();
                if (val.isEmpty()) {

                } else if (val.length() == 10) {
                    phonenumber.setError(null);
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(click_next.getApplicationWindowToken(), 0);
                    click_next.performClick();
                } else {
                    phonenumber.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        phonenumber.getEditText().addTextChangedListener(textWatcher);
    }

    private void With_out_count(String uid) {
        reference.child(uid).child("UID").setValue(uid.trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    singleAlert.stopdialog();
                    Intent intent = new Intent(EnterPhone.this, DashBoard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    reference.child(uid).child("count").removeEventListener(valueEventListener);
                    startActivity(intent);
                    Toast.makeText(EnterPhone.this, "Login Success!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    FirebaseAuth.getInstance().signOut();
                    reference.child(uid).child("count").removeEventListener(valueEventListener);
                    Toast.makeText(EnterPhone.this, "Try Later!", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(EnterPhone.this, DashBoard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    reference.child(uid).child("count").removeEventListener(valueEventListener);
                    startActivity(intent);
                    Toast.makeText(EnterPhone.this, "Login Success!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    FirebaseAuth.getInstance().signOut();
                    reference.child(uid).child("count").removeEventListener(valueEventListener);
                    Toast.makeText(EnterPhone.this, "Error Occurred,Try Later", Toast.LENGTH_SHORT).show();
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