package com.skycaster.hellobase.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
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
    private NotificationCompat.Builder mBuilder;

    @Override
    public void onCreate() {
        super.onCreate();
        mModel=new MySqlModel(new MySqlModelCallBack(){
            @Override
            public void onGetSqlConnection(Connection con) {
                mConnection=con;
                startLooping();
            }

            @Override
            public void onSqlConnectionFail(String msg) {
                stopSelf();
            }


            @Override
            public void onGetStateTableSuccess(StateTable stateTable) {
                showLog(stateTable.toString());
                updateTableList(stateTable);
            }

            @Override
            public void onGetStateTableFail(String msg) {
                Intent intent=new Intent(StaticData.ACTION_SEVER_CON_STATUS_MONITOR);
                intent.putExtra(StaticData.EXTRA_INT_EVENT_TYPE,StaticData.EVENT_TYPE_NET_STATUS);
                intent.putExtra(StaticData.EXTRA_DATA_STATE_TABLE,mStateTable);
                intent.putExtra(StaticData.EXTRA_BOOLEAN_IS_SUCCESS,false);
                intent.putExtra(StaticData.EXTRA_STRING_FAIL_INFO,msg);
                sendBroadcast(intent);
            }
        });

        mReceiver=new Receiver();
        IntentFilter intentFilter=new IntentFilter(StaticData.ACTION_STOP_SERVICE);
        registerReceiver(mReceiver,intentFilter);

    }

    private void showLog(String msg) {
        Log.e(getClass().getSimpleName(),msg);
    }

    private synchronized void updateTableList(StateTable newTable) {
        int size = mStateTables.size();
        if(size >2){
            mStateTables.remove(0);
        }
        mStateTables.add(newTable);
        int outDateCount=0;
        for(int i = 1; i< size; i++){
            long dateTime = mStateTables.get(i).getDateTime();
            if(dateTime==mStateTables.get(i-1).getDateTime()){
                outDateCount++;
            }
        }
        if(size==3){
            switch (outDateCount){
                case StaticData.EXTRA_INT_NET_STATUS_NORMAL:
                    mRemoteViews.setTextViewText(R.id.remote_view_tv_signal,"良好");
                    mRemoteViews.setImageViewResource(R.id.remote_view_iv_signal,R.drawable.vd_ic_signal_excellent_24dp);
                    break;
                case StaticData.EXTRA_INT_NET_STATUS_UNSTABLE:
                    mRemoteViews.setTextViewText(R.id.remote_view_tv_signal,"不稳定");
                    mRemoteViews.setImageViewResource(R.id.remote_view_iv_signal,R.drawable.vd_ic_signal_unstable_24dp);
                    break;
                case StaticData.EXTRA_INT_NET_STATUS_ERROR:
                    mRemoteViews.setTextViewText(R.id.remote_view_tv_signal,"异常");
                    mRemoteViews.setImageViewResource(R.id.remote_view_iv_signal,R.drawable.vd_ic_signal_error_24dp);
                    break;
            }
        }else {
            mRemoteViews.setTextViewText(R.id.remote_view_tv_signal,"检测中...");
            mRemoteViews.setImageViewResource(R.id.remote_view_iv_signal,R.drawable.ic_signal_wifi_error_white_24dp);
            outDateCount=StaticData.EXTRA_INT_NET_STATUS_INITIALIZING;
        }
        updateRemoteViews();

        Intent intent=new Intent(StaticData.ACTION_SEVER_CON_STATUS_MONITOR);
        intent.putExtra(StaticData.EXTRA_INT_EVENT_TYPE,StaticData.EVENT_TYPE_NET_STATUS);
        intent.putExtra(StaticData.EXTRA_BOOLEAN_IS_SUCCESS,true);
        intent.putExtra(StaticData.EXTRA_DATA_STATE_TABLE,newTable);
        intent.putExtra(StaticData.EXTRA_INT_NET_STATUS_CODE,outDateCount);
        sendBroadcast(intent);

    }

    private void updateRemoteViews() {
        startForeground(123,mBuilder.setContent(mRemoteViews).build());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mStateTable = intent.getParcelableExtra(StaticData.EXTRA_DATA_STATE_TABLE);
        mStateTables.add(mStateTable);
        startForeground();
        mModel.connectMySql(StaticData.HOST_ADDRESS,StaticData.DATA_BASE_NAME,StaticData.USER_NAME,StaticData.PASSWORD);
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground(){
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentText("正在监控服务器连接状态......")
                .setSmallIcon(R.drawable.vd_network_check_24dp);
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.remote_view_net_status);
        Intent startActIntent = new Intent(this, ServerStateActivity.class);
        startActIntent.putExtra(StaticData.EXTRA_DATA_STATE_TABLE,mStateTable);
        startActIntent.putExtra(StaticData.EXTRA_BOOLEAN_IS_SERVICE_RUNNING,true);
        PendingIntent startActPi=PendingIntent.getActivity(
                this,
                987,
                startActIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.remote_view_root_view,startActPi);
        Intent stopSevIntent=new Intent(StaticData.ACTION_STOP_SERVICE);
        PendingIntent stopSevPi = PendingIntent.getBroadcast(
                this,
                357,
                stopSevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mRemoteViews.setOnClickPendingIntent(R.id.remote_view_btn_quit,stopSevPi);
        mBuilder.setContent(mRemoteViews);
        Notification notification = mBuilder.build();
        startForeground(123,notification);
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
                    mModel.requestStateTable(mConnection,mStateTable.getHostId());
                    SystemClock.sleep(5000);
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent=new Intent(StaticData.ACTION_SEVER_CON_STATUS_MONITOR);
        intent.putExtra(StaticData.EXTRA_INT_EVENT_TYPE,StaticData.EVENT_TYPE_SERVICE_DISMISS);
        sendBroadcast(intent);
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
            mReceiver=null;
        }
        isContinue.set(false);
        if(mConnection!=null){
            try {
                mConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        stopForeground(true);
    }

    private class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
        }
    }
}
