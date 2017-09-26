package com.skycaster.hellobase.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.adapter.StateTableListAdapter;
import com.skycaster.hellobase.base.BaseActivity;
import com.skycaster.hellobase.bean.StateTable;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/9/25.
 */

public class StateTableListActivity extends BaseActivity {

    private static final String EXTRA_PARCELABLE_ARRAY_STATE_TABLE_LIST = "EXTRA_PARCELABLE_ARRAY_STATE_TABLE_LIST";
    private ListView mListView;
    private ArrayList<StateTable> mList=new ArrayList<>();
    private StateTableListAdapter mAdapter;
    private AlertDialog mAlertDialog;

    public static void start(Context context,ArrayList<StateTable>list) {
        Intent starter = new Intent(context, StateTableListActivity.class);
        starter.putParcelableArrayListExtra(EXTRA_PARCELABLE_ARRAY_STATE_TABLE_LIST,list);
        context.startActivity(starter);
    }
    @Override
    protected int getRootViewLayoutId() {
        return R.layout.activity_state_table_list;
    }

    @Override
    protected void initViews() {
        mListView= (ListView) findViewById(R.id.activity_state_table_list_lst_view);

    }

    @Override
    protected void initData() {
        ArrayList<StateTable> list = getIntent().getParcelableArrayListExtra(EXTRA_PARCELABLE_ARRAY_STATE_TABLE_LIST);
        if(list!=null){
            mList.addAll(list);
        }
        showLog(mList.toString());
        mAdapter=new StateTableListAdapter(mList,this);
        mListView.setAdapter(mAdapter);
        mListView.setDividerHeight(0);

    }

    @Override
    protected void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StateTable table = mList.get(position);
                StateTableActivity.start(StateTableListActivity.this,table);
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("温馨提示")
                .setMessage("您确定要退出本程序吗？")
                .setCancelable(true)
                .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                        StateTableListActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                    }
                });
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }
}
