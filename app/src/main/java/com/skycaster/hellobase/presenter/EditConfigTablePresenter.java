package com.skycaster.hellobase.presenter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.activity.EditConfigActivity;
import com.skycaster.hellobase.adapter.ServiceBaseAdapter;
import com.skycaster.hellobase.base.BaseApplication;
import com.skycaster.hellobase.bean.ConfigTable;
import com.skycaster.hellobase.bean.ServerBase;
import com.skycaster.hellobase.data.StaticData;
import com.skycaster.hellobase.interf.MySqlModelCallBack;
import com.skycaster.hellobase.model.MySqlModel;
import com.skycaster.hellobase.model.SoftInputManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

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
    private AlertDialog mAlertDialog;
    private MySqlModel mMySqlModel;
    private ProgressDialog mProgressDialog;
    private int resultCode;
    private SoftInputManager mSoftInputManager;


    public EditConfigTablePresenter(EditConfigActivity activity) {
        mActivity = activity;
        mHandler=new Handler();
        mMySqlModel=new MySqlModel(new MySqlModelCallBack(){
            @Override
            public void onGetSqlConnection(Connection con) {
                super.onGetSqlConnection(con);
                BaseApplication.setConnection(con);
                mMySqlModel.updateConfigTable(mConfigTable, con);
            }

            @Override
            public void onGetStateTablesFail(String msg) {
                super.onGetStateTablesFail(msg);
                dismissProgressDialog();
                showToast(msg);
            }

            @Override
            public void onUpdateConfigTableSuccess() {
                dismissProgressDialog();
                showToast("修改成功！");
                resultCode=StaticData.RESULT_CODE_EDIT_CONFIG_TABLE_OK;
            }

            @Override
            public void onUpdateConfigTableFail(String msg) {
                dismissProgressDialog();
                showToast(msg);
                resultCode=StaticData.RESULT_CODE_EDIT_CONFIG_TABLE_FAIL;
            }
        });
        mSoftInputManager=new SoftInputManager(mActivity);
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
        mServerBases.clear();
        mServerBases.addAll(bases);
        mAdapter.notifyDataSetChanged();
    }

    public void showEditServerBaseDialog(final int position) {
        //init view
        View rootView=View.inflate(mActivity, R.layout.dialog_edit_base_server,null);
        final EditText edt_qamType=rootView.findViewById(R.id.dialog_config_server_base_edt_qam_type);
        final EditText edt_ldcpNum=rootView.findViewById(R.id.dialog_config_server_base_edt_ldcp_num);
        final EditText edt_ldcpSize=rootView.findViewById(R.id.dialog_config_server_base_edt_ldcp_size);
        final EditText edt_ldcpRate=rootView.findViewById(R.id.dialog_config_server_base_edt_ldcp_rate);
        TextView tv_order=rootView.findViewById(R.id.dialog_config_server_base_tv_order);
        Button btn_confirm=rootView.findViewById(R.id.dialog_config_server_base_btn_confirm);
        Button btn_cancel=rootView.findViewById(R.id.dialog_config_server_base_btn_cancel);
        //init data
        final ServerBase base = mServerBases.get(position);
        String type=String.valueOf(base.getQamType());
        edt_qamType.setText(type);
        edt_qamType.setSelection(type.length());
        String rate = String.valueOf(base.getLdpcRate());
        edt_ldcpRate.setText(rate);
        edt_ldcpRate.setSelection(rate.length());
        String num = String.valueOf(base.getLdpcNum());
        edt_ldcpNum.setText(num);
        edt_ldcpNum.setSelection(num.length());
        String size = String.valueOf(base.getIntvSize());
        edt_ldcpSize.setText(size);
        edt_ldcpSize.setSelection(size.length());
        tv_order.setText(String.format(Locale.CHINA,"%02d",base.getId()));
        //int listeners
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAlertDialog();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_type = edt_qamType.getText().toString().trim();
                if(TextUtils.isEmpty(str_type)){
                    showToast("调制类型不能为空。");
                    return;
                }
                String str_num = edt_ldcpNum.getText().toString().trim();
                if(TextUtils.isEmpty(str_num)){
                    showToast("LDCP码字个数不能为空。");
                    return;
                }
                String str_rate=edt_ldcpRate.getText().toString().trim();
                if(TextUtils.isEmpty(str_rate)){
                    showToast("LDCP编码率不能为空。");
                    return;
                }
                String str_size = edt_ldcpSize.getText().toString().trim();
                if(TextUtils.isEmpty(str_size)){
                    showToast("交织块大小不能为空。");
                    return;
                }
                base.setQamType(Integer.valueOf(str_type));
                base.setIntvSize(Integer.valueOf(str_size));
                base.setLdpcRate(Integer.valueOf(str_rate));
                base.setLdpcNum(Integer.valueOf(str_num));

                //同步更新数据
                ServerBase serverBase = mConfigTable.getServiceBases().get(position);
                serverBase.setQamType(Integer.valueOf(str_type));
                serverBase.setIntvSize(Integer.valueOf(str_size));
                serverBase.setLdpcRate(Integer.valueOf(str_rate));
                serverBase.setLdpcNum(Integer.valueOf(str_num));

                mAdapter.notifyDataSetChanged();
                dismissProgressDialog();
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
        mAlertDialog = builder.setView(rootView).setCancelable(true).create();
        mAlertDialog.show();
    }

    private void dismissAlertDialog() {
        mAlertDialog.dismiss();
        mSoftInputManager.hideInputWindow();
    }

    private void showToast(String msg){
        mActivity.showToast(msg);
    }

    public void submit() {
        AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
        builder.setTitle("温馨提示")
                .setMessage("提交更改后，激励器将重新启动，您确定要提交吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                        showProgressDialog();
                        if(checkAndUpdateLocalConfigTable()){
                            Connection con = BaseApplication.getConnection();
                            try {
                                if(con!=null&&con.isValid(50)){
                                    mMySqlModel.updateConfigTable(mConfigTable, con);
                                }else {
                                    mMySqlModel.connectMySql(BaseApplication.getIpAddress(),StaticData.DATA_BASE_NAME,BaseApplication.getUserName(),BaseApplication.getPassword());
                                }
                            } catch (SQLException e) {
                                dismissProgressDialog();
                                showToast(e.getMessage());
                            }

                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                    }
                })
                .setCancelable(true);
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private boolean checkAndUpdateLocalConfigTable() {
        String vendor = mActivity.getEdt_vendor().getText().toString().trim();
        if(TextUtils.isEmpty(vendor)){
            showToast("运营商不能为空。");
            return false;
        }
        String freq = mActivity.getEdt_frq().getText().toString().trim();
        if(TextUtils.isEmpty(freq)){
            showToast("工作频点不能为空。");
            return false;
        }
        String amp=mActivity.getEdt_amp().getText().toString().trim();
        if(TextUtils.isEmpty(amp)){
            showToast("输出功率（微调）不能为空。");
            return false;
        }
        String fill = mActivity.getEdt_fill().getText().toString().trim();
        if(TextUtils.isEmpty(fill)){
            showToast("输出功率（粗调）不能为空。");
            return false;
        }
        String leftTune = mActivity.getEdt_leftTune().getText().toString().trim();
        if(TextUtils.isEmpty(leftTune)){
            showToast("左频不能为空。");
            return false;
        }
        String rightTune = mActivity.getEdt_rightTune().getText().toString().trim();
        if(TextUtils.isEmpty(rightTune)){
            showToast("右频不能为空。");
            return false;
        }
        mConfigTable.setTheOwner(vendor);
        mConfigTable.setCenterFreq(Double.parseDouble(freq));
        mConfigTable.setSignalAmp(Integer.parseInt(amp));
        mConfigTable.setSignFill(Integer.parseInt(fill));
        mConfigTable.setToneLeft(Integer.parseInt(leftTune));
        mConfigTable.setToneRight(Integer.parseInt(rightTune));
        return true;
    }

    private void showProgressDialog() {
        if(mProgressDialog==null){
            mProgressDialog = ProgressDialog.show(
                    mActivity,
                    "提交中",
                    "向服务器提交更新，请稍候......",
                    true,
                    false);
        }

    }

    private void dismissProgressDialog(){
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
            mProgressDialog=null;
        }
    }

    public void onBackPress(){
        Intent it=new Intent();
        if(resultCode==StaticData.RESULT_CODE_EDIT_CONFIG_TABLE_OK){
            it.putExtra(StaticData.EXTRA_DATA_CONFIG_TABLE,mConfigTable);
        }
        mActivity.setResult(resultCode,it);
    }

}