package com.skycaster.hellobase.activity;

import android.content.Context;
import android.content.Intent;
import android.support.percent.PercentFrameLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.base.BaseActivity;
import com.skycaster.hellobase.presenter.LogInActivityPresenter;

public class LogInActivity extends BaseActivity {
    private PercentFrameLayout mRootView;
    private EditText edt_ip;
    private EditText edt_port;
    private EditText edt_pw;
    private EditText edt_uerName;
    private CheckBox cbx_keepInput;
    private Button btn_logIn;
    private ImageView iv_pwVisibility;
    private LogInActivityPresenter mPresenter;


    public static void start(Context context) {
        Intent starter = new Intent(context, LogInActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected int getRootViewLayoutId() {
        return R.layout.activity_log_in;
    }

    @Override
    protected void initViews() {
        mRootView= (PercentFrameLayout) findViewById(R.id.activity_log_in_root_view);
        edt_ip= (EditText) findViewById(R.id.activity_log_in_edt_ip);
        edt_port= (EditText) findViewById(R.id.activity_log_in_edt_port);
        edt_pw= (EditText) findViewById(R.id.activity_log_in_edt_pw);
        cbx_keepInput= (CheckBox) findViewById(R.id.activity_log_in_cbx_keep_input_record);
        btn_logIn= (Button) findViewById(R.id.activity_log_in_btn_log_in);
        iv_pwVisibility= (ImageView) findViewById(R.id.activity_log_in_iv_pw_visibility);
        edt_uerName= (EditText) findViewById(R.id.activity_log_in_edt_user_name);

    }

    @Override
    protected void initData() {
        mPresenter=new LogInActivityPresenter(this);
        mPresenter.initData();
    }

    @Override
    protected void initListener() {
        cbx_keepInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.toggleIsSaveInput();
            }
        });

        iv_pwVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.togglePwVisibility();
            }
        });

        btn_logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.login();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_log_in,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_log_in_wipe_history:
                mPresenter.showWarnigWipeLoginRecord();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public EditText getEdt_ip() {
        return edt_ip;
    }

    public EditText getEdt_port() {
        return edt_port;
    }

    public EditText getEdt_pw() {
        return edt_pw;
    }

    public CheckBox getCbx_keepInput() {
        return cbx_keepInput;
    }

    public EditText getEdt_uerName() {
        return edt_uerName;
    }

    public ImageView getIv_pwVisibility() {
        return iv_pwVisibility;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }
}
