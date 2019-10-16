package com.example.kcb.bean;

import java.io.Serializable;

public class ClassBean implements Serializable {

    private int classId;

    private String className;

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
