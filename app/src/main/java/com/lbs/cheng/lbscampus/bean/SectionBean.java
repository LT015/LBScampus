package com.lbs.cheng.lbscampus.bean;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by cheng on 2019/4/12.
 */

public class SectionBean {
    String name;
    String image;
    String discription;
    LatLng buidingPt=new LatLng(39.066082,117.289044);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public LatLng getBuidingPt() {
        return buidingPt;
    }

    public void setBuidingPt(LatLng buidingPt) {
        this.buidingPt = buidingPt;
    }
}
