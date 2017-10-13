package com.skycaster.hellobase.presenter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.skycaster.hellobase.activity.ConfigActivity;
import com.skycaster.hellobase.adapter.ServiceBaseAdapter;
import com.skycaster.hellobase.base.BaseApplication;
import com.skycaster.hellobase.bean.ConfigTable;
import com.skycaster.hellobase.bean.ServerBase;
import com.skycaster.hellobase.bean.UserBean;
import com.skycaster.hellobase.data.StaticData;
import com.skycaster.hellobase.interf.MySqlModelCallBack;
import com.skycaster.hellobase.model.MySqlModel;
import com.skycaster.hellobase.model.SoftInputManager;
import com.skycaster.hellobase.utils.AlertDialogUtil;

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

public class ConfigTablePresenter {
    private ConfigActivity mActivity;
    private ConfigTable mConfigTable;
    private ArrayList<ServerBase> mServerBases=new ArrayList<>();
    private ServiceBaseAdapter mAdapter;
    private Handler mHandler;
    private AlertDialog mAlertDialog;
    private MySqlModel mMySqlModel;
    private ProgressDialog mProgressDialog;
    private int resultCode;
    private SoftInputManager mSoftInputManager;
    private ConfigTable mConfigTableBackUp;
    private String mOpCode;


    public ConfigTablePresenter(ConfigActivity activity) {
        mActivity = activity;
        mHandler=new Handler();
        mMySqlModel=new MySqlModel(new MySqlModelCallBack(){

            @Override
            public void onSqlConnectionFail(String msg) {
                showToast("连接失败："+msg);
                dismissProgressDialog();
                resultCode=StaticData.RESULT_CODE_EDIT_CONFIG_TABLE_FAIL;
            }

            @Override
            public void onUpdateConfigTableSuccess() {
                dismissProgressDialog();
                showToast("修改成功！");
                resultCode=StaticData.RESULT_CODE_EDIT_CONFIG_TABLE_OK;
                //既成事实的修改，需要同步更新备份
                mConfigTableBackUp=mConfigTable.deepClone();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        exitEditMode();
                    }
                });
            }

            @Override
            public void onUpdateConfigTableError(String msg) {
                dismissProgressDialog();
                if(msg.contains("command denied to user")){
                    showToast("此用户无修改激励器配置参数的权限！");
                }else {
                    showToast("修改失败，原因："+msg);
                }
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
        mAdapter=new ServiceBaseAdapter(mServerBases, mActivity, new ServiceBaseAdapter.CallBack() {
            @Override
            public void onPressMoreIcon(int position, ServerBase serverBase) {
                AlertDialogUtil.showServerBaseDetails(mActivity,serverBase,position);
            }

            @Override
            public void onPressSettingIcon(int position, ServerBase serverBase) {
                showEditServerBaseDialog(position);
            }
        });
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
        mActivity.getTv_leftTune().setText(left);

        String right = String.valueOf(tb.getToneRight());
        mActivity.getTv_rightTune().setText(right);

        ArrayList<ServerBase> bases = tb.getServiceBases();
        mServerBases.clear();
        mServerBases.addAll(bases);
        mAdapter.notifyDataSetChanged();

        toggleUIByMode(mActivity.getIsInEditMode().get());
    }

    private void toggleUIByMode(boolean isToEditMode) {
        mActivity.getEdt_vendor().setEnabled(isToEditMode);
        mActivity.getEdt_frq().setEnabled(isToEditMode);
        mActivity.getEdt_amp().setEnabled(isToEditMode);
        mActivity.getEdt_fill().setEnabled(isToEditMode);
        mActivity.getTv_leftTune().setEnabled(isToEditMode);
        mActivity.getTv_rightTune().setEnabled(isToEditMode);
        mActivity.getEdt_version().setEnabled(isToEditMode);
        mAdapter.toggleMode(isToEditMode);
    }

    public void showAlertDialogChooseTunes(){
        if(!mActivity.getIsInEditMode().get()){
            return;
        }
        String[] items=new String[]{
                "左频36，右频45",
                "左频26，右频45",
                "左频46，右频60"
        };
        int leftTune=Integer.valueOf(mActivity.getTv_leftTune().getText().toString().trim());
        int rightTune=Integer.valueOf(mActivity.getTv_rightTune().getText().toString().trim());
        int checkedItem;
        if(leftTune==36&&rightTune==45){
            checkedItem=0;
        }else if(leftTune==26&&rightTune==45){
            checkedItem=1;
        }else if(leftTune==46&&rightTune==60){
            checkedItem=2;
        }else {
            checkedItem=-1;
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
        mAlertDialog = builder.setTitle("请选择左频及右频参数")
                .setSingleChoiceItems(
                        items,
                        checkedItem,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        mActivity.getTv_leftTune().setText("36");
                                        mActivity.getTv_rightTune().setText("45");
                                        break;
                                    case 1:
                                        mActivity.getTv_leftTune().setText("26");
                                        mActivity.getTv_rightTune().setText("45");
                                        break;
                                    case 2:
                                        mActivity.getTv_leftTune().setText("46");
                                        mActivity.getTv_rightTune().setText("60");
                                        break;
                                }
                                mAlertDialog.dismiss();
                            }
                        }

                )
                .create();
        mAlertDialog.show();
    }



    public void showEditServerBaseDialog(final int position) {
        AlertDialogUtil.showEditServerBaseDialog(mActivity, mServerBases.get(position), new AlertDialogUtil.ServerBaseEditListener() {
            @Override
            public void onConfirmEdit(ServerBase confirm) {
                confirm.lightClone(mServerBases.get(position));
                //同步更新数据
                confirm.lightClone(mConfigTable.getServiceBases().get(position));
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void dismissAlertDialog() {
        if(mAlertDialog!=null){
            mAlertDialog.dismiss();
        }
        mSoftInputManager.hideInputWindow();
    }

    private void showToast(String msg){
        mActivity.showToast(msg);
    }

    public void submit(){
        submit(mConfigTable);
    }

    public void submit(final ConfigTable configTable) {
        if(checkAndUpdateLocalConfigTable()){
            final UserBean user = BaseApplication.getUser();
            if(user!=null){
                mOpCode = StaticData.OP_CODE_RECONFIG;
                if(mConfigTable.getSpecVer()!=mConfigTableBackUp.getSpecVer()){
                    AlertDialogUtil.showBaseDialog(
                            mActivity,
                            "温馨提示",
                            "您修改了激励器系统版本号，你可以选择提交后立即重启激励器，也可以延迟重启，这样新的版本号将在下次重启后才生效。",
                            "立刻重启",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mOpCode = StaticData.OP_CODE_REBOOT;
                                    mConfigTable.setOpCode(mOpCode);
                                    mActivity.getEdt_opCode().setText(StaticData.OP_CODE_REBOOT);
                                    showProgressDialog();
                                    mMySqlModel.updateConfigTable(user,mConfigTable);
                                }
                            },
                            "延迟重启",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mConfigTable.setOpCode(mOpCode);
                                    mActivity.getEdt_opCode().setText(StaticData.OP_CODE_REBOOT);
                                    showProgressDialog();
                                    mMySqlModel.updateConfigTable(user,configTable);
                                }
                            },
                            null,
                            null
                    );
                }else {
                    AlertDialogUtil.showBaseDialog(
                            mActivity,
                            "温馨提示",
                            "您确定提交修改吗？",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mConfigTable.setOpCode(mOpCode);
                                    mActivity.getEdt_opCode().setText(StaticData.OP_CODE_REBOOT);
                                    showProgressDialog();
                                    mMySqlModel.updateConfigTable(user,configTable);
                                }
                            }
                    );
                }
            }else{
                dismissProgressDialog();
                mActivity.showToast("登陆信息过期，请重新登陆。");
            }
        }
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
        String leftTune = mActivity.getTv_leftTune().getText().toString().trim();
        if(TextUtils.isEmpty(leftTune)){
            showToast("左频不能为空。");
            return false;
        }
        String rightTune = mActivity.getTv_rightTune().getText().toString().trim();
        if(TextUtils.isEmpty(rightTune)){
            showToast("右频不能为空。");
            return false;
        }
        String version = mActivity.getEdt_version().getText().toString().trim();
        if(TextUtils.isEmpty(version)){
            showToast("版本号不能为空。");
            return false;
        }
        mConfigTable.setTheOwner(vendor);
        mConfigTable.setCenterFreq(Double.parseDouble(freq));
        mConfigTable.setSignalAmp(Integer.parseInt(amp));
        mConfigTable.setSignFill(Integer.parseInt(fill));
        mConfigTable.setToneLeft(Integer.parseInt(leftTune));
        mConfigTable.setToneRight(Integer.parseInt(rightTune));
        mConfigTable.setSpecVer(Integer.parseInt(version));
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


    public void enterEditMode() {
        mActivity.getIsInEditMode().set(true);
        mActivity.supportInvalidateOptionsMenu();
        mConfigTableBackUp=mConfigTable.deepClone();
        toggleUIByMode(true);
        showToast("您已进入编辑模式。");

    }

    public void exitEditMode() {
        mActivity.getIsInEditMode().set(false);
        mActivity.supportInvalidateOptionsMenu();
        mConfigTable=mConfigTableBackUp.deepClone();
        updateUiByConfigTable(mConfigTable);
    }
}
