package com.skycaster.hellobase.presenter;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.skycaster.hellobase.activity.MainActivity;
import com.skycaster.hellobase.activity.ServerStateActivity;
import com.skycaster.hellobase.adapter.ServiceBaseAdapter;
import com.skycaster.hellobase.base.BaseApplication;
import com.skycaster.hellobase.bean.ConfigTable;
import com.skycaster.hellobase.bean.ServiceBase;
import com.skycaster.hellobase.bean.StateTable;
import com.skycaster.hellobase.customize.MaxHeightListView;
import com.skycaster.hellobase.data.StaticData;
import com.skycaster.hellobase.interf.MySqlModelCallBack;
import com.skycaster.hellobase.model.MySqlModel;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/9/12.
 */

public class MainActivityPresenter {
    private MainActivity mActivity;
    private MySqlModel mMySqlModel;
    private ProgressDialog mProgressDialog;
    private MaxHeightListView mListView;
    private ArrayList<ServiceBase> list=new ArrayList<>();
    private ServiceBaseAdapter mAdapter;
    private AppCompatSpinner mSpinner;
    private ArrayList<ConfigTable> mConfigTables=new ArrayList<>();
    private ArrayAdapter<ConfigTable> mSpinnerAdapter;
    private DisplayMetrics mDisplayMetrics;


    public MainActivityPresenter(MainActivity activity) {
        mActivity = activity;
        mMySqlModel=new MySqlModel(initMySqlCallBack());
    }

    private MySqlModelCallBack initMySqlCallBack() {
        return new MySqlModelCallBack(){
            @Override
            public void onGetSqlConnection(Connection con) {
                BaseApplication.setConnection(con);
                mMySqlModel.requestConfigTable(BaseApplication.getConnection());
            }

            @Override
            public void onSqlConnectionFail(final String msg) {
                if(mProgressDialog!=null){
                    mProgressDialog.dismiss();
                }
                showToast(msg);

            }

            @Override
            public void onGetConfigTableSuccess(final ArrayList<ConfigTable> tables) {
                if(mProgressDialog!=null){
                    mProgressDialog.dismiss();
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mConfigTables.clear();
                        if(tables!=null){
                            mConfigTables.addAll(tables);
                            for (ConfigTable tb:mConfigTables){
                                showLog(tb.toString());
                            }
                        }
                        mSpinnerAdapter.notifyDataSetChanged();
                        if(mConfigTables.size()>0){
                            updateActivityUi(mConfigTables.get(0));
                        }
                    }
                });

            }

            @Override
            public void onGetConfigTableFail(final String msg) {
                if(mProgressDialog!=null){
                    mProgressDialog.dismiss();
                }
                showToast(msg);
            }

            @Override
            public void onGetStateTableSuccess(final StateTable stateTable) {
                if(mProgressDialog!=null){
                    mProgressDialog.dismiss();
                }
                showLog(stateTable.toString());
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ServerStateActivity.start(mActivity,stateTable);
                    }
                });
            }

            @Override
            public void onGetStateTableFail(final String msg) {
                if(mProgressDialog!=null){
                    mProgressDialog.dismiss();
                }
                showToast(msg);
            }
        };
    }

    private void showLog(String msg) {
        Log.e(getClass().getSimpleName(),msg);
    }

    public void initData(){
        mDisplayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        mSpinner=mActivity.getSpinner();
        mSpinnerAdapter=new ArrayAdapter<ConfigTable>(
                mActivity,
                android.R.layout.simple_spinner_item,
                mConfigTables
        ){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view= (TextView) super.getView(position, convertView, parent);
                view.setText(mConfigTables.get(position).getHostId());
                view.setGravity(Gravity.CENTER_HORIZONTAL);
                view.setTextSize(18);
                view.setPadding(5,5,5,5);
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return getView(position,convertView,parent);
            }
        };
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateActivityUi(mConfigTables.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mListView=mActivity.getListView();
        mAdapter=new ServiceBaseAdapter(list,mActivity);
        mListView.setAdapter(mAdapter);

        mActivity.getScrollView().postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.getScrollView().fullScroll(View.FOCUS_UP);
            }
        },500);
    }




    public void connectMySql(){
        mProgressDialog = ProgressDialog.show(
                mActivity,
                "连接数据库",
                "连接数据库中，请稍候......",
                true,
                false
        );
        mMySqlModel.connectMySql(StaticData.HOST_ADDRESS,StaticData.DATA_BASE_NAME,StaticData.USER_NAME,StaticData.PASSWORD);
    }

    public void goToStateTable(){
        mProgressDialog = ProgressDialog.show(
                mActivity,
                "连接数据库",
                "连接数据库中，请稍候......",
                true,
                false
        );
        try {
            if(BaseApplication.getConnection()!=null
                    &&!BaseApplication.getConnection().isClosed()
                    &&mConfigTables.size()>0){
                mMySqlModel.requestStateTable(BaseApplication.getConnection(),((ConfigTable)mSpinner.getSelectedItem()).getHostId());
            }
        } catch (SQLException e) {
            showToast(e.getMessage());
        }
    }

    private void updateActivityUi(ConfigTable table) {
        mActivity.getTv_hostAddr().setText(table.getHostId());
        mActivity.getTv_hostVersion().setText(String.valueOf(table.getSpecVer()));
        mActivity.getTv_vendor().setText(table.getTheOwner());
        mActivity.getTv_opCode().setText(table.getOpCode());
        mActivity.getTv_freq().setText(String.valueOf(table.getCenterFreq()));
        mActivity.getTv_amp().setText(String.valueOf(table.getSignalAmp()));
        mActivity.getTv_fill().setText(String.valueOf(table.getSignFill()));
        mActivity.getTv_leftTune().setText(String.valueOf(table.getToneLeft()));
        mActivity.getTv_rightTune().setText(String.valueOf(table.getToneRight()));
        ArrayList<ServiceBase> bases = table.getServiceBases();
        list.clear();
        if(bases!=null){
            list.addAll(bases);
        }
        mAdapter.notifyDataSetChanged();
        mListView.smoothScrollToPosition(Integer.MAX_VALUE);
    }

    public void onStop(){
        if(mActivity.isFinishing()){
            if(BaseApplication.getConnection()!=null){
                try {
                    BaseApplication.getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                BaseApplication.setConnection(null);
            }
        }
    }

    private void showToast(final String msg){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
