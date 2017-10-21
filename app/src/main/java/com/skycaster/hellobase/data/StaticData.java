package com.skycaster.hellobase.data;

import com.skycaster.hellobase.R;

/**
 * Created by 廖华凯 on 2017/9/13.
 */

public interface StaticData {
//    String HOST_ADDRESS="183.56.182.140:9527";
    String DATA_BASE_NAME="KinHoodCloud";
//    String USER_NAME="admin";
//    String PASSWORD="skycaster";
    String EXTRA_DATA_STATE_TABLE="EXTRA_DATA_STATE_TABLE";
    String ACTION_SEVER_CON_STATUS_MONITOR ="ACTION_SEVER_CON_STATUS_MONITOR" ;
    String EXTRA_INT_EVENT_TYPE="EXTRA_INT_EVENT_TYPE";
    int EVENT_TYPE_NET_STATUS=1;
    int EVENT_TYPE_SERVICE_DISMISS=2;
    String EXTRA_BOOLEAN_IS_SUCCESS = "EXTRA_BOOLEAN_IS_SUCCESS";
    String EXTRA_STRING_INFO = "EXTRA_STRING_INFO";
    String ACTION_STOP_SERVICE ="ACTION_STOP_SERVICE";
    String EXTRA_BOOLEAN_IS_SERVICE_RUNNING = "EXTRA_BOOLEAN_IS_SERVICE_RUNNING";
    String EXTRA_INT_NET_STATUS_CODE = "EXTRA_INT_NET_STATUS_CODE";
    int EXTRA_INT_NET_STATUS_NORMAL = 0;
    int EXTRA_INT_NET_STATUS_UNSTABLE = 1;
    int EXTRA_INT_NET_STATUS_ERROR = 2;
    int EXTRA_INT_NET_STATUS_MONITOR_CLOSE = 3;
    int EXTRA_INT_NET_STATUS_INITIALIZING=4;
    int EXTRA_INT_NET_STATUS_LINK_FAILED = 5;
    int EXTRA_INT_NET_STATUS_TABLE_FAILED=6;
    String SP_NAME="Config";
    String SP_IP = "SP_IP";
    String SP_PORT = "SP_PORT";
    String SP_IS_KEEP_INPUT = "SP_IS_KEEP_INPUT";
    String SP_IS_SHOW_PW = "SP_IS_SHOW_PW";
    String SP_PW = "SP_PW";
    String SP_USER_NAME = "SP_USER_NAME";
    String EXTRA_DATA_CONFIG_TABLE = "EXTRA_DATA_CONFIG_TABLE";
    int REQUEST_CODE_EDIT_CONFIG_TABLE = 357;
    int RESULT_CODE_EDIT_CONFIG_TABLE_OK = 753;
    int RESULT_CODE_EDIT_CONFIG_TABLE_FAIL = 735;
    String OP_CODE_REBOOT = "reboot";
    String OP_CODE_RECONFIG = "reconfig";
    int[] STATE_SET_NORMAL=new int[]{
            R.attr.net_status_normal,
            -R.attr.net_status_initializing,
            -R.attr.net_status_link_fail,
            -R.attr.net_status_no_update,
            -R.attr.net_status_stopped,
            -R.attr.net_status_unstable};
    int[] STATE_SET_INITIALIZING=new int[]{
            -R.attr.net_status_normal,
            R.attr.net_status_initializing,
            -R.attr.net_status_link_fail,
            -R.attr.net_status_no_update,
            -R.attr.net_status_stopped,
            -R.attr.net_status_unstable};
    int[] STATE_SET_LINK_FAIL=new int[]{
            -R.attr.net_status_normal,
            -R.attr.net_status_initializing,
            R.attr.net_status_link_fail,
            -R.attr.net_status_no_update,
            -R.attr.net_status_stopped,
            -R.attr.net_status_unstable};
    int[] STATE_SET_NO_UPDATE=new int[]{
            -R.attr.net_status_normal,
            -R.attr.net_status_initializing,
            -R.attr.net_status_link_fail,
            R.attr.net_status_no_update,
            -R.attr.net_status_stopped,
            -R.attr.net_status_unstable};
    int[] STATE_SET_STOPPED=new int[]{
            -R.attr.net_status_normal,
            -R.attr.net_status_initializing,
            -R.attr.net_status_link_fail,
            -R.attr.net_status_no_update,
            R.attr.net_status_stopped,
            -R.attr.net_status_unstable};
    int[] STATE_SET_UNSTABLE=new int[]{
            -R.attr.net_status_normal,
            -R.attr.net_status_initializing,
            -R.attr.net_status_link_fail,
            -R.attr.net_status_no_update,
            -R.attr.net_status_stopped,
            R.attr.net_status_unstable};

    String EXTRA_LOGS = "EXTRA_LOGS";
    String EXTRA_STRING_HOST_ID = "EXTRA_STRING_HOST_ID";
    String DATE_START = "DATE_START";
    String DATE_END="DATE_END";
    String OP_CODE_POWER_OFF = "shutdown";
}
