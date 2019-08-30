package com.lbs.cheng.lbscampus.bean;

/**
 * Created by LT on 2019/5/11.
 */

public class DepartmentBean {

    private int departmentId;

    private String name;

    private String description;

    private String picturePath;

    private int buildingId;

    private String leaderId;

    private String superManagerId;

    private int higherDeptId;

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public String getSuperManagerId() {
        return superManagerId;
    }

    public void setSuperManagerId(String superManagerId) {
        this.superManagerId = superManagerId;
    }

    public int getHigherDeptId() {
        return higherDeptId;
    }

    public void setHigherDeptId(int higherDeptId) {
        this.higherDeptId = higherDeptId;
    }
}
