package com.skycaster.hellobase.activity;

import android.content.Context;
import android.content.Intent;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.base.BaseActivity;
import com.skycaster.hellobase.bean.Log;
import com.skycaster.hellobase.data.StaticData;

import java.util.ArrayList;

public class LogActivity extends BaseActivity {

    private ArrayList<Log> mLogs;

    public static void start(Context context,ArrayList<Log> logs) {
        Intent starter = new Intent(context, LogActivity.class);
        starter.putParcelableArrayListExtra(StaticData.EXTRA_LOGS,logs);
        context.startActivity(starter);
    }

    @Override
    protected int getRootViewLayoutId() {
        return R.layout.activity_log;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
}
