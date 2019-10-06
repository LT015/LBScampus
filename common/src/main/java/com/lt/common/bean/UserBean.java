package com.lt.common.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

/** *
 * Created by cheng on 2018/11/24.
 */

public class UserBean extends DataSupport implements Serializable{

    private String userId;

    private String userName;

    private String nickName;

    private String password;

    private int status;

    private int type;

    private String sex;

    private String idNumber;

    private String telNumber;

    private String email;

    private String userImage;

    //private String portraitPath;

    private int isValid;

    private List<Integer> hobbyList;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }


    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public List<Integer> getHobbyList() {
        return hobbyList;
    }

    public void setHobbyList(List<Integer> hobbyList) {
        this.hobbyList = hobbyList;
    }


}
