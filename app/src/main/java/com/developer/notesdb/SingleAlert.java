package com.developer.notesdb;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class SingleAlert {

    private AlertDialog alertDialog;
    private Activity mactivity;
    private String mtext;
    private ViewGroup parent;


    public SingleAlert(Activity activity, String text) {
        this.mactivity = activity;
        this.mtext = text;
    }

    public void startdialog() {
        View view = LayoutInflater.from(mactivity).inflate(R.layout.alert, parent, false);
        TextView textView = view.findViewById(R.id.textView_alert_text);
        alertDialog = new AlertDialog.Builder(mactivity).setCancelable(false).create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setView(view);
        textView.setText(mtext);
        alertDialog.show();
    }

    public void stopdialog() {
        alertDialog.dismiss();
    }

}
