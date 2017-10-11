package com.skycaster.hellobase.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.activity.LogInActivity;
import com.skycaster.hellobase.activity.NewStateTableListActivity;
import com.skycaster.hellobase.base.BaseApplication;
import com.skycaster.hellobase.bean.StateTable;
import com.skycaster.hellobase.bean.UserBean;
import com.skycaster.hellobase.data.StaticData;
import com.skycaster.hellobase.interf.MySqlModelCallBack;
import com.skycaster.hellobase.model.MySqlModel;
import com.skycaster.hellobase.model.NetworkStateModel;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/9/25.
 */

public class LogInPresenter {
    private LogInActivity mActivity;
    private SharedPreferences mSp;
    private String mIp;
    private String mPort;
    private String mUserName;
    private String mPw;
    private boolean mIsKeepInput;
    private boolean mIsPwVisible;
    private ProgressDialog mProgressDialog;
    private MySqlModel mMySqlModel;
    private MySqlModelCallBack mCallBack=new MySqlModelCallBack(){
        @Override
        public void onGetSqlConnection(Connection con) {
            UserBean user=new UserBean();
            user.setUserName(mUserName);
            user.setPassword(mPw);
            user.setHost(mIp+":"+mPort);
            user.setDataBaseName(StaticData.DATA_BASE_NAME);
            BaseApplication.setUser(user);
            mMySqlModel.getStateTables(con,null);
        }

        @Override
        public void onSqlConnectionFail(String msg) {
            dismissProgressDialog();
            showToast("连接失败，请确保IP地址、端口地址、用户名及密码都正确；如果仍然失败，请联系思凯微电子有限公司。");
        }

        @Override
        public void onGetStateTablesSuccess(final ArrayList<StateTable> stateTables) {
            dismissProgressDialog();
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NewStateTableListActivity.start(mActivity,stateTables);
                    mActivity.finish();
                }
            });

        }

        @Override
        public void onGetStateTablesError(String msg) {
            dismissProgressDialog();
            showToast(msg);
        }
    };
    private NetworkStateModel mNetworkStateModel;
    private AlertDialog mAlertDialog;

    public LogInPresenter(LogInActivity activity) {
        mActivity = activity;
        mSp =mActivity.getSharedPreferences(StaticData.SP_NAME, Context.MODE_PRIVATE);
        mMySqlModel=new MySqlModel(mCallBack);
        mNetworkStateModel=new NetworkStateModel(mActivity);
    }

    public void initData(){
       initUI();
    }

    private void initUI(){
        mIsPwVisible=mSp.getBoolean(StaticData.SP_IS_SHOW_PW,false);
        if(mIsPwVisible){
            mActivity.getIv_pwVisibility().setImageResource(R.drawable.ic_visibility_grey600_24dp);
            mActivity.getEdt_pw().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else {
            mActivity.getIv_pwVisibility().setImageResource(R.drawable.ic_visibility_off_grey600_24dp);
            mActivity.getEdt_pw().setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        mIsKeepInput=mSp.getBoolean(StaticData.SP_IS_KEEP_INPUT,false);
        mActivity.getCbx_keepInput().setChecked(mIsKeepInput);
        if(mIsKeepInput){
            mIp =mSp.getString(StaticData.SP_IP,null);
            if(!TextUtils.isEmpty(mIp)){
                mActivity.getEdt_ip().setText(mIp);
                mActivity.getEdt_ip().setSelection(mIp.length());
            }
            mPort=mSp.getString(StaticData.SP_PORT,null);
            if(!TextUtils.isEmpty(mPort)){
                mActivity.getEdt_port().setText(mPort);
                mActivity.getEdt_port().setSelection(mPort.length());
            }
            mUserName=mSp.getString(StaticData.SP_USER_NAME,null);
            if(!TextUtils.isEmpty(mUserName)){
                mActivity.getEdt_uerName().setText(mUserName);
                mActivity.getEdt_uerName().setSelection(mUserName.length());
            }

            mPw=mSp.getString(StaticData.SP_PW,null);
            if(!TextUtils.isEmpty(mPw)){
                mActivity.getEdt_pw().setText(mPw);
                mActivity.getEdt_pw().setSelection(mPw.length());
            }
        }

    }

    public void togglePwVisibility(){
        mIsPwVisible=!mIsPwVisible;
        if(mIsPwVisible){
            mActivity.getIv_pwVisibility().setImageResource(R.drawable.ic_visibility_grey600_24dp);
            mActivity.getEdt_pw().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else {
            mActivity.getIv_pwVisibility().setImageResource(R.drawable.ic_visibility_off_grey600_24dp);
            mActivity.getEdt_pw().setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        if(!TextUtils.isEmpty(mPw)){
            mActivity.getEdt_pw().setSelection(mPw.length());
        }
        mSp.edit().putBoolean(StaticData.SP_IS_SHOW_PW,mIsPwVisible).apply();
    }

    public void toggleIsSaveInput(){
        mIsKeepInput=!mIsKeepInput;
        mSp.edit().putBoolean(StaticData.SP_IS_KEEP_INPUT,mIsKeepInput).apply();
    }

    public void login(){
        if(!mNetworkStateModel.isNetworkAvailable()){
            showToast("请先连接网络。");
            return;
        }
        mIp=mActivity.getEdt_ip().getText().toString().trim();
        if(TextUtils.isEmpty(mIp)){
            showToast("IP地址不能为空。");
            return;
        }
        mPort= mActivity.getEdt_port().getText().toString().trim();
        if(TextUtils.isEmpty(mPort)){
            showToast("端口号不能为空。");
            return;
        }
        mUserName=mActivity.getEdt_uerName().getText().toString().trim();
        if(TextUtils.isEmpty(mUserName)){
            showToast("用户名不能为空。");
            return;
        }
        mPw= mActivity.getEdt_pw().getText().toString().trim();
        if(TextUtils.isEmpty(mPw)){
            showToast("请输入密码。");
            return;
        }
        if(mIsKeepInput){
            SharedPreferences.Editor editor = mSp.edit();
            editor.putString(StaticData.SP_IP,mIp);
            editor.putString(StaticData.SP_PORT,mPort);
            editor.putString(StaticData.SP_USER_NAME,mUserName);
            editor.putString(StaticData.SP_PW,mPw);
            editor.apply();
        }
        showProgressDialog();
        mMySqlModel.connectMySql(mIp+":"+mPort,StaticData.DATA_BASE_NAME,mUserName,mPw);
    }

    private void showProgressDialog() {
        if(mProgressDialog==null){
            mProgressDialog = ProgressDialog.show(
                    mActivity,
                    "登陆中",
                    "正在登陆服务器，请稍候······",
                    true,
                    true,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            showToast("您取消了登陆。");
                        }
                    }
            );
        }

    }

    private void dismissProgressDialog(){
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
            mProgressDialog=null;
        }
    }

    private void wipeLoginRecord(){
        SharedPreferences.Editor editor = mSp.edit();
        editor.remove(StaticData.SP_IP);
        editor.remove(StaticData.SP_PORT);
        editor.remove(StaticData.SP_USER_NAME);
        editor.remove(StaticData.SP_PW);
        editor.apply();
    }

    public void showWarningWipeLoginRecord(){
        AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
        builder.setTitle("温馨提示")
                .setMessage("您确定要清除用户登录信息吗？清除后下次需要重新输入IP地址、端口号、用户名称及密码。")
                .setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wipeLoginRecord();
                        showToast("已清除本地登陆记录！");
                        mAlertDialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                    }
                });
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void showToast(String msg){
        mActivity.showToast(msg);
    }

    public void onStop(){
        if(mActivity.isFinishing()){
            if(!mIsKeepInput){
               wipeLoginRecord();
            }
        }
    }
}
