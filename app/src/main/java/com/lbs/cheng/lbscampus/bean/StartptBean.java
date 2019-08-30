package com.lbs.cheng.lbscampus.bean;

import com.baidu.mapapi.model.LatLng;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by cheng on 2019/2/21.
 */

public class StartptBean extends DataSupport implements Serializable {
    LatLng startPt;

    public LatLng getStartPt() {
        return startPt;
    }

    public void setStartPt(LatLng startPt) {
        this.startPt = startPt;
    }
}
