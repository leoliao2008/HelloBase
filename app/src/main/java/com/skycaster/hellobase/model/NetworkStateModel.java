package com.skycaster.hellobase.model;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 廖华凯 on 2017/9/25.
 */

public class NetworkStateModel {
    private Activity mActivity;
    private final ConnectivityManager mManager;

    public NetworkStateModel(Activity activity) {
        mActivity = activity;
        mManager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    public boolean isNetworkAvailable(){
        if(mManager==null){
            return false;
        }
        NetworkInfo networkInfo = mManager.getActiveNetworkInfo();
        if(networkInfo==null){
            return false;
        }
        return networkInfo.isConnected();
    }

}
