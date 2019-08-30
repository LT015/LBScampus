package com.lbs.cheng.lbscampus.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by LT on 2019/5/12.
 */

public class ShareTimeBean extends DataSupport {
    private int id;

    private String userId;

    private long startTime;

    private long endTime;

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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
