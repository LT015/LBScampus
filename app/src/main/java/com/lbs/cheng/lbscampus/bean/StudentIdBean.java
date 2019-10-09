package com.lbs.cheng.lbscampus.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class StudentIdBean extends DataSupport implements Serializable {

    private String studentId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
