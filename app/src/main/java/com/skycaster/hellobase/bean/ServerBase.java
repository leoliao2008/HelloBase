package com.skycaster.hellobase.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 廖华凯 on 2017/9/12.
 */

public class ServerBase implements Parcelable {
    private int id=0;
//    s0_form_code	  integer not null default 33,-- 此项不用翻译
//    s0_ldpc_num		  integer not null default 15, -- 业务0 LDCP码字个数
//    s0_ldpc_rate	  integer not null default 2,  -- 业务0 LDPC码率
//    s0_intv_size	  integer not null default 8,  -- 业务0 交织块大小
//    s0_qam_type		  integer not null default 0,  -- 业务0 调制类型
    private int formCode=0;
    private int ldpcNum=0;
    private int ldpcRate=0;
    private int intvSize=0;
    private int qamType=0;
//    '服务器IP, 端口号, 用户名, 密码, 数据格式, 经度, 纬度, 高度'      ----新增属性
    private String ip="";
    private String port="";
    private String userName="";
    private String pw="";
    private String dataFormat="";
    private String latitude="";
    private String longitude="";
    private String altitude ="";


    public ServerBase() {
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setFormCode(int formCode) {
        this.formCode = formCode;
    }

    public void setLdpcNum(int ldpcNum) {
        this.ldpcNum = ldpcNum;
    }

    public void setLdpcRate(int ldpcRate) {
        this.ldpcRate = ldpcRate;
    }

    public void setIntvSize(int intvSize) {
        this.intvSize = intvSize;
    }

    public void setQamType(int qamType) {
        this.qamType = qamType;
    }

    public int getId() {
        return id;
    }

    public int getFormCode() {
        return formCode;
    }

    public int getLdpcNum() {
        return ldpcNum;
    }

    public int getLdpcRate() {
        return ldpcRate;
    }

    public int getIntvSize() {
        return intvSize;
    }

    public int getQamType() {
        return qamType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    /**
     * 深度克隆，声明一个新的对象，把当前的属性复制到新的对象中。
     * @return 一个新的对象，属性跟当前这个对象一样，但地址是全新的。
     */
    public ServerBase deepClone(){
        ServerBase sb=new ServerBase();
        sb.setId(id);
        sb.setLdpcNum(ldpcNum);
        sb.setFormCode(formCode);
        sb.setIntvSize(intvSize);
        sb.setQamType(qamType);
        sb.setLdpcRate(ldpcRate);
        sb.setIp(ip);
        sb.setPort(port);
        sb.setDataFormat(dataFormat);
        sb.setUserName(userName);
        sb.setPw(pw);
        sb.setLatitude(latitude);
        sb.setLongitude(longitude);
        sb.setAltitude(altitude);
        return sb;
    }

    /**
     * 浅度克隆，只是把属性的值赋给目标对象，目标对象的地址还是原来那个。
     * @param sb 目标对象
     */
    public void lightClone(ServerBase sb){
        sb.setId(id);
        sb.setLdpcNum(ldpcNum);
        sb.setFormCode(formCode);
        sb.setIntvSize(intvSize);
        sb.setQamType(qamType);
        sb.setLdpcRate(ldpcRate);
        sb.setIp(ip);
        sb.setPort(port);
        sb.setDataFormat(dataFormat);
        sb.setUserName(userName);
        sb.setPw(pw);
        sb.setLatitude(latitude);
        sb.setLongitude(longitude);
        sb.setAltitude(altitude);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.formCode);
        dest.writeInt(this.ldpcNum);
        dest.writeInt(this.ldpcRate);
        dest.writeInt(this.intvSize);
        dest.writeInt(this.qamType);
        dest.writeString(this.ip);
        dest.writeString(this.port);
        dest.writeString(this.userName);
        dest.writeString(this.pw);
        dest.writeString(this.dataFormat);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeString(this.altitude);
    }

    protected ServerBase(Parcel in) {
        this.id = in.readInt();
        this.formCode = in.readInt();
        this.ldpcNum = in.readInt();
        this.ldpcRate = in.readInt();
        this.intvSize = in.readInt();
        this.qamType = in.readInt();
        this.ip = in.readString();
        this.port = in.readString();
        this.userName = in.readString();
        this.pw = in.readString();
        this.dataFormat = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.altitude = in.readString();
    }

    public static final Creator<ServerBase> CREATOR = new Creator<ServerBase>() {
        @Override
        public ServerBase createFromParcel(Parcel source) {
            return new ServerBase(source);
        }

        @Override
        public ServerBase[] newArray(int size) {
            return new ServerBase[size];
        }
    };
}
