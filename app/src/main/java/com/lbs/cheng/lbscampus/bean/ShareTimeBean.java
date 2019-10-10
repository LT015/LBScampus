package com.lbs.cheng.lbscampus.bean;

import org.litepal.crud.DataSupport;

import java.sql.Time;
import java.util.Date;

/**
 * Created by LT on 2019/5/12.
 */

public class ShareTimeBean extends DataSupport {
    private int id;

    private String userId;

    private String startTime;

    private String endTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
