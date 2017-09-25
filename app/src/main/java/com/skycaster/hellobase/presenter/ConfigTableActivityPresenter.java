package com.skycaster.hellobase.presenter;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.skycaster.hellobase.activity.ConfigTableActivity;
import com.skycaster.hellobase.adapter.ServiceBaseAdapter;
import com.skycaster.hellobase.bean.BaseServer;
import com.skycaster.hellobase.bean.ConfigTable;
import com.skycaster.hellobase.customize.MaxHeightListView;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/9/12.
 */

public class ConfigTableActivityPresenter {
    private ConfigTableActivity mActivity;
//    private MySqlModel mMySqlModel;
    private ProgressDialog mProgressDialog;
    private MaxHeightListView mListView;
    private ArrayList<BaseServer> list=new ArrayList<>();
    private ServiceBaseAdapter mAdapter;
    private ConfigTable mConfigTable;
//    private AppCompatSpinner mSpinner;
//    private ArrayList<ConfigTable> mConfigTables=new ArrayList<>();
//    private ArrayAdapter<ConfigTable> mSpinnerAdapter;


    public ConfigTableActivityPresenter(ConfigTableActivity activity) {
        mActivity = activity;
//        mMySqlModel=new MySqlModel(initMySqlCallBack());
    }

//    private MySqlModelCallBack initMySqlCallBack() {
//        return new MySqlModelCallBack(){
//            @Override
//            public void onGetSqlConnection(Connection con) {
////                BaseApplication.setConnection(con);
////                mMySqlModel.requestConfigTable(BaseApplication.getConnection());
//            }
//
//            @Override
//            public void onSqlConnectionFail(final String msg) {
////                if(mProgressDialog!=null){
////                    mProgressDialog.dismiss();
////                }
////                showToast(msg);
//
//            }
//
//            @Override
//            public void onGetConfigTableSuccess(final ArrayList<ConfigTable> tables) {
////                if(mProgressDialog!=null){
////                    mProgressDialog.dismiss();
////                }
////                mActivity.runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        mConfigTables.clear();
////                        if(tables!=null){
////                            mConfigTables.addAll(tables);
////                            for (ConfigTable tb:mConfigTables){
////                                showLog(tb.toString());
////                            }
////                        }
////                        mSpinnerAdapter.notifyDataSetChanged();
////                        if(mConfigTables.size()>0){
////                            updateActivityUi(mConfigTables.get(0));
////                        }else {
////                            showToast("当前基地器数量为0。");
////                        }
////                    }
////                });
//
//            }
//
//            @Override
//            public void onGetConfigTableFail(final String msg) {
////                if(mProgressDialog!=null){
////                    mProgressDialog.dismiss();
////                }
////                showToast(msg);
//            }
//
//            @Override
//            public void onGetStateTablesSuccess(final ArrayList<StateTable> stateTables) {
////                if(mProgressDialog!=null){
////                    mProgressDialog.dismiss();
////                }
////                showLog(stateTables.toString());
////                if(stateTables.isEmpty()){
////                    mActivity.showToast("无法找到对应的状态表。");
////                }else {
////                    mActivity.runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            StateTableActivity.start(mActivity,stateTables.get(0));
////                        }
////                    });
////                }
//            }
//
//            @Override
//            public void onGetStateTablesFail(final String msg) {
////                if(mProgressDialog!=null){
////                    mProgressDialog.dismiss();
////                }
////                showToast(msg);
//            }
//        };
//    }

    private void showLog(String msg) {
        Log.e(getClass().getSimpleName(),msg);
    }

    public void initData(){
//        mDisplayMetrics = new DisplayMetrics();
//        mActivity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
//        mSpinner=mActivity.getSpinner();
//        mSpinnerAdapter=new ArrayAdapter<ConfigTable>(
//                mActivity,
//                android.R.layout.simple_spinner_item,
//                mConfigTables
//        ){
//            @NonNull
//            @Override
//            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                TextView view= (TextView) super.getView(position, convertView, parent);
//                view.setText(mConfigTables.get(position).getHostId());
//                view.setGravity(Gravity.CENTER_HORIZONTAL);
//                view.setTextSize(18);
//                view.setPadding(5,5,5,5);
//                return view;
//            }
//
//            @Override
//            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                return getView(position,convertView,parent);
//            }
//        };
//        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinner.setAdapter(mSpinnerAdapter);
//        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                updateActivityUi(mConfigTables.get(position));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        mListView=mActivity.getListView();
        mAdapter=new ServiceBaseAdapter(list,mActivity);
        mListView.setAdapter(mAdapter);

        mActivity.getScrollView().postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.getScrollView().fullScroll(View.FOCUS_UP);
            }
        },500);

        mConfigTable=mActivity.getIntent().getParcelableExtra(ConfigTableActivity.EXTRA_DATA_CONFIG_TABLE);
        updateActivityUi(mConfigTable);
    }




//    public void connectMySql(){
//        mProgressDialog = ProgressDialog.show(
//                mActivity,
//                "连接数据库",
//                "连接数据库中，请稍候......",
//                true,
//                false
//        );
//        mMySqlModel.connectMySql(BaseApplication.getIpAddress(),StaticData.DATA_BASE_NAME,BaseApplication.getUserName(),BaseApplication.getPassword());
//    }

//    public void goToStateTable(){
//        mProgressDialog = ProgressDialog.show(
//                mActivity,
//                "连接数据库",
//                "连接数据库中，请稍候......",
//                true,
//                false
//        );
//        try {
//            if(BaseApplication.getConnection()!=null
//                    &&!BaseApplication.getConnection().isClosed()
//                    &&mConfigTables.size()>0){
//                mMySqlModel.requestStateTables(BaseApplication.getConnection(),((ConfigTable)mSpinner.getSelectedItem()).getHostId());
//            }else {
//                showToast("请先选择基地器。");
//                mProgressDialog.dismiss();
//            }
//        } catch (SQLException e) {
//            showToast(e.getMessage());
//            mProgressDialog.dismiss();
//        }
//    }

    private void updateActivityUi(ConfigTable table) {
        mActivity.getTv_hostAddr().setText(table.getHostId());
        mActivity.getTv_hostVersion().setText(String.valueOf(table.getSpecVer()));
        mActivity.getTv_vendor().setText(table.getTheOwner());
        mActivity.getTv_opCode().setText(table.getOpCode());
        mActivity.getTv_freq().setText(String.valueOf(table.getCenterFreq())+"MHz");
        mActivity.getTv_amp().setText(String.valueOf(table.getSignalAmp()));
        mActivity.getTv_fill().setText(String.valueOf(table.getSignFill()));
        mActivity.getTv_leftTune().setText(String.valueOf(table.getToneLeft()));
        mActivity.getTv_rightTune().setText(String.valueOf(table.getToneRight()));
        ArrayList<BaseServer> bases = table.getServiceBases();
        list.clear();
        if(bases!=null){
            list.addAll(bases);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void onStop(){
//        if(mActivity.isFinishing()){
//            if(BaseApplication.getConnection()!=null){
//                try {
//                    BaseApplication.getConnection().close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//                BaseApplication.setConnection(null);
//            }
//        }
    }

    private void showToast(final String msg){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editConfigSetting() {
        //// TODO: 2017/9/25  
    }
}
