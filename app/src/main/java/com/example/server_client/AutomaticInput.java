package com.example.server_client;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AutomaticInput extends Fragment {

    int status_bar_height=0;
    MainActivity mainActivity;
    public AutomaticInput(int status_bar_height,MainActivity mainActivity) {
        this.status_bar_height=status_bar_height;
        this.mainActivity=mainActivity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    TextView t01,t02,t03;
    LinearLayout L01;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fr_automatic_input, container, false);
        LinearLayout L00=(LinearLayout)view.findViewById(R.id.L00);
        L01=(LinearLayout)view.findViewById(R.id.L01);
        TextView t01=(TextView) view.findViewById(R.id.t01);
        TextView t02=(TextView) view.findViewById(R.id.t02);
        TextView t03=(TextView) view.findViewById(R.id.t03);

        t02.setOnClickListener(addView());


        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)L00.getLayoutParams();
               params.setMargins(0, 0+status_bar_height, 0,0);
        L00.setLayoutParams(params);
        return view;
    }

    View.OnClickListener addView() {
        return new View.OnClickListener() {
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);
                View viewInflated = LayoutInflater.from(getContext()).
                        inflate(R.layout.add_au_in, (ViewGroup) getView(), false);

                final EditText input1 = (EditText) viewInflated.findViewById(R.id.input1);
                final EditText input2 = (EditText) viewInflated.findViewById(R.id.input2);
                dialog.setView(viewInflated);
                dialog.setPositiveButton("新增", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        addText(input1.getText().toString(),input2.getText().toString());
                    }
                }).show();

            }
        };

    }
    View.OnClickListener AutoEnter(final String enterTextS) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("zzzzzzzzzzz","dddddddddd");
                ((MainActivity)getActivity()).upText(enterTextS);
                MainActivity.drawer.closeDrawer(GravityCompat.END);
            }
        };

    }
    //annotation:註解
    public void addText(final String text, final String annotation) {
        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        param1.setMargins(24, 0, 24,0);
        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        param2.setMargins(0, 0, 0,10);
        LinearLayout linearLayout = new LinearLayout(mainActivity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView t01 = new TextView(mainActivity);
        t01.setText(text);
        t01.setTextSize(20);
        t01.setGravity(Gravity.LEFT);
        t01.setLayoutParams(param1);

        TextView Space = new TextView(mainActivity);
        Space.setText("");
        Space.setTextSize(4);
        Space.setGravity(Gravity.RIGHT);
        Space.setLayoutParams(param1);

        TextView Annotation = new TextView(mainActivity);
        Annotation.setText("註解:"+annotation);
        Annotation.setTextSize(12);
        Annotation.setGravity(Gravity.RIGHT);
        Annotation.setLayoutParams(param1);

        TextView line = new TextView(mainActivity);
        line.setText(text);
        line.setTextSize(1);
        line.setBackgroundColor(0xFFE0E0E0);

        line.setLayoutParams(param2);
        line.setGravity(Gravity.LEFT);

        linearLayout.addView(Space);
        linearLayout.addView(t01);
        linearLayout.addView(Annotation);
        linearLayout.addView(line);

        linearLayout.setOnClickListener(AutoEnter(text));

        L01.addView(linearLayout);

    }
}