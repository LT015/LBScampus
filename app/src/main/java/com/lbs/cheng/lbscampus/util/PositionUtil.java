package com.lbs.cheng.lbscampus.util;

import com.baidu.mapapi.model.LatLng;

import java.math.BigDecimal;

/**
 * Created by cheng on 2018/11/24.
 */

public class PositionUtil {
    private static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
    static double dataDigit(int digit, double in) {
        return new BigDecimal(in).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();

    }
    /**
     * 将火星坐标转变成百度坐标
     *
     * @param lngLat_gd 火星坐标（高德、腾讯地图坐标等）
     * @return 百度坐标
     */

    public static LatLng gaode_to_baidu(LatLng lngLat_gd) {
        double x = lngLat_gd.longitude;
        double y = lngLat_gd.latitude;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        return new LatLng(dataDigit(6, z * Math.sin(theta) + 0.006), dataDigit(6, z * Math.cos(theta) + 0.0065));

    }

    /**
     * 将百度坐标转变成火星坐标
     *
     * @param lngLat_bd 百度坐标（百度地图坐标）
     * @return 火星坐标(高德、腾讯地图等)
     */
    public static LatLng baidu_to_gaode(LatLng lngLat_bd) {
        double x = lngLat_bd.longitude - 0.0065, y = lngLat_bd.latitude - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        return new LatLng(dataDigit(6, z * Math.sin(theta)), dataDigit(6, z * Math.cos(theta)));
    }
}
