package com.skycaster.hellobase.base;

import android.app.Application;

import java.sql.Connection;

/**
 * Created by 廖华凯 on 2017/9/13.
 */

public class BaseApplication extends Application {
    private static Connection connection;

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        BaseApplication.connection = connection;
    }
}
