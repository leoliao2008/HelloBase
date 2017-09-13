package com.skycaster.hellobase.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.TextView;

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

    public static void start(Activity context, StateTable st) {
        Intent starter = new Intent(context, ServerStateActivity.class);
        starter.putExtra(StaticData.EXTRA_DATA_STATE_TABLE,st);
        context.startActivity(starter, ActivityOptionsCompat.makeSceneTransitionAnimation(context).toBundle());
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
}
