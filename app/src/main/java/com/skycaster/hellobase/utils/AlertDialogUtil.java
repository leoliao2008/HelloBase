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
import android.widget.ImageView;
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
        ((TextView)rootView.findViewById(R.id.dialog_server_base_more_tv_form_code)).setText(String.valueOf(base.getFormCode()));
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
        ImageView iv_clearData=rootView.findViewById(R.id.dialog_config_server_base_iv_clear_data);
        final EditText edt_FormCode=rootView.findViewById(R.id.dialog_config_server_base_edt_form_code);
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
        String formCode = String.valueOf(base.getFormCode());
        assignValueToEditText(edt_FormCode,formCode);
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
        iv_clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_ip.setText("");
                edt_port.setText("");
                edt_userName.setText("");
                edt_pw.setText("");
                edt_dataFormat.setText("");
                edt_lat.setText("");
                edt_lng.setText("");
                edt_alt.setText("");
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_formCode = edt_FormCode.getText().toString().trim();
                if(TextUtils.isEmpty(str_formCode)){
                    showToast(context,"业务类型不能为空。");
                    return;
                }
                String str_type = edt_qamType.getText().toString().trim();
                if(TextUtils.isEmpty(str_type)){
                    showToast(context,"调制类型不能为空。");
                    return;
                }
                String str_num = edt_ldcpNum.getText().toString().trim();
                if(TextUtils.isEmpty(str_num)){
                    showToast(context,"FEC码数不能为空。");
                    return;
                }
                String str_rate=edt_ldcpRate.getText().toString().trim();
                if(TextUtils.isEmpty(str_rate)){
                    showToast(context,"FEC码率不能为空。");
                    return;
                }
                String str_size = edt_ldcpSize.getText().toString().trim();
                if(TextUtils.isEmpty(str_size)){
                    showToast(context,"交织深度不能为空。");
                    return;
                }
                String str_ip = edt_ip.getText().toString().trim();
                if(TextUtils.isEmpty(str_ip)){
//                    showToast(context,"IP不能为空。");
//                    return;
                    str_ip="";
                }
                String str_port = edt_port.getText().toString().trim();
                if(TextUtils.isEmpty(str_port)){
//                    showToast(context,"端口不能为空。");
//                    return;
                    str_port="";
                }
                String str_userName = edt_userName.getText().toString().trim();
                if(TextUtils.isEmpty(str_userName)){
//                    showToast(context,"用户名不能为空。");
//                    return;
                    str_userName="";
                }
                String str_pw = edt_pw.getText().toString().trim();
                if(TextUtils.isEmpty(str_pw)){
//                    showToast(context,"密码不能为空。");
//                    return;
                    str_pw="";
                }
                String str_dataFormat = edt_dataFormat.getText().toString().trim();
                if(TextUtils.isEmpty(str_dataFormat)){
//                    showToast(context,"数据格式不能为空。");
//                    return;
                    str_dataFormat="";
                }
                String str_lat = edt_lat.getText().toString().trim();
                if(TextUtils.isEmpty(str_lat)){
//                    showToast(context,"纬度不能为空。");
//                    return;
                    str_lat="";
                }
                String str_lng = edt_lng.getText().toString().trim();
                if(TextUtils.isEmpty(str_lng)){
//                    showToast(context,"经度不能为空。");
//                    return;
                    str_lng="";
                }
                String str_alt = edt_alt.getText().toString().trim();
                if(TextUtils.isEmpty(str_alt)){
//                    showToast(context,"海拔高度不能为空。");
//                    return;
                    str_alt="";
                }
                base.setFormCode(Integer.valueOf(str_formCode));
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
        final EditText edt_startYear=rootView.findViewById(R.id.dialog_sort_log_edt_start_year);
        final EditText edt_startMonth=rootView.findViewById(R.id.dialog_sort_log_edt_start_month);
        final EditText edt_startDay=rootView.findViewById(R.id.dialog_sort_log_edt_start_day);
        final EditText edt_startHour=rootView.findViewById(R.id.dialog_sort_log_edt_start_hour);
        final EditText edt_endYear=rootView.findViewById(R.id.dialog_sort_log_edt_end_year);
        final EditText edt_endMonth=rootView.findViewById(R.id.dialog_sort_log_edt_end_month);
        final EditText edt_endDay=rootView.findViewById(R.id.dialog_sort_log_edt_end_day);
        final EditText edt_endHour=rootView.findViewById(R.id.dialog_sort_log_edt_end_hour);
        Button btn_confirm=rootView.findViewById(R.id.dialog_sort_log_btn_confirm);
        Button btn_cancel=rootView.findViewById(R.id.dialog_sort_log_btn_cancel);
        Button btn_showAll=rootView.findViewById(R.id.dialog_sort_log_btn_showAll);
        //initData
        final SharedPreferences sp=context.getSharedPreferences(StaticData.SP_NAME,Context.MODE_PRIVATE);
        long start = sp.getLong(StaticData.DATE_START, 0);
        if(start>0){
            Date dateStart=new Date(start);
            String year = String.valueOf(1900 + dateStart.getYear());
            edt_startYear.setText(year);
            edt_startYear.setSelection(year.length());
            String month = String.valueOf(dateStart.getMonth()+1);
            edt_startMonth.setText(month);
            edt_startMonth.setSelection(month.length());
            String day = String.valueOf(dateStart.getDate());
            edt_startDay.setText(day);
            edt_startDay.setSelection(day.length());
            String hour=String.valueOf(dateStart.getHours());
            edt_startHour.setText(hour);
            edt_startHour.setSelection(hour.length());
        }
        long end = sp.getLong(StaticData.DATE_END, 0);
        if(end>0){
            Date dateEnd=new Date(end);
            String year = String.valueOf(1900 + dateEnd.getYear());
            edt_endYear.setText(year);
            edt_endYear.setSelection(year.length());
            String month = String.valueOf(dateEnd.getMonth()+1);
            edt_endMonth.setText(month);
            edt_endMonth.setSelection(month.length());
            String day = String.valueOf(dateEnd.getDate());
            edt_endDay.setText(day);
            edt_endDay.setSelection(day.length());
            String hour=String.valueOf(dateEnd.getHours());
            edt_endHour.setText(hour);
            edt_endHour.setSelection(hour.length());
        }
        //initListener
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string_startYear = edt_startYear.getText().toString();
                int int_startYear;
                if(TextUtils.isEmpty(string_startYear)){
                    showToast(context,"请输入起始年份。");
                    return;
                }else {
                    int_startYear=Integer.valueOf(string_startYear.trim());
                    if(int_startYear>2099||int_startYear<2017){
                        showToast(context,"起始年份输入有误，请检查。");
                        return;
                    }
                }
                String string_startMonth = edt_startMonth.getText().toString();
                int int_startMonth;
                if(TextUtils.isEmpty(string_startMonth)){
                    showToast(context,"请输入起始月份。");
                    return;
                }else {
                    int_startMonth=Integer.valueOf(string_startMonth.trim());
                    if(int_startMonth<1||int_startMonth>12){
                        showToast(context,"起始月份输入有误，请检查。");
                        return;
                    }
                }
                String string_startDay = edt_startDay.getText().toString();
                int int_startDay;
                if(TextUtils.isEmpty(string_startDay)){
                    showToast(context,"请输入起始日。");
                    return;
                }else {
                    int_startDay=Integer.valueOf(string_startDay.trim());
                    //1,3,5,7,8,10,12月有31天
                    //4,6,9,11月有30天
                    //平年2月29天，闰年2月28天
                    if(int_startMonth==1||int_startMonth==3||int_startMonth==5||int_startMonth==7||int_startMonth==8||int_startMonth==10||int_startMonth==12){
                        if(int_startDay<0||int_startDay>31){
                            showToast(context,"起始日输入有误，请检查。");
                            return;
                        }
                    }else if(int_startMonth==4||int_startMonth==6||int_startMonth==9||int_startMonth==11){
                        if(int_startDay<0||int_startDay>30){
                            showToast(context,"起始日输入有误，请检查。");
                            return;
                        }
                    }else {
                        boolean isLeapYear=false;
                        if(int_startYear%100==0){
                            if(int_startYear%400==0){
                                isLeapYear=true;
                            }
                        }else if(int_startYear%4==0){
                            isLeapYear=true;
                        }
                        if(isLeapYear){
                            if(int_startDay<0||int_startDay>28){
                                showToast(context,"起始日输入有误，请检查。");
                                return;
                            }
                        }else {
                            if(int_startDay<0||int_startDay>29){
                                showToast(context,"起始日输入有误，请检查。");
                                return;
                            }
                        }
                    }
                }
                String string_startHour = edt_startHour.getText().toString();
                int int_startHour;
                if(TextUtils.isEmpty(string_startHour)){
                    showToast(context,"请输入起始时");
                    return;
                }else {
                    int_startHour=Integer.valueOf(string_startHour.trim());
                    if(int_startHour<0||int_startHour>24){
                        showToast(context,"起始时输入有误，请检查。");
                        return;
                    }
                }
                String string_endYear = edt_endYear.getText().toString();
                int int_endYear;
                if(TextUtils.isEmpty(string_endYear)){
                    showToast(context,"请输入结束年份。");
                    return;
                }else {
                    int_endYear=Integer.valueOf(string_endYear.trim());
                    if(int_endYear>2099||int_endYear<2017){
                        showToast(context,"结束年输入有误，请检查。");
                        return;
                    }
                }
                String string_endMonth = edt_endMonth.getText().toString();
                int int_endMonth;
                if(TextUtils.isEmpty(string_endMonth)){
                    showToast(context,"请输入结束月份。");
                    return;
                }else {
                    int_endMonth=Integer.valueOf(string_endMonth.trim());
                    if(int_endMonth<1||int_endMonth>12){
                        showToast(context,"结束月输入有误，请检查。");
                        return;
                    }
                }
                String string_endDay = edt_endDay.getText().toString();
                int int_endDay;
                if(TextUtils.isEmpty(string_endDay)){
                    showToast(context,"请输入结束日。");
                    return;
                }else {
                    int_endDay=Integer.valueOf(string_endDay.trim());
                    if(int_endMonth==1||int_endMonth==3||int_endMonth==5||int_endMonth==7||int_endMonth==8||int_endMonth==10||int_endMonth==12){
                        if(int_endDay<0||int_endDay>31){
                            showToast(context,"结束日输入有误，请检查。");
                            return;
                        }
                    }else if(int_endMonth==4||int_endMonth==6||int_endMonth==9||int_endMonth==11){
                        if(int_endDay<0||int_endDay>30){
                            showToast(context,"结束日输入有误，请检查。");
                            return;
                        }
                    }else {
                        boolean isLeapYear = false;
                        if (int_endYear % 100 == 0) {
                            if (int_endYear % 400 == 0) {
                                isLeapYear = true;
                            }
                        } else if (int_endYear % 4 == 0) {
                            isLeapYear = true;
                        }
                        if (isLeapYear) {
                            if (int_endDay < 0 || int_endDay > 28) {
                                showToast(context, "结束日输入有误，请检查。");
                                return;
                            }
                        } else {
                            if (int_endDay < 0 || int_endDay > 29) {
                                showToast(context, "结束日输入有误，请检查。");
                                return;
                            }
                        }
                    }
                }
                String string_endHour = edt_endHour.getText().toString();
                int int_endHour;
                if(TextUtils.isEmpty(string_endHour)){
                    showToast(context,"结束时输入有误，请检查。");
                    return;
                }else {
                    int_endHour=Integer.valueOf(string_endHour.trim());
                    if(int_endHour<0||int_endHour>24){
                        showToast(context,"结束时输入有误，请检查。");
                        return;
                    }
                }
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHH",Locale.CHINA);
                try {
                    Date dateStart=simpleDateFormat.parse(
                                    String.format(Locale.CHINA,"%04d",int_startYear)+
                                    String.format(Locale.CHINA,"%02d",int_startMonth)+
                                    String.format(Locale.CHINA,"%02d",int_startDay)+
                                    String.format(Locale.CHINA,"%02d",int_startHour));
                    Date dateEnd=simpleDateFormat.parse(
                                    String.format(Locale.CHINA,"%04d",int_endYear)+
                                    String.format(Locale.CHINA,"%02d",int_endMonth)+
                                    String.format(Locale.CHINA,"%02d",int_endDay)+
                                    String.format(Locale.CHINA,"%02d",int_endHour));
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
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_showAll.setOnClickListener(new View.OnClickListener() {
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
