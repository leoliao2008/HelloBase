package com.skycaster.hellobase.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.skycaster.hellobase.data.StaticData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/9/29.
 */

public class AlertDialogUtil {

    private static AlertDialog alertDialog;
    private static ProgressDialog progressDialog;

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

    public static void showProgressDialog(Context context){
        progressDialog = ProgressDialog.show(
                context,
                "连接中",
                "正在连接数据库，请稍候......",
                true,
                false

        );
    }

    public static void dismissProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    public static void showSortLogsDialog(final Context context, final SortLogRangeListener listener){
        View rootView=View.inflate(context,R.layout.dialog_sort_logs,null);
        //initView
        final EditText startYear=rootView.findViewById(R.id.dialog_sort_log_edt_start_year);
        final EditText startMonth=rootView.findViewById(R.id.dialog_sort_log_edt_start_month);
        final EditText startDay=rootView.findViewById(R.id.dialog_sort_log_edt_start_day);
        final EditText startHour=rootView.findViewById(R.id.dialog_sort_log_edt_start_hour);
        final EditText endYear=rootView.findViewById(R.id.dialog_sort_log_edt_end_year);
        final EditText endMonth=rootView.findViewById(R.id.dialog_sort_log_edt_end_month);
        final EditText endDay=rootView.findViewById(R.id.dialog_sort_log_edt_end_day);
        final EditText endHour=rootView.findViewById(R.id.dialog_sort_log_edt_end_hour);
        Button confirm=rootView.findViewById(R.id.dialog_sort_log_btn_confirm);
        Button cancel=rootView.findViewById(R.id.dialog_sort_log_btn_cancel);
        Button showAll=rootView.findViewById(R.id.dialog_sort_log_btn_showAll);
        //initData
        final SharedPreferences sp=context.getSharedPreferences(StaticData.SP_NAME,Context.MODE_PRIVATE);
        long start = sp.getLong(StaticData.DATE_START, 0);
        if(start>0){
            Date dateStart=new Date(start);
            String year = String.valueOf(1900 + dateStart.getYear());
            startYear.setText(year);
            startYear.setSelection(year.length());
            String month = String.valueOf(dateStart.getMonth()+1);
            startMonth.setText(month);
            startMonth.setSelection(month.length());
            String day = String.valueOf(dateStart.getDate());
            startDay.setText(day);
            startDay.setSelection(day.length());
            String hour=String.valueOf(dateStart.getHours());
            startHour.setText(hour);
            startHour.setSelection(hour.length());
        }
        long end = sp.getLong(StaticData.DATE_END, 0);
        if(end>0){
            Date dateEnd=new Date(end);
            String year = String.valueOf(1900 + dateEnd.getYear());
            endYear.setText(year);
            endYear.setSelection(year.length());
            String month = String.valueOf(dateEnd.getMonth()+1);
            endMonth.setText(month);
            endMonth.setSelection(month.length());
            String day = String.valueOf(dateEnd.getDate());
            endDay.setText(day);
            endDay.setSelection(day.length());
            String hour=String.valueOf(dateEnd.getHours());
            endHour.setText(hour);
            endHour.setSelection(hour.length());
        }
        //initListener
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stY = startYear.getText().toString();
                int intStY;
                if(TextUtils.isEmpty(stY)){
                    showToast(context,"请输入起始年份。");
                    return;
                }else {
                    intStY=Integer.valueOf(stY.trim());
                    if(intStY>2099||intStY<2017){
                        showToast(context,"起始年份输入有误，请检查。");
                        return;
                    }
                }
                String stM = startMonth.getText().toString();
                int intStM;
                if(TextUtils.isEmpty(stM)){
                    showToast(context,"请输入起始月份。");
                    return;
                }else {
                    intStM=Integer.valueOf(stM.trim());
                    if(intStM<1||intStM>12){
                        showToast(context,"起始月份输入有误，请检查。");
                        return;
                    }
                }
                String stD = startDay.getText().toString();
                int intStD;
                if(TextUtils.isEmpty(stD)){
                    showToast(context,"请输入起始日。");
                    return;
                }else {
                    intStD=Integer.valueOf(stD.trim());
                    //1,3,5,7,8,10,12月有31天
                    //4,6,9,11月有30天
                    //平年2月29天，闰年2月28天
                    if(intStM==1||intStM==3||intStM==5||intStM==7||intStM==8||intStM==10||intStM==12){
                        if(intStD<0||intStD>31){
                            showToast(context,"起始日输入有误，请检查。");
                            return;
                        }
                    }else if(intStM==4||intStM==6||intStM==9||intStM==11){
                        if(intStD<0||intStD>30){
                            showToast(context,"起始日输入有误，请检查。");
                            return;
                        }
                    }else {
                        boolean isLeapYear=false;
                        if(intStY%100==0){
                            if(intStY%400==0){
                                isLeapYear=true;
                            }
                        }else if(intStY%4==0){
                            isLeapYear=true;
                        }
                        if(isLeapYear){
                            if(intStD<0||intStD>28){
                                showToast(context,"起始日输入有误，请检查。");
                                return;
                            }
                        }else {
                            if(intStD<0||intStD>29){
                                showToast(context,"起始日输入有误，请检查。");
                                return;
                            }
                        }
                    }
                }
                String stH = startHour.getText().toString();
                int intStH;
                if(TextUtils.isEmpty(stH)){
                    showToast(context,"请输入起始时");
                    return;
                }else {
                    intStH=Integer.valueOf(stH.trim());
                    if(intStH<0||intStH>24){
                        showToast(context,"起始时输入有误，请检查。");
                        return;
                    }
                }
                String endY = endYear.getText().toString();
                int intEndY;
                if(TextUtils.isEmpty(endY)){
                    showToast(context,"请输入结束年份。");
                    return;
                }else {
                    intEndY=Integer.valueOf(endY.trim());
                    if(intEndY>2099||intEndY<2017){
                        showToast(context,"结束年输入有误，请检查。");
                        return;
                    }
                }
                String endM = endMonth.getText().toString();
                int intEndM;
                if(TextUtils.isEmpty(endM)){
                    showToast(context,"请输入结束月份。");
                    return;
                }else {
                    intEndM=Integer.valueOf(endM.trim());
                    if(intEndM<1||intEndM>12){
                        showToast(context,"结束月输入有误，请检查。");
                        return;
                    }
                }
                String endD = endDay.getText().toString();
                int intEndD;
                if(TextUtils.isEmpty(endD)){
                    showToast(context,"请输入结束日。");
                    return;
                }else {
                    intEndD=Integer.valueOf(endD.trim());
                    if(intEndM==1||intEndM==3||intEndM==5||intEndM==7||intEndM==8||intEndM==10||intEndM==12){
                        if(intEndD<0||intEndD>31){
                            showToast(context,"结束日输入有误，请检查。");
                            return;
                        }
                    }else if(intEndM==4||intEndM==6||intEndM==9||intEndM==11){
                        if(intEndD<0||intEndD>30){
                            showToast(context,"结束日输入有误，请检查。");
                            return;
                        }
                    }else {
                        boolean isLeapYear = false;
                        if (intEndY % 100 == 0) {
                            if (intEndY % 400 == 0) {
                                isLeapYear = true;
                            }
                        } else if (intEndY % 4 == 0) {
                            isLeapYear = true;
                        }
                        if (isLeapYear) {
                            if (intEndD < 0 || intEndD > 28) {
                                showToast(context, "结束日输入有误，请检查。");
                                return;
                            }
                        } else {
                            if (intEndD < 0 || intEndD > 29) {
                                showToast(context, "结束日输入有误，请检查。");
                                return;
                            }
                        }
                    }
                }
                String endH = endHour.getText().toString();
                int intEndH;
                if(TextUtils.isEmpty(endH)){
                    showToast(context,"结束时输入有误，请检查。");
                    return;
                }else {
                    intEndH=Integer.valueOf(endH.trim());
                    if(intEndH<0||intEndH>24){
                        showToast(context,"结束时输入有误，请检查。");
                        return;
                    }
                }
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHH",Locale.CHINA);
                try {
                    Date dateStart=simpleDateFormat.parse(
                                    String.format(Locale.CHINA,"%04d",intStY)+
                                    String.format(Locale.CHINA,"%02d",intStM)+
                                    String.format(Locale.CHINA,"%02d",intStD)+
                                    String.format(Locale.CHINA,"%02d",intStH));
                    Date dateEnd=simpleDateFormat.parse(
                                    String.format(Locale.CHINA,"%04d",intEndY)+
                                    String.format(Locale.CHINA,"%02d",intEndM)+
                                    String.format(Locale.CHINA,"%02d",intEndD)+
                                    String.format(Locale.CHINA,"%02d",intEndH));
                    if(dateEnd.compareTo(dateStart)<0){
                        showToast(context,"结束日期不可小于起始日期。");
                        return;
                    }
                    alertDialog.dismiss();
                    sp.edit().putLong(StaticData.DATE_START,dateStart.getTime()).putLong(StaticData.DATE_END,dateEnd.getTime()).apply();
                    listener.onRangeSelected(dateStart,dateEnd);

                } catch (ParseException e) {
                    alertDialog.dismiss();
                    listener.onError(e.getMessage());
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                listener.onChooseToShowAll();
            }
        });
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        alertDialog=builder.setView(rootView).setCancelable(true).create();
        alertDialog.show();
    }

    public interface SortLogRangeListener{
        void onRangeSelected(Date start,Date end);
        void onError(String errorInfo);
        void onChooseToShowAll();
    }
}
