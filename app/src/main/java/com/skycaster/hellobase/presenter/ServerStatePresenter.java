package com.skycaster.hellobase.presenter;

import com.skycaster.hellobase.activity.ServerStateActivity;
import com.skycaster.hellobase.bean.StateTable;
import com.skycaster.hellobase.data.StaticData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/9/13.
 */

public class ServerStatePresenter {
    private ServerStateActivity mActivity;
    private DateFormat mDateFormat;
    private StateTable mStateTable;

    public ServerStatePresenter(ServerStateActivity activity) {
        mActivity = activity;
    }

    public void initData(){
        mDateFormat= new SimpleDateFormat("hh:mm:ss", Locale.CHINA);
        mStateTable=mActivity.getIntent().getParcelableExtra(StaticData.EXTRA_DATA_STATE_TABLE);
        updateActivityUi();
    }

    public void updateActivityUi() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.getTv_mac().setText(mStateTable.getHostId());
                mActivity.getTv_version().setText(String.valueOf(mStateTable.getCurVer()));
                mActivity.getTv_status().setText(mStateTable.getRunningState());
                mActivity.getTv_feedbackTime().setText(mDateFormat.format(new Date(mStateTable.getDateTime())));
                mActivity.getTv_comments().setText(mStateTable.getNotes());
            }
        });
    }
}
