package com.skycaster.hellobase.interf;

import com.skycaster.hellobase.bean.ConfigTable;
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

    public void onGetConfigTableFail(String msg){}

    public void onGetStateTablesSuccess(ArrayList<StateTable> stateTables){}

    public void onGetStateTablesFail(String msg){}

    public void onUpdateConfigTableSuccess() {

    }

    public void onUpdateConfigTableFail(String msg) {

    }
}
