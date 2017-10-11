package com.skycaster.hellobase.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.skycaster.hellobase.data.StaticData;

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
//    TheOwner      varchar(45), -- 运营商     新增选项
    private String theOwner;
    private int stateCode= StaticData.EXTRA_INT_NET_STATUS_MONITOR_CLOSE;

    public StateTable() {
    }


    public StateTable deepClone(){
        StateTable st=new StateTable();
        st.setNotes(notes);
        st.setRunningState(runningState);
        st.setDateTime(dateTime);
        st.setCurVer(curVer);
        st.setHostId(hostId);
        st.setStateCode(stateCode);
        st.setTheOwner(theOwner);
        return st;
    }



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

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public String getTheOwner() {
        return theOwner;
    }

    public void setTheOwner(String theOwner) {
        this.theOwner = theOwner;
    }


    @Override
    public String toString() {
        return "StateTable{" +
                "hostId='" + hostId + '\'' +
                ", curVer=" + curVer +
                ", runningState='" + runningState + '\'' +
                ", dateTime=" + dateTime +
                ", notes='" + notes + '\'' +
                ", theOwner='" + theOwner + '\'' +
                ", stateCode=" + stateCode +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.hostId);
        dest.writeInt(this.curVer);
        dest.writeString(this.runningState);
        dest.writeLong(this.dateTime);
        dest.writeString(this.notes);
        dest.writeString(this.theOwner);
        dest.writeInt(this.stateCode);
    }

    protected StateTable(Parcel in) {
        this.hostId = in.readString();
        this.curVer = in.readInt();
        this.runningState = in.readString();
        this.dateTime = in.readLong();
        this.notes = in.readString();
        this.theOwner = in.readString();
        this.stateCode = in.readInt();
    }

    public static final Creator<StateTable> CREATOR = new Creator<StateTable>() {
        @Override
        public StateTable createFromParcel(Parcel source) {
            return new StateTable(source);
        }

        @Override
        public StateTable[] newArray(int size) {
            return new StateTable[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateTable that = (StateTable) o;

        return theOwner != null ? theOwner.equals(that.theOwner) : that.theOwner == null;

    }

    @Override
    public int hashCode() {
        return theOwner != null ? theOwner.hashCode() : 0;
    }
}
