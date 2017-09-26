package com.skycaster.hellobase.model;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by 廖华凯 on 2017/9/26.
 */

public class SoftInputManager {
    private Activity mActivity;
    private InputMethodManager mInputMethodManager;

    public SoftInputManager(Activity activity) {
        mActivity = activity;
        mInputMethodManager= (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void hideInputWindow(){
        View decorView = mActivity.getWindow().peekDecorView();
        if(decorView!=null){
            IBinder windowToken = decorView.getWindowToken();
            mInputMethodManager.hideSoftInputFromWindow(windowToken,0);
        }
    }

    public void showInputWindow(){
        mInputMethodManager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
