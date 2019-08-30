package com.lbs.cheng.lbscampus.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by LT on 2019/3/13.
 */

public class SearchHistoricalBean extends DataSupport {
    private int id;
    private String name;
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
