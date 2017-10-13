package com.skycaster.hellobase.model;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.skycaster.hellobase.bean.ConfigTable;
import com.skycaster.hellobase.bean.ServerBase;
import com.skycaster.hellobase.bean.StateTable;
import com.skycaster.hellobase.bean.UserBean;
import com.skycaster.hellobase.data.StaticData;
import com.skycaster.hellobase.interf.MySqlModelCallBack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

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
                Connection con=null;
                try {
                    con=getConnection(new UserBean(userName,password,host,StaticData.DATA_BASE_NAME));
                    showLog("连接成功！");
                    mCallBack.onGetSqlConnection(con);
                } catch (SQLException e) {
                    showLog(e.getMessage());
                    mCallBack.onSqlConnectionFail(e.getMessage());
                }
            }
        }).start();
    }

    private ArrayList<ServerBase> getServiceBasesByResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<ServerBase> list=new ArrayList<>();
        int id=0;
        try {
            while (id<8){
                ServerBase serviceBase=new ServerBase();
                serviceBase.setId(id);
                String[] phyParas = resultSet.getString("s" + id + "_phy_para").split(Pattern.quote(","));
                if(phyParas.length==5){
                    serviceBase.setFormCode(Integer.parseInt(phyParas[0]));
                    serviceBase.setLdpcNum(Integer.parseInt(phyParas[1]));
                    serviceBase.setLdpcRate(Integer.parseInt(phyParas[2]));
                    serviceBase.setIntvSize(Integer.parseInt(phyParas[3]));
                    serviceBase.setQamType(Integer.parseInt(phyParas[4]));
                }
                String[] corsParas = resultSet.getString("s" + id + "_cors_para").split(Pattern.quote(","));
//              '服务器IP, 端口号, 用户名, 密码, 数据格式, 经度, 纬度, 高度'
                if(corsParas.length==8){
                    serviceBase.setIp(corsParas[0]);
                    serviceBase.setPort(corsParas[1]);
                    serviceBase.setUserName(corsParas[2]);
                    serviceBase.setPw(corsParas[3]);
                    serviceBase.setDataFormat(corsParas[4]);
                    serviceBase.setLatitude(corsParas[5]);
                    serviceBase.setLongitude(corsParas[6]);
                    serviceBase.setAltitude(corsParas[7]);
                }
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
                        mCallBack.onGetStateTablesError(e.getMessage());
                    }finally {
                        if(resultSet!=null){
                            try {
                                resultSet.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                resultSet=null;
                            }

                        }
                        if(statement!=null){
                            try {
                                statement.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                statement=null;
                            }

                        }
                    }
                }
            }
        }).start();
    }


    private ArrayList<StateTable> getStateTablesByResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<StateTable> list=new ArrayList<>();
        try {
            while (resultSet.next()){
                StateTable st=new StateTable();
                String hostId = resultSet.getString("HostId");
                hostId= TextUtils.isEmpty(hostId)?"unknown":hostId;
                st.setHostId(hostId);
                String theOwner = resultSet.getString("TheOwner");
                st.setTheOwner(theOwner);
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
            throw e;
        }
        return list;
    }



    public void getStateTables(final UserBean userBean,@Nullable final String mac){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection con=null;
                try {
                    con=getConnection(userBean);
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
                        showLog("error while running getStateTables(final UserBean userBean,@Nullable final String mac):"+e.getMessage());
                        mCallBack.onGetStateTablesError(e.getMessage());
                    }finally {
                        if(resultSet!=null){
                            try {
                                resultSet.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                resultSet=null;
                            }
                        }
                        if(statement!=null){
                            try {
                                statement.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                statement=null;
                            }

                        }
                    }

                } catch (SQLException e) {
                    mCallBack.onSqlConnectionFail(e.getMessage());
                } finally {
                    if(con!=null){
                        try {
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        con=null;
                    }
                }
            }
        }).start();
    }

    public void getConfigTable(final UserBean userBean, final String mac){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection con=null;
                try {
                    con = getConnection(userBean);
                    Statement statement=null;
                    ResultSet resultSet=null;
                    try {
                        statement = con.createStatement();
                        if(mac!=null){
                            resultSet = statement.executeQuery("SELECT * FROM ConfigTable WHERE HostId='"+mac+"'");
                        }else {
                            resultSet =statement.executeQuery("SELECT * FROM ConfigTable");
                        }

                        ArrayList<ConfigTable> configTables = getConfigTableByResultSet(resultSet);
                        if(!configTables.isEmpty()){
                            mCallBack.onGetConfigTableSuccess(configTables);
                        }else {
                            mCallBack.onTargetConfigTableNotExist();
                        }
                    } catch (SQLException e) {
                        showLog(e.getMessage());
                        mCallBack.onGetConfigTableError(e.getMessage());
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
                } catch (SQLException e) {
                    showLog("error while running getConfigTable(final UserBean userBean, final String mac):"+e.getMessage());
                    mCallBack.onSqlConnectionFail(e.getMessage());
                } finally {
                    if(con!=null){
                        try {
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        con=null;
                    }
                }
            }
        }).start();
    }

    public void updateConfigTable(final UserBean userBean, final ConfigTable configTable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection con=null;
                try {
                    con=getConnection(userBean);
                    PreparedStatement statement=null;
                    try {
                        String sql= genUpdateCfTableSqlCommand();
                        statement = con.prepareStatement(sql);
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
                        int size = configTable.getServiceBases().size();
                        for (int i=0;i<8;i++){
                            if(i<size){
//                                #'form_code, ldpc_num, ldpc_rate, intv_size, qam_type'
                                ServerBase temp = configTable.getServiceBases().get(i);
                                statement.setString(index++,
                                        new StringBuffer()
                                        .append(temp.getFormCode())
                                        .append(",")
                                        .append(temp.getLdpcNum())
                                        .append(",")
                                        .append(temp.getLdpcRate())
                                        .append(",")
                                        .append(temp.getIntvSize())
                                        .append(",")
                                        .append(temp.getQamType())
                                        .toString()

                                );
//                                #'服务器IP, 端口号, 用户名, 密码, 数据格式, 经度, 纬度, 高度'
                                statement.setString(index++,
                                        new StringBuilder()
                                        .append(temp.getIp())
                                        .append(",")
                                        .append(temp.getPort())
                                        .append(",")
                                        .append(temp.getUserName())
                                        .append(",")
                                        .append(temp.getPw())
                                        .append(",")
                                        .append(temp.getDataFormat())
                                        .append(",")
                                        .append(temp.getLatitude())
                                        .append(",")
                                        .append(temp.getLongitude())
                                        .append(",")
                                        .append(temp.getAltitude())
                                        .toString()
                                );
                            }else {
                                statement.setString(index++,
                                        new StringBuffer()
                                                .append(0)
                                                .append(",")
                                                .append(0)
                                                .append(",")
                                                .append(0)
                                                .append(",")
                                                .append(0)
                                                .append(",")
                                                .append(0)
                                                .toString()

                                );
                                statement.setString(index++,
                                        new StringBuilder()
                                                .append("0.0.0.0")
                                                .append(",")
                                                .append("0")
                                                .append(",")
                                                .append("null")
                                                .append(",")
                                                .append("null")
                                                .append(",")
                                                .append("null")
                                                .append(",")
                                                .append("0")
                                                .append(",")
                                                .append("0")
                                                .append(",")
                                                .append("0")
                                                .toString()
                                );
                            }
                        }
                        statement.setString(index,configTable.getHostId());
                        statement.execute();
                        int updateCount = statement.getUpdateCount();
                        if(updateCount>0){
                            mCallBack.onUpdateConfigTableSuccess();
                        }else {
                            //如果不见有配置表，这里可以申请新增一张。
                            mCallBack.onTargetConfigTableNotExist();
                        }
                    } catch (SQLException e) {
                        showLog("error while running updateConfigTable(final UserBean userBean, final ConfigTable configTable): "+e.getMessage());
                        mCallBack.onUpdateConfigTableError(e.getMessage());
                    }finally {
                        if(statement!=null){
                            try {
                                statement.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                statement=null;
                            }

                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    mCallBack.onSqlConnectionFail(e.getMessage());
                } finally {
                    if(con!=null){
                        try {
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        con=null;
                    }
                }
            }
        }).start();
    }

    public void createNewConfigTable(final UserBean userBean, final ConfigTable table){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection con=null;
                try {
                    con=getConnection(userBean);
                    PreparedStatement statement=null;
                    try {
                        String sql= genInsertCfTableSqlCommand();
                        statement = con.prepareStatement(sql);
                        statement.setString(1,table.getHostId());
                        statement.setInt(2,table.getSpecVer());
                        statement.setString(3, StaticData.OP_CODE_REBOOT);
                        statement.setString(4,table.getTheOwner());
                        statement.setDouble(5,table.getCenterFreq());
                        statement.setInt(6,table.getSignalAmp());
                        statement.setInt(7,table.getSignFill());
                        statement.setInt(8,table.getToneLeft());
                        statement.setInt(9,table.getToneRight());
                        int index=10;
                        for(int i=0;i<16;i++){
                            statement.setString(index++,"null");
                            statement.setString(index++,"null");
                        }
                        try {
                            statement.execute();
                            int updateCount = statement.getUpdateCount();
                            if(updateCount>0){
                                mCallBack.onCreateNewConfigTableSuccess(table);
                            }else {
                                mCallBack.onCreateNewConfigTableFails("创建配置表失败，原因：未知，请联系思凯微电子公司。");
                            }
                        }catch (SQLException e){
                            showLog("error while running createNewConfigTable(final UserBean userBean, final ConfigTable table): "+e.getMessage());
                            mCallBack.onCreateNewConfigTableFails(e.getMessage());
                        }

                    } catch (SQLException e) {
                        showLog("error while running createNewConfigTable(final UserBean userBean, final ConfigTable table): "+e.getMessage());
                        mCallBack.onCreateNewConfigTableFails(e.getMessage());
                    }finally {
                        if(statement!=null){
                            try {
                                statement.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            statement=null;
                        }
                    }
                } catch (SQLException e) {
                    showLog("error while running createNewConfigTable(final UserBean userBean, final ConfigTable table):"+e.getMessage());
                    mCallBack.onSqlConnectionFail(e.getMessage());
                }finally {
                    if(con!=null){
                        try {
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        con=null;
                    }
                }

            }
        }).start();
    }

    public void getLog(final UserBean user, final String hostId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection=null;
                try {
                    connection=getConnection(user);
                    if(connection!=null){
                        Statement statement = null;
                        try {
                            statement = connection.createStatement();
                            ResultSet resultSet=null;
                            try {
                                resultSet = statement.executeQuery("SELECT * FROM RecordTable WHERE HostId='" + hostId + "'");
                                ArrayList<com.skycaster.hellobase.bean.Log> list = obtainLogByResultSet(resultSet);
                                mCallBack.onObtainLog(list);
                            } catch (SQLException e) {
                                showLog("Error while executing getLog(final UserBean user, String hostId):"+e.getMessage());
                                mCallBack.onGetLogError(hostId,e.getMessage());
                            } finally {
                                if(resultSet!=null){
                                    try {
                                        resultSet.close();
                                    }catch (SQLException e){
                                        e.printStackTrace();
                                    }
                                    resultSet=null;
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }finally {
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
                } catch (SQLException e) {
                    showLog("Error while executing getLog(final UserBean user, String hostId):"+e.getMessage());
                    mCallBack.onSqlConnectionFail(e.getMessage());
                } finally {
                    if(connection!=null){
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        connection=null;
                    }
                }
            }
        }).start();
    }

    private ArrayList<com.skycaster.hellobase.bean.Log> obtainLogByResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<com.skycaster.hellobase.bean.Log> list=new ArrayList<>();
        try {
            while (resultSet.next()){
                com.skycaster.hellobase.bean.Log log=new com.skycaster.hellobase.bean.Log();
                log.setHostId(resultSet.getString("HostId"));
                log.setRecordTime(new Date(resultSet.getTimestamp("RecordTime").getTime()));
                log.setNotes(resultSet.getString("Notes"));
                list.add(log);
            }
        }catch (SQLException e){
            showLog("error while executing obtainLogByResultSet(ResultSet resultSet):+e.getMessage()");
            throw new SQLException(e.getMessage());
        }
        return list;
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

    private String genUpdateCfTableSqlCommand() {
        StringBuilder sb=new StringBuilder();
        sb.append("update ConfigTable set HostId=?,SpecVer=?,OpCode=?,TheOwner=?,center_freq=?,signal_amp=?,sign_fill=?,tone_index_left=?,tone_index_right=?,");
        for (int i=0;i<8;i++){
            sb.append("s").append(i).append("_phy_para=?,");
            sb.append("s").append(i).append("_cors_para=?");
            if(i<7){
                sb.append(",");
            }
        }
        sb.append(" where HostId=?");
        return sb.toString();
    }

    private String genInsertCfTableSqlCommand(){
        StringBuilder sb=new StringBuilder();
        sb.append("insert into ConfigTable (HostId,SpecVer,OpCode,TheOwner,center_freq,signal_amp,sign_fill,tone_index_left,tone_index_right,");
        for(int i=0;i<8;i++){
            sb.append("s").append(i).append("_phy_para=?,");
            sb.append("s").append(i).append("_cors_para=?");
            if(i<7){
                sb.append(",");
            }
        }
        sb.append(") values(");
        for (int i=0;i<25;i++){
            sb.append("?");
            if(i<24){
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    private Connection getConnection(UserBean userBean) throws SQLException {
        return DriverManager.getConnection(PREFIX+userBean.getHost()+"/"
                +userBean.getDataBaseName()+"?user="+userBean.getUserName()
                +"&password="+userBean.getPassword());
    }


    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }


}
