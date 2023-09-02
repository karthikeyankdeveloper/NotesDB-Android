package com.developer.notesdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Random;

public class DashBoard extends AppCompatActivity {

    private Adapter adapter;
    private ListView listView;
    private TextView add, refresh, not_avail;
    private ConstraintLayout constraintLayout;
    private ArrayList<Data> arrayList = new ArrayList<Data>();
    private final String colorss[] = {"red", "blue", "rose", "green", "black","yellow","difrose","orange","darkrose","trdblue"};
    private String uid;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;
    private final SingleAlert singleAlert = new SingleAlert(DashBoard.this, "refreshing");
    private ViewGroup mparent;
    private View view_error;
    private TextView NiceDay;
    int cou = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        add = findViewById(R.id.textView_add);
        constraintLayout = findViewById(R.id.constraint_dash);
        listView = findViewById(R.id.listview_dash);
        refresh = findViewById(R.id.textView_refresh);
        not_avail = findViewById(R.id.textView_not_avail);
        NiceDay = findViewById(R.id.textView_nice_day);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        NiceDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutShow();
            }
        });

        view_error = LayoutInflater.from(DashBoard.this).inflate(R.layout.error_decision, mparent, false);
        AlertDialog alert_error = new AlertDialog.Builder(DashBoard.this).create();
        alert_error.setView(view_error);
        alert_error.setCancelable(false);
        alert_error.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button btn_exit = view_error.findViewById(R.id.button_exit_error);
        Button btn_re_login = view_error.findViewById(R.id.button_re_login_error);


        if (uid!=null){
            reference = FirebaseDatabase.getInstance().getReference("UserList").child(uid).child("List");
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrayList.clear();
                    if (snapshot.exists()) {
                        not_avail.setVisibility(View.INVISIBLE);
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                String color = ds.child("color").getValue(String.class);
                                String title = ds.child("title").getValue(String.class);
                                String desc = ds.child("desc").getValue(String.class);
                                String date = ds.child("date").getValue(String.class);
                                String star = ds.child("star").getValue(String.class);
                                Integer id = ds.child("id").getValue(Integer.class);

                                if (color==null || title == null || desc == null || date == null || star == null || id==null){
                                }
                                else {
                                    Data data = new Data("" + color, "" + title, "" + desc, "" + date, "" + star, "" + id);
                                    arrayList.add(data);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            catch (Exception e){
                                Toast.makeText(DashBoard.this, ""+e, Toast.LENGTH_SHORT).show();
                            }
                            singleAlert.stopdialog();
                        }

                    } else {
                        arrayList.clear();
                        not_avail.setVisibility(View.VISIBLE);
                        singleAlert.stopdialog();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    arrayList.add(null);
                    not_avail.setVisibility(View.VISIBLE);
                    singleAlert.stopdialog();
                }
            };

            adapter = new Adapter(DashBoard.this, R.layout.adapter, arrayList, reference, valueEventListener);

            ActivateReference();

        }else {

            alert_error.show();

            btn_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert_error.dismiss();
                    DashBoard.super.onBackPressed();
                }
            });

            btn_re_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(DashBoard.this,EnterPhone.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    alert_error.dismiss();
                    startActivity(intent);
                    finish();
                }
            });

        }

        listView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveReference();
                Random random = new Random();
                int a = random.nextInt(10);
                ColorPass(a);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.setEnabled(false);
                refresh.setVisibility(View.INVISIBLE);
                ActivateReference();
            }
        });

    }

    public void ActivateReference() {
        singleAlert.startdialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reference.addValueEventListener(valueEventListener);
            }
        }, 900);

    }

    public void RemoveReference() {
        reference.removeEventListener(valueEventListener);
    }

    private void ColorPass(int a) {
        RemoveReference();
        Intent intent = new Intent(DashBoard.this, Content.class);
        intent.putExtra("pass_title", "");
        intent.putExtra("pass_desc", "");
        intent.putExtra("pass_color", colorss[a]);
        intent.putExtra("pass_id", "");
        intent.putExtra("pass_all_cre", "create");
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
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

    private void AboutShow() {
        if (cou==3){
            AboutDialog aboutDialog = new AboutDialog(DashBoard.this);
            aboutDialog.start();
            cou=0;
        }
        else {
            cou++;
        }
    }

}