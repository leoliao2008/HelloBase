package com.skycaster.hellobase.model;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.skycaster.hellobase.bean.ConfigTable;
import com.skycaster.hellobase.bean.ServerBase;
import com.skycaster.hellobase.bean.StateTable;
import com.skycaster.hellobase.interf.MySqlModelCallBack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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

    public void getConfigTable(final Connection con, @Nullable final String mac){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Statement statement=null;
                ResultSet resultSet=null;
                try {
                    statement = con.createStatement();
                    if(mac!=null){
                        resultSet = statement.executeQuery("SELECT * FROM ConfigTable WHERE HostId='"+mac+"'");
                    }else {
                        resultSet =statement.executeQuery("SELECT * FROM ConfigTable");
                    }

                    mCallBack.onGetConfigTableSuccess(getConfigTableByResultSet(resultSet));
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

    private ArrayList<ConfigTable> getConfigTableByResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<ConfigTable> list=new ArrayList<>();
        try {
            while (resultSet.next()){
                ConfigTable tb=new ConfigTable();
                tb.setHostId(resultSet.getString("HostId"));
                tb.setSpecVer(resultSet.getInt("SpecVer"));
                tb.setOpCode(resultSet.getString("OpCode"));
                tb.setTheOwner(resultSet.getString("TheOwner"));
                tb.setCenterFreq(resultSet.getDouble("center_freq"));
                tb.setSignalAmp(resultSet.getInt("signal_amp"));
                tb.setSignFill(resultSet.getInt("sign_fill"));
                tb.setToneLeft(resultSet.getInt("tone_index_left"));
                tb.setToneRight(resultSet.getInt("tone_index_right"));
                tb.setServiceBases(getServiceBasesByResultSet(resultSet));
                list.add(tb);
            }
        } catch (SQLException e) {
            showLog("error during getConfigTableByResultSet(ResultSet resultSet) :"+e.getMessage());
            throw e;
        }
        return list;
    }

    private ArrayList<ServerBase> getServiceBasesByResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<ServerBase> list=new ArrayList<>();
        int id=0;
        try {
            while (id<8){
                ServerBase serviceBase=new ServerBase();
                serviceBase.setId(id);
                serviceBase.setFormCode(resultSet.getInt("s"+id+"_form_code"));
                serviceBase.setLdpcNum(resultSet.getInt("s"+id+"_ldpc_num"));
                serviceBase.setLdpcRate(resultSet.getInt("s"+id+"_ldpc_rate"));
                serviceBase.setIntvSize(resultSet.getInt("s"+id+"_intv_size"));
                serviceBase.setQamType(resultSet.getInt("s"+id+"_qam_type"));
                list.add(serviceBase);
                id++;
            }
        } catch (SQLException e) {
            showLog("error during getServiceBasesByResultSet(ResultSet resultSet) "+e.getMessage());
            throw e;
        }
        return list;
    }

    public void getStateTables(final Connection con, @Nullable final String mac){
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (MySqlModel.this){
                    Statement statement=null;
                    ResultSet resultSet=null;
                    try {
                        statement =con.createStatement();
                        if(mac!=null){
                            resultSet=statement.executeQuery("SELECT * FROM StateTable WHERE HostId='"+mac+"'");
                        }else {
                            resultSet=statement.executeQuery("SELECT * FROM StateTable");
                        }
                        ArrayList<StateTable> tableList = getStateTablesByResultSet(resultSet);
                        mCallBack.onGetStateTablesSuccess(tableList);
                    } catch (SQLException e) {
                        showLog("error while running getStateTables(final Connection con):"+e.getMessage());
                        mCallBack.onGetStateTablesFail(e.getMessage());
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
            }
        }).start();
    }


    private ArrayList<StateTable> getStateTablesByResultSet(ResultSet resultSet) {
        ArrayList<StateTable> list=new ArrayList<>();
        try {
            while (resultSet.next()){
                StateTable st=new StateTable();
                String hostId = resultSet.getString("HostId");
                hostId= TextUtils.isEmpty(hostId)?"unknown":hostId;
                st.setHostId(hostId);
                Timestamp timestamp = resultSet.getTimestamp("FeedbackTime");
                if(timestamp==null){
                    timestamp=new Timestamp(0);
                }
                st.setDateTime(timestamp.getTime());
                st.setCurVer(resultSet.getInt("CurVer"));
                String runningState = resultSet.getString("StateCode");
                runningState=TextUtils.isEmpty(runningState)?"unknown":runningState;
                st.setRunningState(runningState);
                String notes = resultSet.getString("Notes");
                notes=TextUtils.isEmpty(notes)?"unknown":notes;
                st.setNotes(notes);
                list.add(st);
            }
        } catch (SQLException e) {
            showLog("error while running getStateTablesByResultSet(ResultSet resultSet): "+e.getMessage());
            try {
                throw e;
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return list;
    }

    public void updateConfigTable(final ConfigTable configTable, final Connection con) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String sql="update ConfigTable set HostId=?,SpecVer=?,OpCode=?,TheOwner=?,center_freq=?,signal_amp=?,sign_fill=?,tone_index_left=?," +
                            "tone_index_right=?,s0_form_code=?,s0_ldpc_num=?,s0_ldpc_rate=?,s0_intv_size=?,s0_qam_type=?,s1_form_code=?,s1_ldpc_num=?," +
                            "s1_ldpc_rate=?,s1_intv_size=?,s1_qam_type=?,s2_form_code=?,s2_ldpc_num=?,s2_ldpc_rate=?,s2_intv_size=?,s2_qam_type=?,s3_form_code=?," +
                            "s3_ldpc_num=?,s3_ldpc_rate=?,s3_intv_size=?,s3_qam_type=?,s4_form_code=?,s4_ldpc_num=?,s4_ldpc_rate=?,s4_intv_size=?,s4_qam_type=?," +
                            "s5_form_code=?,s5_ldpc_num=?,s5_ldpc_rate=?,s5_intv_size=?,s5_qam_type=?,s6_form_code=?,s6_ldpc_num=?,s6_ldpc_rate=?,s6_intv_size=?," +
                            "s6_qam_type=?,s7_form_code=?,s7_ldpc_num=?,s7_ldpc_rate=?,s7_intv_size=?,s7_qam_type=? where HostId=?";
                    PreparedStatement statement = con.prepareStatement(sql);
                    statement.setString(1,configTable.getHostId());
                    statement.setInt(2,configTable.getSpecVer());
                    statement.setString(3,configTable.getOpCode());
                    statement.setString(4,configTable.getTheOwner());
                    statement.setDouble(5,configTable.getCenterFreq());
                    statement.setInt(6,configTable.getSignalAmp());
                    statement.setInt(7,configTable.getSignFill());
                    statement.setInt(8,configTable.getToneLeft());
                    statement.setInt(9,configTable.getToneRight());
                    int index=10;
                    for (ServerBase temp:configTable.getServiceBases()){
                        statement.setInt(index++,temp.getFormCode());
                        statement.setInt(index++,temp.getLdpcNum());
                        statement.setInt(index++,temp.getLdpcRate());
                        statement.setInt(index++,temp.getIntvSize());
                        statement.setInt(index++,temp.getQamType());
                    }
                    statement.setString(index,configTable.getHostId());
                    statement.execute();
                    int updateCount = statement.getUpdateCount();
                    if(updateCount>0){
                        mCallBack.onUpdateConfigTableSuccess();
                    }else {
                        //// TODO: 2017/9/26 新增一个配置表
                        mCallBack.onUpdateConfigTableFail("找不到对应的配置表。");
                    }
                    statement.close();
                } catch (SQLException e) {
                    showLog("error while running updateConfigTable(ConfigTable configTable,Connection con): "+e.getMessage());
                    mCallBack.onUpdateConfigTableFail(e.getMessage());
                }
            }
        }).start();

    }


    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }
}
