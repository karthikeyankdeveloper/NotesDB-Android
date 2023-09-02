package com.developer.notesdb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class AboutDialog {

    private Context mcontext;
    private AlertDialog alertDialog;
    private View view;
    private TextView ok;

    public AboutDialog(Context context){
        this.mcontext=context;
    }


    public void start(){
        view= LayoutInflater.from(mcontext).inflate(R.layout.about,null);
        ok=view.findViewById(R.id.textView_about_ok);
        alertDialog = new AlertDialog.Builder(mcontext).create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setView(view);
        alertDialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}
