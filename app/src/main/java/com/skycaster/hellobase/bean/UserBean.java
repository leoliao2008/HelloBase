package com.skycaster.hellobase.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/10/10.
 */

public class UserBean implements Parcelable {
    private String userName;
    private String password;
    private String host;
    private String dataBaseName;
    private ArrayList<String> tokens;

    public UserBean() {
    }

    public UserBean(String userName, String password, String host, String dataBaseName, ArrayList<String> tokens) {
        this.userName = userName;
        this.password = password;
        this.host = host;
        this.dataBaseName = dataBaseName;
        this.tokens = tokens;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public void setTokens(ArrayList<String> tokens) {
        this.tokens = tokens;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.password);
        dest.writeString(this.host);
        dest.writeString(this.dataBaseName);
        dest.writeStringList(this.tokens);
    }

    protected UserBean(Parcel in) {
        this.userName = in.readString();
        this.password = in.readString();
        this.host = in.readString();
        this.dataBaseName = in.readString();
        this.tokens = in.createStringArrayList();
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel source) {
            return new UserBean(source);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };
}
