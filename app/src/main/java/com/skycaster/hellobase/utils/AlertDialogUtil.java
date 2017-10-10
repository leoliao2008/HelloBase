package com.skycaster.hellobase.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skycaster.hellobase.R;

/**
 * Created by 廖华凯 on 2017/9/29.
 */

public class AlertDialogUtil {

    private static AlertDialog alertDialog;

    private AlertDialogUtil(){}

    public static void showBaseDialog(Context context, String title,String msg, View.OnClickListener onConfirm){
        showBaseDialog(context,title,msg,onConfirm,null);
    }

    public static void showBaseDialog(
            Context context,
            String title,
            String msg,
            final View.OnClickListener onConfirm,
            @Nullable final View.OnClickListener onCancel){

        showBaseDialog(
                context,
                title,
                msg,
                null,
                onConfirm,
                null,
                onCancel);
    }

    public static void showBaseDialog(
            Context context,
            String title,
            String msg,
            @Nullable String confirm,
            final View.OnClickListener onConfirm,
            @Nullable String cancel,
            @Nullable final View.OnClickListener onCancel){

       showBaseDialog(
               context,
               title,
               msg,
               confirm,
               onConfirm,
               null,
               null,
               cancel,
               onCancel);
    }

    public static void showBaseDialog(
            Context context,
            String title,
            String msg,
            @Nullable String confirm,
            final View.OnClickListener onConfirm,
            @Nullable String extra,
            @Nullable final View.OnClickListener onExtra,
            @Nullable String cancel,
            @Nullable final View.OnClickListener onCancel){

        View rootView=View.inflate(context, R.layout.dialog_base_dialog,null);
        TextView tv_title=rootView.findViewById(R.id.base_dialog_tv_title);
        TextView tv_msg=rootView.findViewById(R.id.base_dialog_tv_msg);

        Button btn_confirm=rootView.findViewById(R.id.base_dialog_btn_confirm);
        if(!TextUtils.isEmpty(confirm)){
            btn_confirm.setText(confirm);
        }

        Button btn_extra=rootView.findViewById(R.id.base_dialog_btn_extra);
        if(!TextUtils.isEmpty(extra)){
            btn_extra.setText(extra);
            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) btn_extra.getLayoutParams();
            params.weight=1;
        }

        Button btn_cancel=rootView.findViewById(R.id.base_dialog_btn_cancel);
        if(!TextUtils.isEmpty(cancel)){
            btn_cancel.setText(cancel);
        }

        tv_title.setText(title);
        tv_msg.setText(msg);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirm.onClick(v);
                alertDialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCancel!=null){
                    onCancel.onClick(v);
                }
                alertDialog.dismiss();
            }
        });
        btn_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onExtra!=null){
                    onExtra.onClick(v);
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
