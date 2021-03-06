package com.skycaster.hellobase.interf;

import com.skycaster.hellobase.bean.ConfigTable;
import com.skycaster.hellobase.bean.Log;
import com.skycaster.hellobase.bean.StateTable;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/9/12.
 */

public class MySqlModelCallBack {
    public void onGetSqlConnection(Connection con){}

    public void onSqlConnectionFail(String msg){}

    public void onGetConfigTableSuccess(ArrayList<ConfigTable> tables){}

    public void onGetConfigTableError(String error){}

    public void onGetStateTablesSuccess(ArrayList<StateTable> stateTables){}

    public void onGetStateTablesError(String error){}

    public void onUpdateConfigTableSuccess() {

    }

    public void onUpdateConfigTableError(String msg) {

    }

    public void onTargetConfigTableNotExist() {

    }

    public void onCreateNewConfigTableSuccess(ConfigTable table) {

    }

    public void onCreateNewConfigTableFails(String error) {

    }

    public void onGetLogError(String hostId, String errorInfo) {

    }

    public void onObtainLog(ArrayList<Log> list) {

    }

    /**
     * 2018.3.20更新
     * @param tokens
     */
    public void onGetPermissionTokens(ArrayList<String> tokens) {

    }

    public void onGetPermissionTokensFail(String error) {

    }
}
