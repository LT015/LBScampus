package com.lbs.cheng.lbscampus.bean;

/** *
 * Created by LT on 2019/5/10.
 */

public class PositionBean {

    private String userId;

    private double longitude;

    private double latitude;//纬度

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

}
