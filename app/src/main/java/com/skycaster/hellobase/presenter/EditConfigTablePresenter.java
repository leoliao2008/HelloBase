package com.skycaster.hellobase.presenter;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.skycaster.hellobase.activity.EditConfigActivity;
import com.skycaster.hellobase.adapter.ServiceBaseAdapter;
import com.skycaster.hellobase.bean.ConfigTable;
import com.skycaster.hellobase.bean.ServerBase;
import com.skycaster.hellobase.data.StaticData;

import java.util.ArrayList;

/**
 * 创建者     $Author$
 * 创建时间   2017/9/25 22:57
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */

public class EditConfigTablePresenter {
    private EditConfigActivity mActivity;
    private ConfigTable mConfigTable;
    private ArrayList<ServerBase> mServerBases=new ArrayList<>();
    private ServiceBaseAdapter mAdapter;
    private Handler mHandler;


    public EditConfigTablePresenter(EditConfigActivity activity) {
        mActivity = activity;
        mHandler=new Handler();
    }

    public void initData(){
        initListView();

        mConfigTable=mActivity.getIntent().getParcelableExtra(StaticData.EXTRA_DATA_CONFIG_TABLE);
        if(mConfigTable!=null){
            updateUiByConfigTable(mConfigTable);
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.getScrollerView().fullScroll(View.FOCUS_UP);
            }
        },500);

    }

    private void initListView() {
        mAdapter=new ServiceBaseAdapter(mServerBases,mActivity);
        mActivity.getListView().setAdapter(mAdapter);
        mActivity.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    private void updateUiByConfigTable(ConfigTable tb) {
        String hostId = tb.getHostId();
        if(!TextUtils.isEmpty(hostId)){
            mActivity.getEdt_id().setText(hostId);
            mActivity.getEdt_id().setSelection(hostId.length());
        }
        String opCode = tb.getOpCode();
        if(!TextUtils.isEmpty(opCode)){
            mActivity.getEdt_opCode().setText(opCode);
            mActivity.getEdt_opCode().setSelection(opCode.length());
        }
        String centerFreq = String.valueOf(tb.getCenterFreq());
        mActivity.getEdt_frq().setText(centerFreq);
        mActivity.getEdt_frq().setSelection(centerFreq.length());

        String version = String.valueOf(tb.getSpecVer());
        mActivity.getEdt_version().setText(version);
        mActivity.getEdt_version().setSelection(version.length());

        String vendor = tb.getTheOwner();
        if(!TextUtils.isEmpty(vendor)){
            mActivity.getEdt_vendor().setText(vendor);
            mActivity.getEdt_vendor().setSelection(vendor.length());
        }

        String amp = String.valueOf(tb.getSignalAmp());
        mActivity.getEdt_amp().setText(amp);
        mActivity.getEdt_amp().setSelection(amp.length());

        String fill = String.valueOf(tb.getSignFill());
        mActivity.getEdt_fill().setText(fill);
        mActivity.getEdt_fill().setSelection(fill.length());

        String left = String.valueOf(tb.getToneLeft());
        mActivity.getEdt_leftTune().setText(left);
        mActivity.getEdt_leftTune().setSelection(left.length());

        String right = String.valueOf(tb.getToneRight());
        mActivity.getEdt_rightTune().setText(right);
        mActivity.getEdt_rightTune().setSelection(right.length());

        ArrayList<ServerBase> bases = tb.getServiceBases();
        mServerBases.addAll(bases);
        mAdapter.notifyDataSetChanged();
    }
}
