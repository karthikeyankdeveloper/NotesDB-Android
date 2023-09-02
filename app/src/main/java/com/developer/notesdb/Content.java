package com.developer.notesdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Content extends AppCompatActivity {


    private TextView back, type, save, delete;
    private EditText et_title, et_desc;
    private ConstraintLayout constraintLayout;
    private DatabaseReference reference, reference1, reference2;
    private FirebaseDatabase firebaseDatabase;
    private ValueEventListener valueEventListener;
    private String uid;
    private int count = 0, b = 0;
    private final SingleAlert singleAlert = new SingleAlert(Content.this, "PleaseWait");
    private final SingleAlert singleAlert1 = new SingleAlert(Content.this, "Data");
    private View view_delete, view_add,view_error;
    private ViewGroup mparent, nparent;
    String title,desc,color,id,all_cre;

    //for time date
    String time;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        singleAlert1.startdialog();
        back = findViewById(R.id.textView_back_content);
        type = findViewById(R.id.textView_type_content);
        save = findViewById(R.id.textView_save_content);
        delete = findViewById(R.id.textView_delete);
        et_title = findViewById(R.id.editText_title_content);
        et_desc = findViewById(R.id.edittext_desc_content);
        constraintLayout = findViewById(R.id.constrain_content_layout);
        delete.setEnabled(false);
        delete.setVisibility(View.INVISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){
            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        view_error = LayoutInflater.from(Content.this).inflate(R.layout.error_decision, mparent, false);
        AlertDialog alert_error = new AlertDialog.Builder(Content.this).create();
        alert_error.setView(view_error);
        alert_error.setCancelable(false);
        alert_error.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button btn_exit = view_error.findViewById(R.id.button_exit_error);
        Button btn_re_login = view_error.findViewById(R.id.button_re_login_error);

        if (uid!=null){
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference("UserList").child(uid).child("count");
            reference1 = firebaseDatabase.getReference("UserList").child(uid).child("List");
            Count_store(0);
            title = getIntent().getStringExtra("pass_title");
            desc = getIntent().getStringExtra("pass_desc");
            color = getIntent().getStringExtra("pass_color");
            id = getIntent().getStringExtra("pass_id");
            all_cre = getIntent().getStringExtra("pass_all_cre");
            try {
                if (!title.isEmpty()) {
                    et_title.setText(title);
                    delete.setEnabled(true);
                    delete.setVisibility(View.VISIBLE);
                }
                if (!desc.isEmpty()) {
                    et_desc.setText(Html.fromHtml(desc));
                    et_desc.setMovementMethod(LinkMovementMethod.getInstance());
                    delete.setEnabled(true);
                    delete.setVisibility(View.VISIBLE);
                }
                if (!color.isEmpty()) {
                    SwitchCall(color);
                }
            }
            catch (Exception e){
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        else {
            alert_error.show();
            btn_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert_error.dismiss();
                    Content.super.onBackPressed();
                }
            });

            btn_re_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Content.this,EnterPhone.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    alert_error.dismiss();
                    startActivity(intent);
                    finish();
                }
            });
        }

        view_delete = LayoutInflater.from(Content.this).inflate(R.layout.delete_desicion, mparent, false);

        AlertDialog alert_delete = new AlertDialog.Builder(Content.this).create();
        alert_delete.setView(view_delete);
        alert_delete.setCancelable(false);
        alert_delete.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button cancel_btn = view_delete.findViewById(R.id.button_cancel_decision_delete_page);
        Button delete_btn = view_delete.findViewById(R.id.button_delete_decision_delete_page);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_delete.show();
                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert_delete.dismiss();
                    }
                });
                delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        singleAlert.startdialog();
                        reference1.child("" + id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    alert_delete.dismiss();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(Content.this, DashBoard.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            reference.removeEventListener(valueEventListener);
                                            startActivity(intent);
                                            singleAlert.stopdialog();
                                            Toast.makeText(Content.this, "Deleted", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }, 1000);
                                } else {
                                    Toast.makeText(Content.this, "error occurred", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Content.this, DashBoard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                reference.removeEventListener(valueEventListener);
                startActivity(intent);
                finish();
            }
        });

        view_add = LayoutInflater.from(Content.this).inflate(R.layout.add_decision, nparent, false);
        AlertDialog alert_add = new AlertDialog.Builder(Content.this).create();
        alert_add.setView(view_add);
        alert_add.setCancelable(false);
        alert_add.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button add_btn_cancel = view_add.findViewById(R.id.button_cancel_decision_add_page);
        Button add_btn_add = view_add.findViewById(R.id.button_add_decision_add_page);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_add.show();
                add_btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert_add.dismiss();
                    }
                });
                add_btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_title.setEnabled(false);
                        et_desc.setEnabled(false);
                        time = simpleDateFormat.format(calendar.getTime());
                        String totle = et_title.getText().toString().trim();
                        String dosc = et_desc.getText().toString().trim();
                        singleAlert.startdialog();
                        if (count != 0) {
                            b = count - 1;
                            reference2 = reference1.child("" + count);
                            if (all_cre==null){
                                Toast.makeText(Content.this, "error occurred!", Toast.LENGTH_SHORT).show();
                                singleAlert.stopdialog();
                                alert_add.dismiss();
                            }
                            else if (all_cre.equals("create")) {
                                if (totle.isEmpty() || dosc.isEmpty()) {
                                    singleAlert.stopdialog();
                                    alert_add.dismiss();
                                    Toast.makeText(Content.this, "can't empty", Toast.LENGTH_SHORT).show();
                                    et_title.setEnabled(true);
                                    et_desc.setEnabled(true);
                                } else {
                                    reference.removeEventListener(valueEventListener);
                                    save.setEnabled(false);
                                    reference2.child("date").setValue(time);
                                    reference2.child("desc").setValue(dosc.trim());
                                    reference2.child("id").setValue(count);
                                    reference2.child("star").setValue("No");
                                    reference2.child("color").setValue("" + color.trim());
                                    reference2.child("title").setValue(totle.trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        reference.setValue(b).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    alert_add.dismiss();
                                                                    new Handler().postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Intent intent = new Intent(Content.this, DashBoard.class);
                                                                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                                            singleAlert.stopdialog();
                                                                            reference.removeEventListener(valueEventListener);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    }, 1000);

                                                                } else {
                                                                    Toast.makeText(Content.this, "error occurred", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });
                                                    }
                                                },1500);
                                            }
                                            else {
                                                Toast.makeText(Content.this, "error occurred,try later", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            }
                            else if (all_cre.equals("already")) {
                                if (totle.isEmpty() || dosc.isEmpty()) {
                                    singleAlert.stopdialog();
                                    alert_add.dismiss();
                                    Toast.makeText(Content.this, "can't empty", Toast.LENGTH_SHORT).show();
                                    et_title.setEnabled(true);
                                    et_desc.setEnabled(true);
                                }
                                else if (totle.equals(title) && dosc.equals(desc)) {
                                    singleAlert.stopdialog();
                                    Toast.makeText(Content.this, "No Changes", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Content.this, DashBoard.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    reference.removeEventListener(valueEventListener);
                                    save.setEnabled(false);
                                    reference1.child(id + "").removeValue();
                                    reference2.child("date").setValue(time);
                                    reference2.child("desc").setValue(dosc.trim());
                                    reference2.child("id").setValue(count);
                                    reference2.child("star").setValue("No");
                                    reference2.child("color").setValue("" + color.trim());
                                    reference2.child("title").setValue(totle.trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        reference.setValue(b).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    alert_add.dismiss();
                                                                    new Handler().postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Intent intent = new Intent(Content.this, DashBoard.class);
                                                                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                                            singleAlert.stopdialog();
                                                                            reference.removeEventListener(valueEventListener);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    }, 1000);

                                                                } else {
                                                                    Toast.makeText(Content.this, "error occurred", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });
                                                    }
                                                },1500);
                                            }
                                            else {
                                                Toast.makeText(Content.this, "error occurred", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }

                            } else {
                                Toast.makeText(Content.this, "error occurred", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Content.this, "error occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void Count_store(int ret_con) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    count =  snapshot.getValue(Integer.class);
                    b = count - 1;
                    singleAlert1.stopdialog();
                    reference.removeEventListener(valueEventListener);
                } else {
                    reference.removeEventListener(valueEventListener);
                    Toast.makeText(Content.this, "error occurred,try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                reference.removeEventListener(valueEventListener);
                Toast.makeText(Content.this, "error occurred", Toast.LENGTH_SHORT).show();
            }
        };

        reference.addValueEventListener(valueEventListener);

        if (ret_con != 2) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Count_store(ret_con + 1);
                }
            }, 1000);
        }


    }

    private void SwitchCall(String color) {
        switch (color) {
            case "red":
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.red));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.red));
                }
                break;
            case "blue":
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.blue));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.blue));
                }
                break;
            case "rose":
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.light_rose));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.light_rose));
                }
                break;
            case "green":
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.green));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.green));
                }
                break;
            case "yellow":
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.yellow));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.yellow));
                }
                break;
            case "difrose":
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.difrose));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.difrose));
                }
                break;
            case "orange":
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.orange));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.orange));
                }
                break;
            case "darkrose":
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.darkrose));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.darkrose));
                }
                break;
            case "trdblue":
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.trdblue));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.trdblue));
                }
                break;
            default:
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.black));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.black));
                }
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}