package com.skycaster.hellobase.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by 廖华凯 on 2017/10/13.
 */

public class Log implements Parcelable {
//    HostId        varchar(45),
//    RecordTime    datetime,
//    Notes         varchar default null,
//    primary key(HostId,RecordTime)
    private String mHostId;
    private Date mRecordTime;
    private String mNotes;

    public String getHostId() {
        return mHostId;
    }

    public void setHostId(String hostId) {
        mHostId = hostId;
    }

    public Date getRecordTime() {
        return mRecordTime;
    }

    public void setRecordTime(Date recordTime) {
        mRecordTime = recordTime;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mHostId);
        dest.writeLong(this.mRecordTime != null ? this.mRecordTime.getTime() : -1);
        dest.writeString(this.mNotes);
    }

    public Log() {
    }

    protected Log(Parcel in) {
        this.mHostId = in.readString();
        long tmpMRecordTime = in.readLong();
        this.mRecordTime = tmpMRecordTime == -1 ? null : new Date(tmpMRecordTime);
        this.mNotes = in.readString();
    }

    public static final Creator<Log> CREATOR = new Creator<Log>() {
        @Override
        public Log createFromParcel(Parcel source) {
            return new Log(source);
        }

        @Override
        public Log[] newArray(int size) {
            return new Log[size];
        }
    };
}
