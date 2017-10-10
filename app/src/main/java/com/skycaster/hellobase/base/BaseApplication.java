package com.skycaster.hellobase.base;

import android.app.Application;
import android.util.Log;

import com.skycaster.hellobase.bean.StateTable;
import com.skycaster.hellobase.bean.UserBean;

/**
 * Created by 廖华凯 on 2017/9/13.
 */

public class BaseApplication extends Application {
    private static StateTable currentStateTable;
    private static UserBean user;

    public static StateTable getCurrentStateTable() {
        return currentStateTable;
    }

    public static void setCurrentStateTable(StateTable currentStateTable) {
        BaseApplication.currentStateTable = currentStateTable;
    }

    public static UserBean getUser() {
        return user;
    }

    public static void setUser(UserBean user) {
        BaseApplication.user = user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();//加载驱动换成这个
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(),"加载数据库引擎失败！");
        }
        Log.e(getClass().getSimpleName(),"加载数据库引擎成功！");
    }
}
