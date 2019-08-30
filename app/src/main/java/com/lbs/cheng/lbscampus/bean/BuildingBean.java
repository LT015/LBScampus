package com.lbs.cheng.lbscampus.bean;

import com.baidu.mapapi.model.LatLng;

/** *
 * Created by cheng on 2019/2/21.
 */

public class BuildingBean {
    private int buildingId;

    private String name;

    private double longitude;

    private double latitude;

    private String picturePath;

    private String description;

    private int type;
    //LatLng buidingPt=new LatLng(39.066082,117.289044);


    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
