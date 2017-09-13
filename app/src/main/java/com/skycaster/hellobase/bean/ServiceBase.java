package com.skycaster.hellobase.bean;

/**
 * Created by 廖华凯 on 2017/9/12.
 */

public class ServiceBase {
    private int id;
//    s0_form_code	  integer not null default 33,-- 此项不用翻译
//    s0_ldpc_num		  integer not null default 15, -- 业务0 LDCP码字个数
//    s0_ldpc_rate	  integer not null default 2,  -- 业务0 LDPC码率
//    s0_intv_size	  integer not null default 8,  -- 业务0 交织块大小
//    s0_qam_type		  integer not null default 0,  -- 业务0 调制类型
    private int formCode;
    private int ldpcNum;
    private int ldpcRate;
    private int intvSize;
    private int qamType;

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
}
