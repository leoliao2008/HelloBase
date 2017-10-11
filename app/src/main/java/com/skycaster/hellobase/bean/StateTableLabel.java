package com.skycaster.hellobase.bean;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/10/11.
 */

public class StateTableLabel {
    private String theOwner;
    private ArrayList<StateTable> mStateTables=new ArrayList<>();
    private int tableCount;

    public StateTableLabel(String title) {
        theOwner = title;
    }

    public String getTheOwner() {
        return theOwner;
    }

    public void setTheOwner(String theOwner) {
        this.theOwner = theOwner;
    }

    public ArrayList<StateTable> getStateTables() {
        return mStateTables;
    }

    public void setStateTables(ArrayList<StateTable> stateTables) {
        mStateTables.clear();
        mStateTables.addAll(stateTables);
        tableCount=mStateTables.size();
    }

    public void setTableCount(int tableCount) {
        this.tableCount = tableCount;
    }

    public int getTableCount() {
        return tableCount;
    }

    public boolean isContainTable(StateTable table){
        return mStateTables.contains(table);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateTableLabel label = (StateTableLabel) o;

        return theOwner != null ? theOwner.equals(label.theOwner) : label.theOwner == null;

    }

    @Override
    public int hashCode() {
        return theOwner != null ? theOwner.hashCode() : 0;
    }
}
