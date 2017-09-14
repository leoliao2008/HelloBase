package com.skycaster.hellobase.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.base.BaseActivity;
import com.skycaster.hellobase.bean.StateTable;
import com.skycaster.hellobase.data.StaticData;
import com.skycaster.hellobase.presenter.ServerStatePresenter;

public class ServerStateActivity extends BaseActivity {

    private TextView tv_mac;
    private TextView tv_version;
    private TextView tv_status;
    private TextView tv_comments;
    private TextView tv_feedbackTime;
    private ServerStatePresenter mPresenter;
    private ToggleButton tgbtn_monitoring;
    private TextView tv_statusReport;
    private ImageView iv_statusReport;


    public static void start(Activity context, StateTable stateTable) {
        Intent starter = new Intent(context, ServerStateActivity.class);
        starter.putExtra(StaticData.EXTRA_DATA_STATE_TABLE,stateTable);
        context.startActivity(starter);
    }


    @Override
    protected int getRootViewLayoutId() {
        return R.layout.activity_server_state;
    }

    @Override
    protected void initViews() {
        tv_mac= (TextView) findViewById(R.id.state_tv_mac_address);
        tv_version= (TextView) findViewById(R.id.state_tv_version);
        tv_comments= (TextView) findViewById(R.id.state_tv_comments);
        tv_feedbackTime= (TextView) findViewById(R.id.state_tv_feed_back_time);
        tv_status= (TextView) findViewById(R.id.state_tv_status);
        tgbtn_monitoring= (ToggleButton) findViewById(R.id.state_toggle_btn_monitoring);
        tv_statusReport= (TextView) findViewById(R.id.state_tv_status_report);
        iv_statusReport= (ImageView) findViewById(R.id.state_iv_status_report);

    }

    @Override
    protected void initData() {
        mPresenter=new ServerStatePresenter(this);
        mPresenter.initData();
    }

    @Override
    protected void initListener() {

    }

    public TextView getTv_mac() {
        return tv_mac;
    }

    public TextView getTv_version() {
        return tv_version;
    }

    public TextView getTv_status() {
        return tv_status;
    }

    public TextView getTv_comments() {
        return tv_comments;
    }

    public TextView getTv_feedbackTime() {
        return tv_feedbackTime;
    }

    public ToggleButton getTgbtn_monitoring() {
        return tgbtn_monitoring;
    }

    public TextView getTv_statusReport() {
        return tv_statusReport;
    }

    public ImageView getIv_statusReport() {
        return iv_statusReport;
    }

    public void startMonitoring(View view) {
        if(tgbtn_monitoring.isChecked()){
            mPresenter.startMonitoring();
        }else {
            mPresenter.stopMonitoring();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }



}
