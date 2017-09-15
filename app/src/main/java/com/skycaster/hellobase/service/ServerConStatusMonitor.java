package com.skycaster.hellobase.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.activity.ServerStateActivity;
import com.skycaster.hellobase.bean.StateTable;
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
    private MySqlModel mModel;
    private StateTable mStateTable;
    private Connection mConnection;
    private AtomicBoolean isContinue=new AtomicBoolean(false);
    private Receiver mReceiver;
    private RemoteViews mRemoteViews;
    private ArrayList<StateTable> mStateTables=new ArrayList<>();
    private NotificationCompat.Builder mNotiBuilder;
    private int mNetState =StaticData.EXTRA_INT_NET_STATUS_MONITOR_CLOSE;
    private MySqlModelCallBack mCallback=new MySqlModelCallBack(){
        @Override
        public void onGetSqlConnection(Connection con) {
            mConnection=con;
            startLooping();
        }

        @Override
        public void onSqlConnectionFail(String msg) {
            mConnection=null;
            mNetState =StaticData.EXTRA_INT_NET_STATUS_LINK_FAILED;
            updateRemoteViewsAndBroadcast(mNetState,msg);
            stopSelf();
        }


        @Override
        public void onGetStateTableSuccess(StateTable stateTable) {
            showLog(stateTable.toString());
            updateNetStateByStateTable(stateTable);
        }

        @Override
        public void onGetStateTableFail(String msg) {
            mNetState =StaticData.EXTRA_INT_NET_STATUS_TABLE_FAILED;
            updateRemoteViewsAndBroadcast(mNetState,msg);
        }
    };
    private NotificationManagerCompat mNotiManager;
    private NotificationCompat.Builder mHeadUpNotiBuilder;

    @Override
    public void onCreate() {
        super.onCreate();
        mModel=new MySqlModel(mCallback);
        mNotiManager=NotificationManagerCompat.from(this);

        mHeadUpNotiBuilder = new NotificationCompat.Builder(this);
        mHeadUpNotiBuilder.setSmallIcon(R.drawable.ic_signal_wifi_alert_white_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_signal_wifi_alert_white_48dp))
                .setContentTitle("连接异常！");

        mReceiver=new Receiver();
        IntentFilter intentFilter=new IntentFilter(StaticData.ACTION_STOP_SERVICE);
        registerReceiver(mReceiver,intentFilter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mStateTable = intent.getParcelableExtra(StaticData.EXTRA_DATA_STATE_TABLE);
        mStateTables.add(mStateTable);
        startForeground();
        updateNetStateByStateTable(mStateTable);
        mModel.connectMySql(StaticData.HOST_ADDRESS,StaticData.DATA_BASE_NAME,StaticData.USER_NAME,StaticData.PASSWORD);
        return super.onStartCommand(intent, flags, startId);
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
        int outDateCount=0;
        for(int i = 1; i< size; i++){
            long dateTime = mStateTables.get(i).getDateTime();
            if(dateTime==mStateTables.get(i-1).getDateTime()){
                outDateCount++;
            }
        }
        if(mStateTables.size()==3){
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
                src=R.drawable.ic_signal_wifi_initializing_white_24dp;
                break;
            case StaticData.EXTRA_INT_NET_STATUS_NORMAL:
                text="数据正常更新";
                color= Color.GREEN;
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                    src=R.drawable.vd_ic_signal_excellent_24dp;
                }else {
                    src=R.drawable.ic_signal_wifi_full_white_24dp;
                }
                break;
            case StaticData.EXTRA_INT_NET_STATUS_UNSTABLE:
                text="数据更新缓慢";
                color=Color.YELLOW;
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                    src=R.drawable.vd_ic_signal_unstable_24dp;
                }else {
                    src=R.drawable.ic_signal_wifi_half_white_24dp;
                }
                break;
            case StaticData.EXTRA_INT_NET_STATUS_ERROR:
                text="数据停止更新";
                color=Color.RED;
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                    src=R.drawable.vd_ic_signal_error_24dp;
                }else {
                    src=R.drawable.ic_signal_wifi_alert_white_24dp;
                }
                break;
            case StaticData.EXTRA_INT_NET_STATUS_TABLE_FAILED:
            case StaticData.EXTRA_INT_NET_STATUS_LINK_FAILED:
            default:
                text="联不上服务器";
                color=Color.RED;
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                    src=R.drawable.vd_ic_signal_error_24dp;
                }else {
                    src=R.drawable.ic_signal_wifi_alert_white_24dp;
                }
                break;
        }
        mRemoteViews.setTextViewText(R.id.remote_view_tv_signal,text);
        mRemoteViews.setTextColor(R.id.remote_view_tv_signal,color);
        mRemoteViews.setImageViewResource(R.id.remote_view_iv_signal,src);
        mRemoteViews.setOnClickPendingIntent(R.id.remote_view_root_view,getActPendingIntent(netState));
        startForeground(123, mNotiBuilder.setContent(mRemoteViews).build());

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

    private void headUpNotification(String msg){
        mHeadUpNotiBuilder.setContentText(msg);
        Intent startActIntent = new Intent(this, ServerStateActivity.class);
        startActIntent.putExtra(StaticData.EXTRA_DATA_STATE_TABLE,mStateTable);
        startActIntent.putExtra(StaticData.EXTRA_BOOLEAN_IS_SERVICE_RUNNING,true);
        startActIntent.putExtra(StaticData.EXTRA_INT_NET_STATUS_CODE, mNetState);
        PendingIntent pi=PendingIntent.getActivity(this,753,startActIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mHeadUpNotiBuilder.setFullScreenIntent(pi,true);
        mHeadUpNotiBuilder.setAutoCancel(true);
        mNotiManager.notify(963, mHeadUpNotiBuilder.build());
    }

    private void startForeground(){
        mNotiBuilder = new NotificationCompat.Builder(this);
        mNotiBuilder.setContentText("正在监控服务器连接状态......");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            mNotiBuilder.setSmallIcon(R.drawable.vd_network_check_24dp);
        }else {
            mNotiBuilder.setSmallIcon(R.drawable.ic_signal_wifi_full_white_24dp);
        }
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.remote_view_net_status);
        mRemoteViews.setOnClickPendingIntent(R.id.remote_view_root_view,getActPendingIntent(mNetState));
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

    private PendingIntent getActPendingIntent(int netStatus) {
        Intent startActIntent = new Intent(this, ServerStateActivity.class);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isContinue.get()){
                    try {
                        mModel.requestStateTable(mConnection,mStateTable.getHostId());
                        SystemClock.sleep(5000);
                    }catch (Exception e){
                        try {
                            mConnection.close();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                        mConnection=null;
                        mCallback.onSqlConnectionFail(e.getMessage());
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent=new Intent(StaticData.ACTION_SEVER_CON_STATUS_MONITOR);
        intent.putExtra(StaticData.EXTRA_INT_EVENT_TYPE,StaticData.EVENT_TYPE_SERVICE_DISMISS);
        intent.putExtra(StaticData.EXTRA_INT_NET_STATUS_CODE,StaticData.EXTRA_INT_NET_STATUS_MONITOR_CLOSE);
        sendBroadcast(intent);
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
            mReceiver=null;
        }
        isContinue.set(false);
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
