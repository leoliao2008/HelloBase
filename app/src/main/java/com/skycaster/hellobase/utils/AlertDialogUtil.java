package com.skycaster.hellobase.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.bean.ServerBase;

import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/9/29.
 */

public class AlertDialogUtil {

    private static AlertDialog alertDialog;

    private AlertDialogUtil() {
    }

    public static void showBaseDialog(Context context, String title, String msg, View.OnClickListener onConfirm) {
        showBaseDialog(context, title, msg, onConfirm, null);
    }

    public static void showBaseDialog(
            Context context,
            String title,
            String msg,
            final View.OnClickListener onConfirm,
            @Nullable final View.OnClickListener onCancel) {

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
            @Nullable final View.OnClickListener onCancel) {

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
            @Nullable final View.OnClickListener onCancel) {

        View rootView = View.inflate(context, R.layout.dialog_base_dialog, null);
        TextView tv_title = rootView.findViewById(R.id.base_dialog_tv_title);
        TextView tv_msg = rootView.findViewById(R.id.base_dialog_tv_msg);

        Button btn_confirm = rootView.findViewById(R.id.base_dialog_btn_confirm);
        if (!TextUtils.isEmpty(confirm)) {
            btn_confirm.setText(confirm);
        }

        Button btn_extra = rootView.findViewById(R.id.base_dialog_btn_extra);
        if (!TextUtils.isEmpty(extra)) {
            btn_extra.setText(extra);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btn_extra.getLayoutParams();
            params.weight = 1;
        }

        Button btn_cancel = rootView.findViewById(R.id.base_dialog_btn_cancel);
        if (!TextUtils.isEmpty(cancel)) {
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
                if (onCancel != null) {
                    onCancel.onClick(v);
                }
                alertDialog.dismiss();
            }
        });
        btn_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onExtra != null) {
                    onExtra.onClick(v);
                }
                alertDialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(rootView).setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showServerBaseDetails(Context context, ServerBase base,int position) {
        View rootView = View.inflate(context, R.layout.dialog_base_server_more, null);
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_order)).setText(String.format(Locale.CHINA,"%02d",position));
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_qam_type)).setText(base.getQamType()+"");
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_altitude)).setText(base.getAltitude());
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_data_format)).setText(base.getDataFormat());
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_ip)).setText(base.getIp());
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_latitude)).setText(base.getLatitude());
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_ldcp_num)).setText(base.getLdpcNum()+"");
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_ldcp_rate)).setText(base.getLdpcRate()+"");
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_ldcp_size)).setText(base.getIntvSize()+"");
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_port)).setText(base.getPort());
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_user_name)).setText(base.getUserName());
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_pw)).setText(base.getPw());
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_longitude)).setText(base.getLongitude());
        rootView.findViewById(R.id.dialog_server_base_more_btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        alertDialog=builder.setView(rootView).setCancelable(true).create();
        alertDialog.show();


    }

    public interface ServerBaseEditListener {
        void onConfirmEdit(ServerBase confirm);
    }

    public static void showEditServerBaseDialog(final Context context, final ServerBase base, final ServerBaseEditListener listener) {
        //init view
        View rootView=View.inflate(context, R.layout.dialog_edit_base_server,null);
        final EditText edt_qamType=rootView.findViewById(R.id.dialog_config_server_base_edt_qam_type);
        final EditText edt_ldcpNum=rootView.findViewById(R.id.dialog_config_server_base_edt_ldcp_num);
        final EditText edt_ldcpSize=rootView.findViewById(R.id.dialog_config_server_base_edt_ldcp_size);
        final EditText edt_ldcpRate=rootView.findViewById(R.id.dialog_config_server_base_edt_ldcp_rate);
//        #'服务器IP, 端口号, 用户名, 密码, 数据格式, 经度, 纬度, 高度'
        final EditText edt_ip=rootView.findViewById(R.id.dialog_config_server_base_edt_ip);
        final EditText edt_port=rootView.findViewById(R.id.dialog_config_server_base_edt_port);
        final EditText edt_userName=rootView.findViewById(R.id.dialog_config_server_base_edt_user_name);
        final EditText edt_pw=rootView.findViewById(R.id.dialog_config_server_base_edt_pw);
        final EditText edt_dataFormat=rootView.findViewById(R.id.dialog_config_server_base_edt_data_format);
        final EditText edt_lng=rootView.findViewById(R.id.dialog_config_server_base_edt_lng);
        final EditText edt_lat=rootView.findViewById(R.id.dialog_config_server_base_edt_lat);
        final EditText edt_alt=rootView.findViewById(R.id.dialog_config_server_base_edt_alt);
        TextView tv_order=rootView.findViewById(R.id.dialog_config_server_base_tv_order);
        Button btn_confirm=rootView.findViewById(R.id.dialog_config_server_base_btn_confirm);
        Button btn_cancel=rootView.findViewById(R.id.dialog_config_server_base_btn_cancel);
        //init data
        String type=String.valueOf(base.getQamType());
        assignValueToEditText(edt_qamType,type);
        String rate = String.valueOf(base.getLdpcRate());
        assignValueToEditText(edt_ldcpRate,rate);
        String num = String.valueOf(base.getLdpcNum());
        assignValueToEditText(edt_ldcpNum,num);
        String size = String.valueOf(base.getIntvSize());
        assignValueToEditText(edt_ldcpSize,size);
        tv_order.setText(String.format(Locale.CHINA,"%02d",base.getId()));
        assignValueToEditText(edt_ip,base.getIp());
        assignValueToEditText(edt_port,base.getPort());
        assignValueToEditText(edt_userName,base.getUserName());
        assignValueToEditText(edt_pw,base.getPw());
        assignValueToEditText(edt_dataFormat,base.getDataFormat());
        assignValueToEditText(edt_lat,base.getLatitude());
        assignValueToEditText(edt_lng,base.getLongitude());
        assignValueToEditText(edt_alt,base.getAltitude());
        //int listeners
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_type = edt_qamType.getText().toString().trim();
                if(TextUtils.isEmpty(str_type)){
                    showToast(context,"调制类型不能为空。");
                    return;
                }
                String str_num = edt_ldcpNum.getText().toString().trim();
                if(TextUtils.isEmpty(str_num)){
                    showToast(context,"LDCP码字个数不能为空。");
                    return;
                }
                String str_rate=edt_ldcpRate.getText().toString().trim();
                if(TextUtils.isEmpty(str_rate)){
                    showToast(context,"LDCP编码率不能为空。");
                    return;
                }
                String str_size = edt_ldcpSize.getText().toString().trim();
                if(TextUtils.isEmpty(str_size)){
                    showToast(context,"交织块大小不能为空。");
                    return;
                }
                String str_ip = edt_ip.getText().toString().trim();
                if(TextUtils.isEmpty(str_ip)){
                    showToast(context,"IP不能为空。");
                    return;
                }
                String str_port = edt_port.getText().toString().trim();
                if(TextUtils.isEmpty(str_port)){
                    showToast(context,"端口不能为空。");
                    return;
                }
                String str_userName = edt_userName.getText().toString().trim();
                if(TextUtils.isEmpty(str_userName)){
                    showToast(context,"用户名不能为空。");
                    return;
                }
                String str_pw = edt_pw.getText().toString().trim();
                if(TextUtils.isEmpty(str_pw)){
                    showToast(context,"密码不能为空。");
                    return;
                }
                String str_dataFormat = edt_dataFormat.getText().toString().trim();
                if(TextUtils.isEmpty(str_dataFormat)){
                    showToast(context,"数据格式不能为空。");
                    return;
                }
                String str_lat = edt_lat.getText().toString().trim();
                if(TextUtils.isEmpty(str_lat)){
                    showToast(context,"纬度不能为空。");
                    return;
                }
                String str_lng = edt_lng.getText().toString().trim();
                if(TextUtils.isEmpty(str_lng)){
                    showToast(context,"经度不能为空。");
                    return;
                }
                String str_alt = edt_alt.getText().toString().trim();
                if(TextUtils.isEmpty(str_alt)){
                    showToast(context,"海拔高度不能为空。");
                    return;
                }
                base.setIp(str_ip);
                base.setPw(str_pw);
                base.setPort(str_port);
                base.setUserName(str_userName);
                base.setDataFormat(str_dataFormat);
                base.setLatitude(str_lat);
                base.setLongitude(str_lng);
                base.setAltitude(str_alt);
                base.setQamType(Integer.valueOf(str_type));
                base.setIntvSize(Integer.valueOf(str_size));
                base.setLdpcNum(Integer.valueOf(str_num));
                base.setLdpcRate(Integer.valueOf(str_rate));
                listener.onConfirmEdit(base);
                alertDialog.dismiss();
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        alertDialog = builder.setView(rootView).setCancelable(true).create();
        alertDialog.show();
    }

    private static void showToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    private static void assignValueToEditText(EditText editText, String value){
        editText.setText(value);
        editText.setSelection(value.length());
    }
}
