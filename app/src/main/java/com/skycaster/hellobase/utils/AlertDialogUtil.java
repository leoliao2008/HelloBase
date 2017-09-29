package com.skycaster.hellobase.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.skycaster.hellobase.R;

/**
 * Created by 廖华凯 on 2017/9/29.
 */

public class AlertDialogUtil {

    private static AlertDialog alertDialog;

    private AlertDialogUtil(){}

    public static void showBaseDialog(Context context, String title,String msg, View.OnClickListener confirm){
        showBaseDialog(context,title,msg,confirm,null);
    }

    public static void showBaseDialog(Context context, String title, String msg, final View.OnClickListener confirm, @Nullable final View.OnClickListener cancel){
        View rootView=View.inflate(context, R.layout.dialog_base_dialog,null);
        TextView tv_title=rootView.findViewById(R.id.base_dialog_tv_title);
        TextView tv_msg=rootView.findViewById(R.id.base_dialog_tv_msg);
        Button btn_confirm=rootView.findViewById(R.id.base_dialog_btn_confirm);
        Button btn_cancel=rootView.findViewById(R.id.base_dialog_btn_cancel);
        tv_title.setText(title);
        tv_msg.setText(msg);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.onClick(v);
                alertDialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cancel!=null){
                    cancel.onClick(v);
                }
                alertDialog.dismiss();
            }
        });
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(rootView).setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
    }
}
