package com.lbs.cheng.lbscampus.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class LoginInfo extends DataSupport implements Serializable {

    private int type;

    private String num;

    private String password;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
