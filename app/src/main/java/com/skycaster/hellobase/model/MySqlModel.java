package com.skycaster.hellobase.model;

import android.text.TextUtils;
import android.util.Log;

import com.skycaster.hellobase.bean.ConfigTable;
import com.skycaster.hellobase.bean.ReferentialStation;
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

    public void connectMySql(final String host, final String userName, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection con;
                try {
                    con=getConnection(new UserBean(userName,password,host,StaticData.DATA_BASE_NAME_CLIENT,null));
                    showLog("连接成功！");
                    mCallBack.onGetSqlConnection(con);
                } catch (SQLException e) {
                    showLog(e.getMessage());
                    mCallBack.onSqlConnectionFail(e.getMessage());
                }
            }
        }).start();
    }

    public void getUserToken(final Connection con){
//        select TABLE_NAME from  information_schema.VIEWS where TABLE_SCHEMA = 'state_views_db'
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Statement statement = con.createStatement();
                    showLog("exe cmd to get token...");
                    ResultSet resultSet = statement.executeQuery("select TABLE_NAME from information_schema.VIEWS where TABLE_SCHEMA = 'state_views_db'");
                    ArrayList<String> list=new ArrayList<>();
                    while (resultSet.next()){
                        list.add(resultSet.getString("TABLE_NAME"));
                    }
                    showLog("tokens get:"+list.toString());
                    mCallBack.onGetPermissionTokens(list);
                } catch (SQLException e) {
                    e.printStackTrace();
                    showLog("Fail to get tokes :"+e.getMessage());
                    mCallBack.onGetPermissionTokensFail(e.getMessage());
                }
            }
        }).start();
    }

    public void getStateTables(final Connection con, final ArrayList<String> tokens){
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (MySqlModel.this){
                    Statement statement=null;
                    ResultSet resultSet=null;
                    try {
                        statement =con.createStatement();
                        if(tokens.size()==1){
                            resultSet=statement.executeQuery("SELECT * FROM state_views_db."+tokens.get(0));
                        }else {
                            StringBuilder sb=new StringBuilder();
//                            select * from ( (select * from  state_views_db.武汉 )UNION (select * from  state_views_db.深圳)) as abc
                            sb.append("select * from ((select * from  state_views_db.");
                            for(int i=0;i<tokens.size();i++){
                                sb.append(tokens.get(i));
                                if(i!=tokens.size()-1){
                                    sb.append(")UNION (select * from  state_views_db.");
                                }
                            }
                            sb.append(")) as abc");
                            resultSet=statement.executeQuery(sb.toString());
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

    public void getConfigTable(final UserBean userBean, final String hostId){
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
                        ArrayList<String> tokens = userBean.getTokens();
//                        select * from  config_views_db.武汉
                        if(tokens.size()==0){
                            mCallBack.onGetConfigTableError("user is not authorized to access config table.");
                            return;
                        }
                        StringBuilder sb=new StringBuilder();
                        if(tokens.size()==1){
                            sb.append("select * from  config_views_db.").append(tokens.get(0)).append(" where HostId = '").append(hostId).append("'");
                        }else {
//                            select * from ( (select * from  config_views_db.武汉 where hostid = '' )UNION (select * from  config_views_db.深圳)) as abc
                            sb.append("select * from ( (select * from  config_views_db.");
                            for(int i=0;i<tokens.size();i++){
                                sb.append(tokens.get(i)).append(" where HostId = '").append(hostId).append("'");
                                if(i!=tokens.size()-1){
                                    sb.append(" )UNION (select * from  config_views_db.");
                                }
                            }
                            sb.append(")) as efg");
                        }
                        resultSet=statement.executeQuery(sb.toString());
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
                        String sql= getUpdateConfigTableCmd(getConfigTableNameByHostId(con,userBean.getTokens(),configTable.getHostId()));
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
                        int size = configTable.getStations().size();
                        for (int i=0;i<8;i++){
                            if(i<size){
//                                #'form_code, ldpc_num, ldpc_rate, intv_size, qam_type'
                                ReferentialStation temp = configTable.getStations().get(i);
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
//                                #'服务器IP, 端口号, 用户名, 密码, 数据格式, 纬度,经度， 高度'
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
                        String sql= sqlCmdInsertConfigTable(getConfigTableNameByHostId(con,userBean.getTokens(),table.getHostId()));
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
//                                SELECT * FROM record_views_db.views_record where HostId = 'b827eb14f70d'
                                resultSet = statement.executeQuery("SELECT * FROM record_views_db.views_record WHERE HostId='" + hostId + "'");
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
                log.setNotes(resultSet.getString("Note"));
                list.add(log);
            }

            showLog("Logs size ="+list.size()+","+list.toString());
        }catch (SQLException e){
            showLog("error while executing obtainLogByResultSet(ResultSet resultSet):"+e.getMessage());
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
                tb.setStations(getReferStationsByResultSet(resultSet));
                list.add(tb);
            }
        } catch (SQLException e) {
            showLog("error during getConfigTableByResultSet(ResultSet resultSet) :"+e.getMessage());
            throw e;
        }
        return list;
    }

    public String getConfigTableNameByHostId(Connection con,ArrayList<String> tokens,String hostId) {
        String tableName=null;
        for(String token:tokens){
//            select * from  config_views_db.武汉
            ResultSet resultSet=null;
            try {
                resultSet = con.createStatement().executeQuery("select 1 from  config_views_db." + token + " where HostId = '" + hostId + "'");
                if(resultSet.next()){
                    tableName="config_views_db." + token;
                    showLog("table name is found "+tableName);
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tableName;
    }

    private String getUpdateConfigTableCmd(String tableName) {
        StringBuilder sb=new StringBuilder();
        sb.append("update ")
          .append(tableName)
          .append(" set HostId=?,SpecVer=?,OpCode=?,TheOwner=?,center_freq=?,signal_amp=?,sign_fill=?,tone_index_left=?,tone_index_right=?,");
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

    private String sqlCmdInsertConfigTable(String tableName){
        StringBuilder sb=new StringBuilder();
        sb.append("insert into ").append(tableName).append(" (HostId,SpecVer,OpCode,TheOwner,center_freq,signal_amp,sign_fill,tone_index_left,tone_index_right,");
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
        //"jdbc:mysql://168.121.121.12:9527/state_views_db?user=admin&password=123456"
        return DriverManager.getConnection(PREFIX+userBean.getHost()+"/"
                +userBean.getDataBaseName()+"?user="+userBean.getUserName()
                +"&password="+userBean.getPassword());
    }


    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }

    /**
     * 获取基站清单
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private ArrayList<ReferentialStation> getReferStationsByResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<ReferentialStation> list=new ArrayList<>();
        int id=0;
        try {
            while (id<8){
                ReferentialStation station=new ReferentialStation();
                station.setId(id);
                String[] phyParas = getPhyParas(resultSet.getString("s" + id + "_phy_para"));
                if(phyParas.length==5){
                    station.setFormCode(Integer.parseInt(phyParas[0]));
                    station.setLdpcNum(Integer.parseInt(phyParas[1]));
                    station.setLdpcRate(Integer.parseInt(phyParas[2]));
                    station.setIntvSize(Integer.parseInt(phyParas[3]));
                    station.setQamType(Integer.parseInt(phyParas[4]));
                }
                String s = resultSet.getString("s" + id + "_cors_para");
                showLog("字符串："+s);
                String[] corsParas = getCorsParas(resultSet.getString("s" + id + "_cors_para"));
//                for (String temp:corsParas){
//                    showLog(temp);
//                }
//              '服务器IP, 端口号, 用户名, 密码, 数据格式, 纬度,经度， 高度'
                if(corsParas.length==8){
                    station.setIp(corsParas[0]);
                    station.setPort(corsParas[1]);
                    station.setUserName(corsParas[2]);
                    station.setPw(corsParas[3]);
                    station.setDataFormat(corsParas[4]);
                    station.setLatitude(corsParas[5]);
                    station.setLongitude(corsParas[6]);
                    station.setAltitude(corsParas[7]);
                }
                list.add(station);
                id++;
            }
        } catch (SQLException e) {
            showLog("error during getReferStationsByResultSet(ResultSet resultSet) "+e.getMessage());
            throw e;
        }
        return list;
    }

    private String[] getPhyParas(String phyParas) {
        String[] result=new String[5];
        for(int i=0,len=result.length;i<len;i++){
            result[i]="0";
        }
        String[] split = phyParas.split(Pattern.quote(","));
        for(int i=0,len=split.length;i<len;i++){
            String temp = split[i];
            if(!TextUtils.isEmpty(temp)){
                result[i]=split[i];
            }
        }
        return result;
    }

    private String[] getCorsParas(String corsParas){
//        '服务器IP, 端口号, 用户名, 密码, 数据格式, 纬度,经度， 高度'
        String[] result=new String[8];
        for(int i=0,len=result.length;i<len;i++){
            result[i]="";
        }
        String[] split = corsParas.split(Pattern.quote(","));
        for(int i=0,len=split.length;i<len;i++){
            String temp = split[i];
            if(!TextUtils.isEmpty(temp)){
                result[i]=split[i];
            }
        }
        return result;
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


}
