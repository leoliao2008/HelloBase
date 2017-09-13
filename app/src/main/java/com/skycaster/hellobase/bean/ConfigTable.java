package com.skycaster.hellobase.bean;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/9/12.
 */

public class ConfigTable {
    private String hostId="null";
    private int specVer;
    private String opCode="null";
    private String theOwner="null";
    private double centerFreq=0;
    private int signalAmp=0;
    private int signFill=0;
    private int toneLeft=0;
    private int toneRight=0;
    private ArrayList<ServiceBase> mServiceBases;


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

    public void setServiceBases(ArrayList<ServiceBase> serviceBases) {
        mServiceBases = serviceBases;
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

    public ArrayList<ServiceBase> getServiceBases() {
        return mServiceBases;
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
                ", mServiceBases=" + mServiceBases +
                '}';
    }
}
