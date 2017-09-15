package com.skycaster.hellobase.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 廖华凯 on 2017/9/13.
 */

public class StateTable implements Parcelable{
//    HostId        varchar(45) primary key , -- 主机ID(MAC地址)
//    CurVer        varchar(45) not null, -- 当前版本号
//    RunningState  varchar(45), -- 运行状态
//    FeedbackTime  datetime, -- 反馈时间
//    Notes         text default null -- 备注
    private String hostId;
    private int curVer;
    private String runningState;
    private long dateTime;
    private String notes;

    public StateTable() {
    }

    protected StateTable(Parcel in) {
        hostId = in.readString();
        curVer = in.readInt();
        runningState = in.readString();
        dateTime=in.readLong();
        notes = in.readString();
    }

    public StateTable deepClone(){
        StateTable st=new StateTable();
        st.setNotes(notes);
        st.setRunningState(runningState);
        st.setDateTime(dateTime);
        st.setCurVer(curVer);
        st.setHostId(hostId);
        return st;
    }

    public static final Creator<StateTable> CREATOR = new Creator<StateTable>() {
        @Override
        public StateTable createFromParcel(Parcel in) {
            return new StateTable(in);
        }

        @Override
        public StateTable[] newArray(int size) {
            return new StateTable[size];
        }
    };

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public int getCurVer() {
        return curVer;
    }

    public void setCurVer(int curVer) {
        this.curVer = curVer;
    }

    public String getRunningState() {
        return runningState;
    }

    public void setRunningState(String runningState) {
        this.runningState = runningState;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hostId);
        dest.writeInt(curVer);
        dest.writeString(runningState);
        dest.writeLong(dateTime);
        dest.writeString(notes);
    }

    @Override
    public String toString() {
        return "StateTable{" +
                "hostId='" + hostId + '\'' +
                ", curVer='" + curVer + '\'' +
                ", runningState='" + runningState + '\'' +
                ", dateTime=" + dateTime +
                ", notes='" + notes + '\'' +
                '}';
    }
}
