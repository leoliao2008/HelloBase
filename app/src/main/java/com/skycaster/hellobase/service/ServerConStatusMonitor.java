package com.skycaster.hellobase.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.activity.StateActivity;
import com.skycaster.hellobase.base.BaseApplication;
import com.skycaster.hellobase.bean.StateTable;
import com.skycaster.hellobase.bean.UserBean;
import com.skycaster.hellobase.data.StaticData;
import com.skycaster.hellobase.interf.MySqlModelCallBack;
import com.skycaster.hellobase.model.MySqlModel;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 廖华凯 on 2017/9/13.
 */

public class ServerConStatusMonitor extends Service {
    private MySqlModel mMySqlModel;
    private StateTable mStateTable;
    private Connection mConnection;
    private AtomicBoolean isContinue=new AtomicBoolean(false);
    private Receiver mReceiver;
    private RemoteViews mRemoteViews;
    private ArrayList<StateTable> mStateTables=new ArrayList<>();
    private NotificationCompat.Builder mNotiBuilder;
    private int mNetState =StaticData.EXTRA_INT_NET_STATUS_MONITOR_CLOSE;
    private Thread mLoopThread;
    private ArrayList<String> mTokens;
    private MySqlModelCallBack mCallback=new MySqlModelCallBack(){
        @Override
        public void onGetSqlConnection(Connection con) {
            mConnection=con;
//            startLooping();
            mMySqlModel.getUserToken(mConnection);
        }

        @Override
        public void onSqlConnectionFail(String msg) {
            mConnection=null;
            mNetState =StaticData.EXTRA_INT_NET_STATUS_LINK_FAILED;
            updateRemoteViewsAndBroadcast(mNetState,msg);
            stopSelf();
        }

        @Override
        public void onGetPermissionTokens(ArrayList<String> tokens) {
            mTokens=tokens;
            if(tokens.size()>0){
                startLooping();
            }
        }

        @Override
        public void onGetStateTablesSuccess(ArrayList<StateTable> stateTables) {
            showLog(stateTables.toString());
            if(!stateTables.isEmpty()){
                updateNetStateByStateTable(stateTables.get(0));
            }
        }


        @Override
        public void onGetStateTablesError(String msg) {
            mConnection=null;
            stopLooping();
            connectMySql();
        }
    };



    @Override
    public void onCreate() {
        super.onCreate();
        mMySqlModel =new MySqlModel(mCallback);
        mReceiver=new Receiver();
        IntentFilter intentFilter=new IntentFilter(StaticData.ACTION_STOP_SERVICE);
        registerReceiver(mReceiver,intentFilter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mStateTable = intent.getParcelableExtra(StaticData.EXTRA_DATA_STATE_TABLE);
        BaseApplication.setCurrentStateTable(mStateTable);
        showForegroundNotification();
        updateNetStateByStateTable(mStateTable);
        connectMySql();
        return super.onStartCommand(intent, flags, startId);
    }

    private void connectMySql(){
        UserBean user = BaseApplication.getUser();
        if(user!=null){
            mMySqlModel.connectMySql(user.getHost(),user.getUserName(),user.getPassword());
        }else {
            Toast.makeText(this,"登陆信息过期，请重新登陆。",Toast.LENGTH_SHORT).show();
        }
    }

    private void showLog(String msg) {
        Log.e(getClass().getSimpleName(),msg);
    }

    private synchronized void updateNetStateByStateTable(StateTable newTable) {
        int size = mStateTables.size();
        if(size >2){
            mStateTables.remove(0);
        }
        mStateTables.add(newTable.deepClone());

        if(mStateTables.size()==3){
            int outDateCount=0;
            for(int i = 1; i< size; i++){
                long dateTime = mStateTables.get(i).getDateTime();
                if(dateTime==mStateTables.get(i-1).getDateTime()){
                    outDateCount++;
                }
            }
            switch (outDateCount){
                case 0:
                    mNetState =StaticData.EXTRA_INT_NET_STATUS_NORMAL;
                    break;
                case 1:
                    mNetState =StaticData.EXTRA_INT_NET_STATUS_UNSTABLE;
                    break;
                case 2:
                    mNetState =StaticData.EXTRA_INT_NET_STATUS_ERROR;
                    break;
            }
        }else {
            mNetState =StaticData.EXTRA_INT_NET_STATUS_INITIALIZING;
        }
        mStateTable=newTable;
        updateRemoteViewsAndBroadcast(mNetState,null);

    }

    private void updateRemoteViewsAndBroadcast(int netState, @Nullable String msg) {
        String text;
        int src;
        int color;
        switch (netState){
            case StaticData.EXTRA_INT_NET_STATUS_INITIALIZING:
                text="初始化中";
                color=Color.WHITE;
                src=R.drawable.ic_signal_wifi_statusbar_not_connected_white_18dp;
                break;
            case StaticData.EXTRA_INT_NET_STATUS_NORMAL:
                text="更新正常";
                color= Color.GREEN;
                src=R.drawable.ic_signal_wifi_4_bar_white_18dp;
                break;
            case StaticData.EXTRA_INT_NET_STATUS_UNSTABLE:
                text="更新缓慢";
                color=Color.YELLOW;
                src=R.drawable.ic_signal_wifi_statusbar_2_bar_white_18dp;
                break;
            case StaticData.EXTRA_INT_NET_STATUS_ERROR:
                text="停止更新";
                color=Color.RED;
                src=R.drawable.ic_signal_wifi_statusbar_connected_no_internet_white_18dp;
                break;
            case StaticData.EXTRA_INT_NET_STATUS_TABLE_FAILED:
            case StaticData.EXTRA_INT_NET_STATUS_LINK_FAILED:
            default:
                text="链接中断";
                color=Color.RED;
                src=R.drawable.ic_signal_wifi_statusbar_connected_no_internet_white_18dp;
                break;
        }
        mRemoteViews.setTextViewText(R.id.remote_view_tv_signal,text);
        mRemoteViews.setTextColor(R.id.remote_view_tv_signal,color);
        mRemoteViews.setImageViewResource(R.id.remote_view_iv_signal,src);
        mRemoteViews.setOnClickPendingIntent(R.id.remote_view_root_view, initActPi(netState));
        startForeground(123, mNotiBuilder.setContent(mRemoteViews).build());

        mStateTable.setStateCode(netState);
        BaseApplication.setCurrentStateTable(mStateTable);

        Intent intent=new Intent(StaticData.ACTION_SEVER_CON_STATUS_MONITOR);
        intent.putExtra(StaticData.EXTRA_INT_EVENT_TYPE,StaticData.EVENT_TYPE_NET_STATUS);
        intent.putExtra(StaticData.EXTRA_BOOLEAN_IS_SUCCESS,netState!=StaticData.EXTRA_INT_NET_STATUS_LINK_FAILED&&netState!=StaticData.EXTRA_INT_NET_STATUS_TABLE_FAILED);
        intent.putExtra(StaticData.EXTRA_DATA_STATE_TABLE,mStateTable);
        intent.putExtra(StaticData.EXTRA_INT_NET_STATUS_CODE,netState);
        if(!TextUtils.isEmpty(msg)){
            intent.putExtra(StaticData.EXTRA_STRING_INFO,msg);
        }
        sendBroadcast(intent);
    }

    private void showForegroundNotification(){
        mNotiBuilder = new NotificationCompat.Builder(this);
        mNotiBuilder.setContentText("正在监控服务器连接状态......");
        mNotiBuilder.setSmallIcon(R.drawable.ic_signal_wifi_4_bar_white_18dp);
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.remote_view_net_status);
        mRemoteViews.setOnClickPendingIntent(R.id.remote_view_root_view, initActPi(mNetState));
        Intent stopSevIntent=new Intent(StaticData.ACTION_STOP_SERVICE);
        PendingIntent stopSevPi = PendingIntent.getBroadcast(
                this,
                357,
                stopSevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mRemoteViews.setOnClickPendingIntent(R.id.remote_view_tv_quit,stopSevPi);
        mNotiBuilder.setContent(mRemoteViews);
        Notification notification = mNotiBuilder.build();
        startForeground(123,notification);
    }

    private PendingIntent initActPi(int netStatus) {
        Intent startActIntent = new Intent(this, StateActivity.class);
        startActIntent.putExtra(StaticData.EXTRA_DATA_STATE_TABLE,mStateTable);
        startActIntent.putExtra(StaticData.EXTRA_BOOLEAN_IS_SERVICE_RUNNING,true);
        startActIntent.putExtra(StaticData.EXTRA_INT_NET_STATUS_CODE, netStatus);
        return PendingIntent.getActivity(this, 987, startActIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startLooping() {
        isContinue.set(true);
        mLoopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isContinue.get()) {
                    if(Thread.currentThread().isInterrupted()){
                        break;
                    }
                    mMySqlModel.getStateTables(mConnection, mTokens,mStateTable.getHostId());
                    SystemClock.sleep(10000);
                }
            }
        });
        mLoopThread.start();
    }

    private void stopLooping() {
        isContinue.set(false);
        if(mLoopThread!=null){
            mLoopThread.interrupt();
            mLoopThread=null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showLog("onDestroy");
        BaseApplication.setCurrentStateTable(null);

        Intent intent=new Intent(StaticData.ACTION_SEVER_CON_STATUS_MONITOR);
        intent.putExtra(StaticData.EXTRA_INT_EVENT_TYPE,StaticData.EVENT_TYPE_SERVICE_DISMISS);
        intent.putExtra(StaticData.EXTRA_INT_NET_STATUS_CODE,StaticData.EXTRA_INT_NET_STATUS_MONITOR_CLOSE);
        sendBroadcast(intent);

        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
            mReceiver=null;
        }

        stopLooping();

        //在连接未成功的情况下退出时，不要清除通知栏，让用户知道连接失败了。
        stopForeground(mConnection!=null);
        if(mConnection!=null){
            try {
                mConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            mConnection=null;
        }
    }

    private class Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
        }
    }
}
