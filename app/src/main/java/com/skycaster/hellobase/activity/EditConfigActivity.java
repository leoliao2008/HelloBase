package com.skycaster.hellobase.activity;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ScrollView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.base.BaseActivity;
import com.skycaster.hellobase.bean.ConfigTable;
import com.skycaster.hellobase.customize.MaxHeightListView;
import com.skycaster.hellobase.data.StaticData;
import com.skycaster.hellobase.presenter.EditConfigTablePresenter;

public class EditConfigActivity extends BaseActivity {

    private EditText edt_id;
    private EditText edt_version;
    private EditText edt_opCode;
    private EditText edt_vendor;
    private EditText edt_frq;
    private EditText edt_amp;
    private EditText edt_fill;
    private EditText edt_leftTune;
    private EditText edt_rightTune;
    private MaxHeightListView mListView;
    private ScrollView mScrollerView;
    private EditConfigTablePresenter mPresenter;


    public static void start(Context context, ConfigTable table) {
        Intent starter = new Intent(context, EditConfigActivity.class);
        starter.putExtra(StaticData.EXTRA_DATA_CONFIG_TABLE, table);
        context.startActivity(starter);
    }


    @Override
    protected int getRootViewLayoutId() {
        return R.layout.activity_edit_config;
    }

    @Override
    protected void initViews() {
        edt_id= (EditText) findViewById(R.id.activity_edit_config_edt_id);
        edt_version= (EditText) findViewById(R.id.activity_edit_config_edt_version);
        edt_opCode= (EditText) findViewById(R.id.activity_edit_config_edt_ops_code);
        edt_vendor= (EditText) findViewById(R.id.activity_edit_config_edt_vendor);
        edt_amp= (EditText) findViewById(R.id.activity_edit_config_edt_amp);
        edt_frq= (EditText) findViewById(R.id.activity_edit_config_edt_frq);
        edt_fill= (EditText) findViewById(R.id.activity_edit_config_edt_fill);
        edt_leftTune= (EditText) findViewById(R.id.activity_edit_config_edt_left_tune);
        edt_rightTune= (EditText) findViewById(R.id.activity_edit_config_edt_right_tune);
        mListView= (MaxHeightListView) findViewById(R.id.activity_edit_config_list_view);
        mScrollerView= (ScrollView) findViewById(R.id.activity_edit_config_scroll_view);

    }

    @Override
    protected void initData() {
        mPresenter=new EditConfigTablePresenter(this);
        mPresenter.initData();


    }

    @Override
    protected void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.showEditServerBaseDialog(position);
            }
        });

    }

    public EditText getEdt_id() {
        return edt_id;
    }

    public EditText getEdt_version() {
        return edt_version;
    }

    public EditText getEdt_opCode() {
        return edt_opCode;
    }

    public EditText getEdt_vendor() {
        return edt_vendor;
    }

    public EditText getEdt_frq() {
        return edt_frq;
    }

    public EditText getEdt_amp() {
        return edt_amp;
    }

    public EditText getEdt_fill() {
        return edt_fill;
    }

    public EditText getEdt_leftTune() {
        return edt_leftTune;
    }

    public EditText getEdt_rightTune() {
        return edt_rightTune;
    }

    public MaxHeightListView getListView() {
        return mListView;
    }

    public ScrollView getScrollerView() {
        return mScrollerView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_config_table,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit_config_table_submit:
                mPresenter.submit();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mPresenter.onBackPress();
        super.onBackPressed();
    }

}
