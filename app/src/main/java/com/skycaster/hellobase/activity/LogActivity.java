package com.skycaster.hellobase.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.adapter.LogListAdapter;
import com.skycaster.hellobase.base.BaseActivity;
import com.skycaster.hellobase.base.BaseApplication;
import com.skycaster.hellobase.bean.Log;
import com.skycaster.hellobase.bean.UserBean;
import com.skycaster.hellobase.data.StaticData;
import com.skycaster.hellobase.interf.MySqlModelCallBack;
import com.skycaster.hellobase.model.MySqlModel;
import com.skycaster.hellobase.utils.AlertDialogUtil;

import java.util.ArrayList;
import java.util.Date;

public class LogActivity extends BaseActivity {

    private ArrayList<Log> mShowLogs =new ArrayList<>();
    private ArrayList<Log> mLogsOrigin =new ArrayList<>();
    private RecyclerView mRecyclerView;
    private MySqlModel mMySqlModel;
    private String mHostId;
    private TextView tv_noData;
    private LogListAdapter mAdapter;
    private Date mDateFrom;
    private Date mDateTo;
    private boolean isMaxToMinOrder=true;

    public static void start(Context context,String hostId) {
        Intent starter = new Intent(context, LogActivity.class);
        starter.putExtra(StaticData.EXTRA_STRING_HOST_ID,hostId);
        context.startActivity(starter);
    }

    @Override
    protected int getRootViewLayoutId() {
        return R.layout.activity_log;
    }

    @Override
    protected void initViews() {
        mRecyclerView= (RecyclerView) findViewById(R.id.activity_log_recycler_view);
        tv_noData= (TextView) findViewById(R.id.activity_log_tv_no_data);
    }

    @Override
    protected void initData() {
        mHostId=getIntent().getStringExtra(StaticData.EXTRA_STRING_HOST_ID);
        mMySqlModel=new MySqlModel(new MySqlModelCallBack(){
            @Override
            public void onSqlConnectionFail(String msg) {
                super.onSqlConnectionFail(msg);
                AlertDialogUtil.dismissProgressDialog();
                showToast(msg);
            }

            @Override
            public void onObtainLog(final ArrayList<Log> list) {
                super.onObtainLog(list);
                AlertDialogUtil.dismissProgressDialog();
                mLogsOrigin.clear();
                mLogsOrigin.addAll(list);
                if(mDateFrom!=null&&mDateTo!=null){
                    sortLogListByDate(mDateFrom,mDateTo);
                }else {
                    refreshRecyclerView(list);
                }
                showToast("日志更新完毕！");

            }

            @Override
            public void onGetLogError(String hostId, String errorInfo) {
                super.onGetLogError(hostId, errorInfo);
                AlertDialogUtil.dismissProgressDialog();
                showToast(errorInfo);
            }
        });

        initRecyclerView();

    }



    private void initRecyclerView(){
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(LogActivity.this,LinearLayoutManager.VERTICAL,false);
        mAdapter = new LogListAdapter(mShowLogs,LogActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void refreshRecyclerView(final ArrayList<Log> newList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShowLogs.clear();
                mShowLogs.addAll(newList);
                if(mShowLogs.size()>0){
                    tv_noData.setVisibility(View.GONE);
                }else {
                    tv_noData.setVisibility(View.VISIBLE);
                }
                sortAndNotifyRecyclerView();
            }
        });
    }

    private void sortAndNotifyRecyclerView() {
        mAdapter.sortAndNotifyDataSetChange(isMaxToMinOrder?LogListAdapter.FLAG_COMPARATOR_MAX_MIN:LogListAdapter.FLAG_COMPARATOR_MIN_MAX);
    }

    @Override
    protected void onStart() {
        super.onStart();
        renewLogs();
    }


    @Override
    protected void initListener() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_log,menu);
        MenuItem item = menu.findItem(R.id.menu_toggle_log_order);
        if(isMaxToMinOrder){
            item.setTitle("按日期升序排列");
        }else {
            item.setTitle("按日期降序排列");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_search){
            showSearchDialog();
        }
        if(item.getItemId()==R.id.menu_renew){
            renewLogs();
        }
        if(item.getItemId()==R.id.menu_toggle_log_order){
            toggleLogOrder();
            supportInvalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleLogOrder() {
        isMaxToMinOrder=!isMaxToMinOrder;
        sortAndNotifyRecyclerView();
    }

    private void renewLogs() {
        UserBean user = BaseApplication.getUser();
        if(user==null){
            showToast("登陆信息已过期，请重新登陆。");
        }else {
            AlertDialogUtil.showProgressDialog(this);
            mMySqlModel.getLog(user,mHostId);
        }
    }

    private void showSearchDialog() {
        AlertDialogUtil.showSortLogsDialog(this, new AlertDialogUtil.SortLogRangeListener() {
            @Override
            public void onRangeSelected(Date start, Date end) {
                mDateFrom=start;
                mDateTo=end;
                sortLogListByDate(start,end);
            }

            @Override
            public void onError(String errorInfo) {
                showToast(errorInfo);
            }

            @Override
            public void onChooseToShowAll() {
                mDateFrom=null;
                mDateTo=null;
                refreshRecyclerView(mLogsOrigin);
            }
        });

    }

    private void sortLogListByDate(Date start, Date end) {
        ArrayList<Log> logs=new ArrayList<>();
        for (Log temp: mLogsOrigin){
            Date date = temp.getRecordTime();
            if(date.compareTo(start)>=0&&date.compareTo(end)<=0){
                logs.add(temp);
            }
        }
        refreshRecyclerView(logs);
    }
}
