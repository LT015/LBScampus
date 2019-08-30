package com.lbs.cheng.lbscampus.util;

import com.baidu.mapapi.model.LatLng;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

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
