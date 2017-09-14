package com.skycaster.hellobase.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.base.BaseActivity;
import com.skycaster.hellobase.customize.MaxHeightListView;
import com.skycaster.hellobase.presenter.MainActivityPresenter;

public class MainActivity extends BaseActivity {

    private MainActivityPresenter mPresenter;
    private TextView tv_hostAddr;
    private TextView tv_hostVersion;
    private TextView tv_opCode;
    private TextView tv_vendor;
    private TextView tv_freq;
    private TextView tv_amp;
    private TextView tv_fill;
    private TextView tv_leftTune;
    private TextView tv_rightTune;
    private MaxHeightListView mListView;
    private AppCompatSpinner mSpinner;
    private Button btn_connect;
    private ScrollView mScrollView;
    private Button btn_state;

    public static void start(Activity context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected int getRootViewLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        tv_hostAddr= (TextView) findViewById(R.id.main_tv_host_address);
        tv_hostVersion= (TextView) findViewById(R.id.main_tv_host_version);
        tv_opCode= (TextView) findViewById(R.id.main_tv_host_op_code);
        tv_vendor= (TextView) findViewById(R.id.main_tv_vendor_name);
        tv_freq= (TextView) findViewById(R.id.main_tv_frq);
        tv_amp= (TextView) findViewById(R.id.main_tv_signal_amp);
        tv_fill= (TextView) findViewById(R.id.main_tv_signal_fill);
        tv_leftTune= (TextView) findViewById(R.id.main_tv_left_tune);
        tv_rightTune= (TextView) findViewById(R.id.main_tv_right_tune);
        mListView= (MaxHeightListView) findViewById(R.id.main_list_view);
        mSpinner= (AppCompatSpinner) findViewById(R.id.main_spinner);
        btn_connect= (Button) findViewById(R.id.main_btn_connect);
        mScrollView= (ScrollView) findViewById(R.id.main_scroller_view);
        btn_state= (Button) findViewById(R.id.main_btn_to_state_table);

    }

    @Override
    protected void initData() {
        mPresenter=new MainActivityPresenter(this);
        mPresenter.initData();
    }

    @Override
    protected void initListener() {
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.connectMySql();
            }
        });
        btn_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.goToStateTable();
            }
        });
    }

    public TextView getTv_hostAddr() {
        return tv_hostAddr;
    }

    public TextView getTv_hostVersion() {
        return tv_hostVersion;
    }

    public TextView getTv_opCode() {
        return tv_opCode;
    }

    public TextView getTv_vendor() {
        return tv_vendor;
    }

    public TextView getTv_freq() {
        return tv_freq;
    }

    public TextView getTv_amp() {
        return tv_amp;
    }

    public TextView getTv_fill() {
        return tv_fill;
    }

    public TextView getTv_leftTune() {
        return tv_leftTune;
    }

    public TextView getTv_rightTune() {
        return tv_rightTune;
    }

    public ScrollView getScrollView() {
        return mScrollView;
    }

    public AppCompatSpinner getSpinner() {
        return mSpinner;
    }

    public MaxHeightListView getListView() {
        return mListView;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }
}
