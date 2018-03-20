package com.skycaster.hellobase.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/9/12.
 */

public class ConfigTable implements Parcelable{
    private String hostId="null";
    private int specVer;
    private String opCode="null";
    private String theOwner="null";
    private double centerFreq=0;
    private int signalAmp=0;
    private int signFill=0;
    private int toneLeft=0;
    private int toneRight=0;
    private ArrayList<ReferentialStation> mStations =new ArrayList<>();

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setSpecVer(int specVer) {
        this.specVer = specVer;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public void setTheOwner(String theOwner) {
        this.theOwner = theOwner;
    }

    public void setCenterFreq(double centerFreq) {
        this.centerFreq = centerFreq;
    }

    public void setSignalAmp(int signalAmp) {
        this.signalAmp = signalAmp;
    }

    public void setSignFill(int signFill) {
        this.signFill = signFill;
    }

    public void setToneLeft(int toneLeft) {
        this.toneLeft = toneLeft;
    }

    public void setToneRight(int toneRight) {
        this.toneRight = toneRight;
    }

    public void setStations(ArrayList<ReferentialStation> stations) {
        mStations.clear();
        for (ReferentialStation temp: stations){
            mStations.add(temp.deepClone());
        }
    }

    public String getHostId() {
        return hostId;
    }

    public int getSpecVer() {
        return specVer;
    }

    public String getOpCode() {
        return opCode;
    }

    public String getTheOwner() {
        return theOwner;
    }

    public double getCenterFreq() {
        return centerFreq;
    }

    public int getSignalAmp() {
        return signalAmp;
    }

    public int getSignFill() {
        return signFill;
    }

    public int getToneLeft() {
        return toneLeft;
    }

    public int getToneRight() {
        return toneRight;
    }

    public ArrayList<ReferentialStation> getStations() {
        return mStations;
    }

    @Override
    public String toString() {
        return "ConfigTable{" +
                "hostId='" + hostId + '\'' +
                ", specVer='" + specVer + '\'' +
                ", opCode=" + opCode +
                ", theOwner='" + theOwner + '\'' +
                ", centerFreq=" + centerFreq +
                ", signalAmp=" + signalAmp +
                ", signFill=" + signFill +
                ", toneLeft=" + toneLeft +
                ", toneRight=" + toneRight +
                ", mStations=" + mStations +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.hostId);
        dest.writeInt(this.specVer);
        dest.writeString(this.opCode);
        dest.writeString(this.theOwner);
        dest.writeDouble(this.centerFreq);
        dest.writeInt(this.signalAmp);
        dest.writeInt(this.signFill);
        dest.writeInt(this.toneLeft);
        dest.writeInt(this.toneRight);
        dest.writeTypedList(this.mStations);
    }

    public ConfigTable() {}

    protected ConfigTable(Parcel in) {
        this.hostId = in.readString();
        this.specVer = in.readInt();
        this.opCode = in.readString();
        this.theOwner = in.readString();
        this.centerFreq = in.readDouble();
        this.signalAmp = in.readInt();
        this.signFill = in.readInt();
        this.toneLeft = in.readInt();
        this.toneRight = in.readInt();
        this.mStations = in.createTypedArrayList(ReferentialStation.CREATOR);
    }

    public static final Creator<ConfigTable> CREATOR = new Creator<ConfigTable>() {
        @Override
        public ConfigTable createFromParcel(Parcel source) {
            return new ConfigTable(source);
        }

        @Override
        public ConfigTable[] newArray(int size) {
            return new ConfigTable[size];
        }
    };

    public ConfigTable deepClone(){
        ConfigTable tb=new ConfigTable();
        tb.setSpecVer(specVer);
        tb.setOpCode(opCode);
        tb.setHostId(hostId);
        tb.setCenterFreq(centerFreq);
        tb.setSignFill(signFill);
        tb.setSignalAmp(signalAmp);
        tb.setTheOwner(theOwner);
        tb.setToneLeft(toneLeft);
        tb.setToneRight(toneRight);
        tb.setStations(mStations);
        return tb;
    }
}
