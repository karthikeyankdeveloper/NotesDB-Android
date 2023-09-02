package com.developer.notesdb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Adapter extends ArrayAdapter<Data> {

    private final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final Context mcontext;
    private final int mresource;
    private DatabaseReference reference;
    private DatabaseReference mreference;
    private ValueEventListener mvalueEventListener;

    public Adapter(@NonNull Context context, int resource, ArrayList<Data> arrayList, DatabaseReference mref, ValueEventListener valueEventListener) {
        super(context, resource, arrayList);
        this.mcontext = context;
        this.mresource = resource;
        this.mreference = mref;
        this.mvalueEventListener = valueEventListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        reference = FirebaseDatabase.getInstance().getReference("UserList").child(uid).child("List");

        view = LayoutInflater.from(mcontext).inflate(mresource, parent, false);
        ConstraintLayout constraintLayout = (ConstraintLayout) view.findViewById(R.id.adapter_constrain);
        TextView title = (TextView) view.findViewById(R.id.textview_title);
        TextView desc = (TextView) view.findViewById(R.id.textView_description);
        TextView date = (TextView) view.findViewById(R.id.textView_data_time);
        TextView star_or_not = (TextView) view.findViewById(R.id.textView_star_not);

        String id = getItem(position).getId();
        title.setText(getItem(position).getTitle());
        desc.setText(getItem(position).getDesc());
        date.setText(getItem(position).getDate());

        star_or_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(position).getStar().equals("YES")) {
                    reference.child(id).child("star").setValue("NO");
                } else {
                    reference.child(id).child("star").setValue("YES");
                }
            }
        });

        if (getItem(position).getStar().equals("YES")) {
            star_or_not.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_stared, 0);
        }

        String color = getItem(position).getColor();

        switch (color) {
            case "red":
                constraintLayout.setBackgroundResource(R.drawable.constrain_red);
                break;
            case "blue":
                constraintLayout.setBackgroundResource(R.drawable.constrain_blue);
                break;
            case "rose":
                constraintLayout.setBackgroundResource(R.drawable.constrain_rose);
                break;
            case "green":
                constraintLayout.setBackgroundResource(R.drawable.constrain_green);
                break;
            case "yellow":
                constraintLayout.setBackgroundResource(R.drawable.constraint_yellow);
                break;
            case "difrose":
                constraintLayout.setBackgroundResource(R.drawable.constrain_difrose);
                break;
            case "orange":
                constraintLayout.setBackgroundResource(R.drawable.constrain_orange);
                break;
            case "darkrose":
                constraintLayout.setBackgroundResource(R.drawable.constrain_darkrose);
                break;
            case "trdblue":
                constraintLayout.setBackgroundResource(R.drawable.constrain_trdblue);
                break;
            default:
                constraintLayout.setBackgroundResource(R.drawable.constrain_black);
        }

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mreference.removeEventListener(mvalueEventListener);
                Intent intent = new Intent(mcontext, Content.class);
                intent.putExtra("pass_title", "" + getItem(position).getTitle());
                intent.putExtra("pass_desc", "" + getItem(position).getDesc());
                intent.putExtra("pass_color", "" + getItem(position).getColor());
                intent.putExtra("pass_id", "" + getItem(position).getId());
                intent.putExtra("pass_all_cre", "already");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mcontext.startActivity(intent);
                ((Activity) mcontext).finish();
            }
        });

        return view;
    }


}
