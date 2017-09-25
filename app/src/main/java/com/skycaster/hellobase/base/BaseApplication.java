package com.skycaster.hellobase.base;

import android.app.Application;

import com.skycaster.hellobase.bean.StateTable;

import java.sql.Connection;

/**
 * Created by 廖华凯 on 2017/9/13.
 */

public class BaseApplication extends Application {
    private static Connection connection;
    private static StateTable boundTable;
    private static String ipAddress;
    private static String userName;
    private static String password;

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        BaseApplication.connection = connection;
    }

    public static StateTable getBoundTable() {
        return boundTable;
    }

    public static void setBoundTable(StateTable boundTable) {
        BaseApplication.boundTable = boundTable;
    }

    public static String getIpAddress() {
        return ipAddress;
    }

    public static void setIpAddress(String ipAddress) {
        BaseApplication.ipAddress = ipAddress;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        BaseApplication.userName = userName;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        BaseApplication.password = password;
    }
}
