package com.skycaster.hellobase.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.adapter.LvOneStListAdapter;
import com.skycaster.hellobase.base.BaseActivity;
import com.skycaster.hellobase.bean.StateTable;
import com.skycaster.hellobase.bean.StateTableLabel;
import com.skycaster.hellobase.utils.AlertDialogUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/9/25.
 */

public class StateTableListActivity extends BaseActivity {

    private static final String EXTRA_PARCELABLE_ARRAY_STATE_TABLE_LIST = "EXTRA_PARCELABLE_ARRAY_STATE_TABLE_LIST";
    private ListView mListView;
    private ArrayList<StateTableLabel> mList=new ArrayList<>();
    private LvOneStListAdapter mAdapter;

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
        sortTables(list);
        mAdapter=new LvOneStListAdapter(mList,this);
        mListView.setAdapter(mAdapter);
        mListView.setDividerHeight(0);

    }

    private void sortTables(ArrayList<StateTable> stateTables) {
        for (StateTable temp:stateTables){
            boolean isContain=false;
            for (StateTableLabel label:mList){
                if(label.isContainTable(temp)){
                    isContain=true;
                    label.getStateTables().add(temp);
                    break;
                }
            }
            if(!isContain){
                StateTableLabel label=new StateTableLabel(temp.getTheOwner());
                label.getStateTables().add(temp);
                mList.add(label);
            }
        }
    }

    @Override
    protected void initListener() {
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                StateTable table = mList.get(position);
//                StateActivity.start(StateTableListActivity.this,table);
//            }
//        });
        mAdapter.setOnItemClickListener(new LvOneStListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(StateTable table) {
                StateActivity.start(StateTableListActivity.this,table);
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialogUtil.showBaseDialog(this, "温馨提示", "您确定要退出本程序吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StateTableListActivity.super.onBackPressed();
            }
        });
    }
}
