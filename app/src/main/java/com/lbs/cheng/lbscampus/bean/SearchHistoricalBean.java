package com.lbs.cheng.lbscampus.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by LT on 2019/3/13.
 */

public class SearchHistoricalBean extends DataSupport {
    private int id;

    private int type;//1是建筑物搜索 2是部门 3是人 4是综合搜索 5是公告搜索

    private String name;

    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
