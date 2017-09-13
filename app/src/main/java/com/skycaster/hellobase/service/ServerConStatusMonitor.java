package com.skycaster.hellobase.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.bean.StateTable;
import com.skycaster.hellobase.data.StaticData;
import com.skycaster.hellobase.interf.MySqlModelCallBack;
import com.skycaster.hellobase.model.MySqlModel;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 廖华凯 on 2017/9/13.
 */

public class ServerConStatusMonitor extends Service {
    private MySqlModel mModel;
    private StateTable mStateTable;
    private Connection mConnection;
    private AtomicBoolean isContinue=new AtomicBoolean(false);

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
                mStateTable=stateTable;
                Intent intent=new Intent(StaticData.ACTION_STATE_TABLE_GET);
                intent.putExtra(StaticData.EXTRA_DATA_STATE_TABLE,mStateTable);
                intent.putExtra(StaticData.EXTRA_BOOLEAN_IS_SUCCESS,true);
                sendBroadcast(intent);
            }

            @Override
            public void onGetStateTableFail(String msg) {
                Intent intent=new Intent(StaticData.ACTION_STATE_TABLE_GET);
                intent.putExtra(StaticData.EXTRA_DATA_STATE_TABLE,mStateTable);
                intent.putExtra(StaticData.EXTRA_BOOLEAN_IS_SUCCESS,false);
                intent.putExtra(StaticData.EXTRA_STRING_FAIL_INFO,msg);
                sendBroadcast(intent);
            }
        });
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setContentText("正在监控服务器连接状态......")
                .setSmallIcon(R.drawable.vd_network_check_24dp);
//        new RemoteViews(getPackageName(),R.layout.)
//        builder.setContent();
        Notification notification = builder.build();
        startForeground(123,notification);
        mStateTable = intent.getParcelableExtra(StaticData.EXTRA_DATA_STATE_TABLE);
        mModel.connectMySql(StaticData.HOST_ADDRESS,StaticData.DATA_BASE_NAME,StaticData.USER_NAME,StaticData.PASSWORD);
        return super.onStartCommand(intent, flags, startId);
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
                stopSelf();
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
