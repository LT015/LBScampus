package com.lbs.cheng.lbscampus.bean;

/** *
 * Created by LT on 2019/5/11.
 */

public class DepartmentDetailBean {

    private int departmentId;

    private String name;

    private String description;

    private String picturePath;

    private BuildingBean building;

    private UserDetailBean leader;

    private UserDetailBean superManager;

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

    public BuildingBean getBuilding() {
        return building;
    }

    public void setBuilding(BuildingBean building) {
        this.building = building;
    }

    public UserDetailBean getLeader() {
        return leader;
    }

    public void setLeader(UserDetailBean leader) {
        this.leader = leader;
    }

    public UserDetailBean getSuperManager() {
        return superManager;
    }

    public void setSuperManager(UserDetailBean superManager) {
        this.superManager = superManager;
    }

    public int getHigherDeptId() {
        return higherDeptId;
    }

    public void setHigherDeptId(int higherDeptId) {
        this.higherDeptId = higherDeptId;
    }
}
