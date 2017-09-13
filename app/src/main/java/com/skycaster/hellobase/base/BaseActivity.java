package com.skycaster.hellobase.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by 廖华凯 on 2017/9/12.
 */

public abstract class BaseActivity extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getRootViewLayoutId());
        initViews();
        initData();
        initListener();
    }

    protected abstract int getRootViewLayoutId();

    protected abstract void initViews();

    protected abstract void initData();

    protected abstract void initListener();


}
