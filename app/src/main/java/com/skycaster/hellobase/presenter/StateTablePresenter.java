package com.skycaster.hellobase.presenter;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.activity.ConfigActivity;
import com.skycaster.hellobase.activity.LogActivity;
import com.skycaster.hellobase.activity.StateActivity;
import com.skycaster.hellobase.base.BaseApplication;
import com.skycaster.hellobase.bean.ConfigTable;
import com.skycaster.hellobase.bean.StateTable;
import com.skycaster.hellobase.bean.UserBean;
import com.skycaster.hellobase.customize.MutableColorSpan;
import com.skycaster.hellobase.customize.MutableSizeSpan;
import com.skycaster.hellobase.data.StaticData;
import com.skycaster.hellobase.interf.MySqlModelCallBack;
import com.skycaster.hellobase.model.MySqlModel;
import com.skycaster.hellobase.service.ServerConStatusMonitor;
import com.skycaster.hellobase.utils.AlertDialogUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/9/13.
 */

public class StateTablePresenter {
    private StateActivity mActivity;
    private DateFormat mDateFormat;
    private StateTable mStateTable;
    private Receiver mReceiver;
    private TextView tv_statusReport;
    private ImageView iv_netStateIcon;
    private int mNetStatus;
    private TextView tv_updateTime;
    private float mTextSize;
    private MySqlModel mMySqlModel;
    private ProgressDialog mProgressDialog;
    private Drawable mAslDrawable;


    public StateTablePresenter(StateActivity activity) {
        mActivity = activity;
        mMySqlModel=new MySqlModel(new MySqlModelCallBack(){

            @Override
            public void onSqlConnectionFail(String msg) {
                super.onSqlConnectionFail(msg);
                dismissProgressDialog();
                mActivity.showToast(msg);
            }

            @Override
            public void onGetConfigTableSuccess(final ArrayList<ConfigTable> tables) {
                dismissProgressDialog();
                showLog("config tables get:"+tables.toString());
                toConfigTableActivity(tables.get(0));
            }

            @Override
            public void onGetStateTablesError(String msg) {
                super.onGetStateTablesError(msg);
                dismissProgressDialog();
                mActivity.showToast(msg);
            }

            @Override
            public void onTargetConfigTableNotExist() {
                super.onTargetConfigTableNotExist();
                dismissProgressDialog();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialogCreateConfigTable();
                    }
                });
            }

            @Override
            public void onGetConfigTableError(String error) {
                dismissProgressDialog();
                mActivity.showToast(error);
            }

            @Override
            public void onCreateNewConfigTableSuccess(final ConfigTable table) {
                super.onCreateNewConfigTableSuccess(table);
                dismissProgressDialog();
                toConfigTableActivity(table);
            }

            @Override
            public void onCreateNewConfigTableFails(String s) {
                super.onCreateNewConfigTableFails(s);
                dismissProgressDialog();
                mActivity.showToast(s);
            }
        });
    }

    private void toConfigTableActivity(final ConfigTable configTable) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(mActivity,ConfigActivity.class);
                intent.putExtra(StaticData.EXTRA_DATA_CONFIG_TABLE,configTable);
                mActivity.startActivityForResult(intent,StaticData.REQUEST_CODE_EDIT_CONFIG_TABLE);
            }
        });
    }


    public void initData(){

        tv_updateTime=mActivity.getTv_feedbackTime();
        mTextSize = tv_updateTime.getTextSize();

        tv_statusReport=mActivity.getTv_statusReport();
        iv_netStateIcon =mActivity.getIv_netStateIcon();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            mAslDrawable = mActivity.getDrawable(R.drawable.asl_net_status_icon);
            iv_netStateIcon.setImageDrawable(mAslDrawable);
        }
        mNetStatus=mActivity.getIntent().getIntExtra(StaticData.EXTRA_INT_NET_STATUS_CODE,StaticData.EXTRA_INT_NET_STATUS_MONITOR_CLOSE);

//        2017-10-08 09:14:18”
        mDateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        mStateTable= mActivity.getIntent().getParcelableExtra(StaticData.EXTRA_DATA_STATE_TABLE);
        ActionBar supportActionBar = mActivity.getSupportActionBar();
        if(supportActionBar!=null){
            supportActionBar.setTitle(mStateTable.getTheOwner());
        }
        if(mStateTable!=null){
            updateActivityUi(mStateTable);
            //假如已经有前台服务在运行
            StateTable table = BaseApplication.getCurrentStateTable();
            if(table !=null){
                //更新前台服务对象
                String newStbId=TextUtils.isEmpty(mStateTable.getHostId())?"null":mStateTable.getHostId();
                String preStbId=TextUtils.isEmpty(table.getHostId())?"null":table.getHostId();
                if(!newStbId.equals(preStbId)){
                    stopMonitoring();
                    startMonitoring();
                }else {
                    mNetStatus=table.getStateCode();
                }
            }
        }

        mReceiver=new Receiver();
        IntentFilter intentFilter=new IntentFilter(StaticData.ACTION_SEVER_CON_STATUS_MONITOR);
        mActivity.registerReceiver(mReceiver,intentFilter);

    }


    private void showLog(String msg) {
        Log.e(getClass().getSimpleName(),msg);
    }

    public void updateActivityUi(final StateTable stateTable) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.getTv_mac().setText(stateTable.getHostId());
                mActivity.getTv_version().setText(String.valueOf(stateTable.getCurVer()));
                mActivity.getTv_status().setText(stateTable.getRunningState());
//                mActivity.getTv_feedbackTime().setText(mDateFormat.format(new Date(stateTable.getDateTime())));
                feedbackTimeAnimation(stateTable);
                mActivity.getTv_comments().setText(stateTable.getNotes());
            }
        });
    }

    public void startMonitoring(){
        Intent intent = new Intent(mActivity, ServerConStatusMonitor.class);
        intent.putExtra(StaticData.EXTRA_DATA_STATE_TABLE,mStateTable);
        mActivity.startService(intent);
    }

    public void stopMonitoring(){
        mActivity.stopService(new Intent(mActivity, ServerConStatusMonitor.class));
    }

    public void toLogActivity(){
        LogActivity.start(mActivity,mStateTable.getHostId());
    }

    public void onStart() {
        boolean isServiceRunning = mActivity.getIntent().getBooleanExtra(StaticData.EXTRA_BOOLEAN_IS_SERVICE_RUNNING, false)||BaseApplication.getCurrentStateTable()!=null;
        mActivity.getToggleButton().setChecked(isServiceRunning);
        updateStatusReport(mNetStatus);
    }

    public void onStop() {
        if(!mActivity.isFinishing()) {
            mActivity.getIntent().putExtra(StaticData.EXTRA_BOOLEAN_IS_SERVICE_RUNNING, mActivity.getToggleButton().isChecked());
        }
        if(mActivity.isFinishing()){
            mActivity.unregisterReceiver(mReceiver);
            mReceiver=null;
        }
    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(!mActivity.getToggleButton().isChecked()){
                mActivity.getToggleButton().setChecked(true);
            }
            int type = intent.getIntExtra(StaticData.EXTRA_INT_EVENT_TYPE, 0);
            switch (type){
                case StaticData.EVENT_TYPE_NET_STATUS:
                    boolean isSuccess = intent.getBooleanExtra(StaticData.EXTRA_BOOLEAN_IS_SUCCESS, false);
                    if(isSuccess){
                        mStateTable = intent.getParcelableExtra(StaticData.EXTRA_DATA_STATE_TABLE);
                        updateActivityUi(mStateTable);
                    }else {
                        mActivity.getTv_feedbackTime().setText("获取失败");
                        String error = intent.getStringExtra(StaticData.EXTRA_STRING_INFO);
                        mActivity.getTv_comments().setText(TextUtils.isEmpty(error)?"获取状态表失败，原因：未知":"获取状态表失败，原因："+error);
                    }
                    break;
                case StaticData.EVENT_TYPE_SERVICE_DISMISS:
                    mActivity.getToggleButton().setChecked(false);
                    break;
                case 0:
                default:
                    break;
            }
            updateStatusReport(intent);
        }
    }

    private void updateStatusReport(Intent intent) {
        mNetStatus = intent.getIntExtra(StaticData.EXTRA_INT_NET_STATUS_CODE, StaticData.EXTRA_INT_NET_STATUS_INITIALIZING);
        updateStatusReport(mNetStatus);
    }

    private void updateStatusReport(int netStatus){
        String text;
        int[] statusSet;
        switch (netStatus){
            case StaticData.EXTRA_INT_NET_STATUS_NORMAL:
                tv_statusReport.setTextColor(Color.GREEN);
                text="服务器正常。";
                statusSet=StaticData.STATE_SET_NORMAL;
                break;
            case StaticData.EXTRA_INT_NET_STATUS_UNSTABLE:
                tv_statusReport.setTextColor(mActivity.getResources().getColor(R.color.colorOrange));
                text="服务器连接不稳定...";
                statusSet=StaticData.STATE_SET_UNSTABLE;
                break;
            case StaticData.EXTRA_INT_NET_STATUS_ERROR:
                tv_statusReport.setTextColor(Color.RED);
                text="服务器数据停止更新了。";
                statusSet=StaticData.STATE_SET_NO_UPDATE;
                break;
            case StaticData.EXTRA_INT_NET_STATUS_INITIALIZING:
                tv_statusReport.setTextColor(Color.GRAY);
                text="初始化中...";
                statusSet=StaticData.STATE_SET_INITIALIZING;
                break;
            case StaticData.EXTRA_INT_NET_STATUS_TABLE_FAILED:
            case StaticData.EXTRA_INT_NET_STATUS_LINK_FAILED:
                tv_statusReport.setTextColor(Color.RED);
                text="服务器连接失败。";
                statusSet=StaticData.STATE_SET_LINK_FAIL;
                break;
            case StaticData.EXTRA_INT_NET_STATUS_MONITOR_CLOSE:
            default:
                tv_statusReport.setTextColor(Color.BLACK);
                text="监控已经停止。";
                statusSet=StaticData.STATE_SET_STOPPED;
                break;
        }
        tv_statusReport.setText(text);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP&&mAslDrawable!=null){
            iv_netStateIcon.setImageState(statusSet,true);
        }
    }

    private void feedbackTimeAnimation(StateTable stateTable){
        String time = mDateFormat.format(new Date(stateTable.getDateTime()));
        if(time.trim().equals(tv_updateTime.getText().toString().trim())){
            return;
        }
        final SpannableString sp=new SpannableString(time);
        final MutableSizeSpan sizeSpan = new MutableSizeSpan((int) mTextSize, false);
        sp.setSpan(sizeSpan,0,time.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        final MutableColorSpan colorSpan = new MutableColorSpan(Color.RED,255);
        sp.setSpan(colorSpan,0,time.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ValueAnimator valueAnimator=ValueAnimator.ofFloat(0,1f);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                colorSpan.setAlpha((int) (255*fraction));
                sizeSpan.setSize((int) (mTextSize+3*fraction));
                tv_updateTime.setText(sp);
            }
        });
        valueAnimator.start();
    }

    public void toConfigTableActivity(){
        UserBean user = BaseApplication.getUser();
        if(user!=null){
            showProgressDialog();
            showLog("target host id: "+mStateTable.getHostId());
            mMySqlModel.getConfigTable(user,mStateTable.getHostId());
        }else {
            mActivity.showToast("登陆信息已过期，请重新登陆。");
        }
    }

    private void showProgressDialog(){
        if(mProgressDialog==null){
            mProgressDialog = ProgressDialog.show(
                    mActivity,
                    "连接数据库",
                    "连接数据库中，请稍候......",
                    true,
                    false
            );
        }
    }

    private void dismissProgressDialog(){
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
            mProgressDialog=null;
        }
    }

    private void showAlertDialogCreateConfigTable() {
        AlertDialogUtil.showBaseDialog(
                mActivity,
                "新建配置表",
                "当前激励器还没有生成相应的配置表，您需要现在创建吗？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog();
                        ConfigTable configTable=new ConfigTable();
                        configTable.setHostId(mStateTable.getHostId());
                        configTable.setOpCode(StaticData.OP_CODE_REBOOT);
                        configTable.setSpecVer(mStateTable.getCurVer());
                        UserBean user = BaseApplication.getUser();
                        if(user!=null){
                            mMySqlModel.createNewConfigTable(user,configTable);
                        }else {
                            mActivity.showToast("登陆信息已过期，请重新登陆。");
                        }
                    }
                }
        );
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==StaticData.REQUEST_CODE_EDIT_CONFIG_TABLE){
            if(resultCode==StaticData.RESULT_CODE_EDIT_CONFIG_TABLE_OK){
                //9月28日 因增加修改版本号的功能，导致要增加以下代码
                ConfigTable configTable=data.getParcelableExtra(StaticData.EXTRA_DATA_CONFIG_TABLE);
                mActivity.getTv_version().setText(String.valueOf(configTable.getSpecVer()));
            }
        }
    }


}
