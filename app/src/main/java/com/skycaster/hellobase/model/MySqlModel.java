package com.skycaster.hellobase.model;

import android.util.Log;

import com.skycaster.hellobase.bean.ConfigTable;
import com.skycaster.hellobase.bean.ServiceBase;
import com.skycaster.hellobase.bean.StateTable;
import com.skycaster.hellobase.interf.MySqlModelCallBack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/9/12.
 */

public class MySqlModel {

    //"jdbc:mysql://localhost/test?user=minty&password=greatsqldb"
    private static final String PREFIX = "jdbc:mysql://";
    private MySqlModelCallBack mCallBack;

    public MySqlModel(MySqlModelCallBack callBack) {
        mCallBack = callBack;
    }

    public void connectMySql(final String host, final String dataBase, final String userName, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   Class.forName("com.mysql.jdbc.Driver").newInstance();//加载驱动换成这个
                } catch (Exception e) {
                    showLog("加载数据库引擎失败");
                    mCallBack.onSqlConnectionFail(e.getMessage());
                }
                showLog("数据库驱动成功");

                try {
                    Connection con = DriverManager.getConnection(PREFIX+host+"/"+dataBase+"?user="+userName+"&password="+password);// 连接数据库对象
                    showLog("连接数据库成功！");
                    mCallBack.onGetSqlConnection(con);
                } catch (SQLException e) {
                    showLog(e.getMessage());
                    mCallBack.onSqlConnectionFail(e.getMessage());
                }
            }
        }).start();
    }

    public void requestConfigTable(final Connection con){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Statement statement=null;
                ResultSet resultSet=null;
                try {
                    statement = con.createStatement();
                    resultSet = statement.executeQuery("SELECT * FROM ConfigTable");
                    mCallBack.onGetConfigTableSuccess(getConfigTable(resultSet));
                } catch (SQLException e) {
                    showLog(e.getMessage());
                    mCallBack.onGetConfigTableFail(e.getMessage());
                }finally {
                    if(resultSet!=null){
                        try {
                            resultSet.close();
                        } catch (SQLException e) {
                            showLog(e.getMessage());
                        }
                        resultSet=null;
                    }
                    if(statement!=null){
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            showLog(e.getMessage());
                        }
                        statement=null;
                    }
                }
            }
        }).start();
    }

    private ArrayList<ConfigTable> getConfigTable(ResultSet resultSet) {
        ArrayList<ConfigTable> list=new ArrayList<>();
        try {
            while (resultSet.next()){
                ConfigTable tb=new ConfigTable();
                tb.setHostId(resultSet.getString(1));
                tb.setSpecVer(resultSet.getInt(2));
                tb.setOpCode(resultSet.getString(3));
                tb.setTheOwner(resultSet.getString(4));
                tb.setCenterFreq(resultSet.getDouble(5));
                tb.setSignalAmp(resultSet.getInt(6));
                tb.setSignFill(resultSet.getInt(7));
                tb.setToneLeft(resultSet.getInt(8));
                tb.setToneRight(resultSet.getInt(9));
                tb.setServiceBases(getServiceBases(resultSet));
                list.add(tb);
            }
        } catch (SQLException e) {
            showLog("error during getConfigTable(ResultSet resultSet) "+e.getMessage());
        }
        return list;
    }

    private ArrayList<ServiceBase> getServiceBases(ResultSet resultSet) {
        ArrayList<ServiceBase> list=new ArrayList<>();
        int id=0;
        int cursor=10;
        try {
            while (id<8){
                ServiceBase serviceBase=new ServiceBase();
                serviceBase.setId(id);
                serviceBase.setFormCode(resultSet.getInt(cursor++));
                serviceBase.setLdpcNum(resultSet.getInt(cursor++));
                serviceBase.setLdpcRate(resultSet.getInt(cursor++));
                serviceBase.setIntvSize(resultSet.getInt(cursor++));
                serviceBase.setQamType(resultSet.getInt(cursor++));
                id++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showLog(e.getMessage());
        }
        return list;
    }

    public void requestStateTable(final Connection con, final String mac){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Statement statement=null;
                ResultSet resultSet=null;
                try {
                    statement =con.createStatement();
                    resultSet=statement.executeQuery("SELECT * FROM StateTable WHERE HostId='"+mac+"'");
                    mCallBack.onGetStateTableSuccess(getStateTable(resultSet));
                } catch (SQLException e) {
                    showLog("error while running requestStateTable(final Connection con):"+e.getMessage());
                    mCallBack.onGetStateTableFail(e.getMessage());
                }finally {
                    if(resultSet!=null){
                        try {
                            resultSet.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        resultSet=null;
                    }
                    if(statement!=null){
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        statement=null;
                    }
                }
            }
        }).start();
    }

    private StateTable getStateTable(ResultSet resultSet) {
        StateTable st=new StateTable();
        try {
            while (resultSet.next()){
                st.setHostId(resultSet.getString(1));
                st.setDateTime(resultSet.getTimestamp(2).getTime());
                st.setCurVer(resultSet.getInt(3));
                st.setRunningState(resultSet.getString(4));
                st.setNotes(resultSet.getString(5));
            }
        } catch (SQLException e) {
            showLog("error while running getStateTable(ResultSet resultSet): "+e.getMessage());
        }
        return st;
    }


    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }
}
