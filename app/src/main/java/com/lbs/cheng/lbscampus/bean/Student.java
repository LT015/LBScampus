package com.lbs.cheng.lbscampus.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;


public class Student extends DataSupport implements Serializable {

    private String studentId;

    private String userId;

    private int classId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }
}