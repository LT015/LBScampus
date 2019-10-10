package com.lt.common.util;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by cheng on 2019/2/21.
 */

public class LocationUtil {
    static LatLng myLocation;
    public static void setMyLocation( LatLng latLng){
        myLocation = latLng;
    }
    public static LatLng getMyLocation(){
        return myLocation;
    }
}
