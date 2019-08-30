package com.lbs.cheng.lbscampus.util;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.lbs.cheng.lbscampus.bean.BuildingBean;
import com.lbs.cheng.lbscampus.bean.DepartmentDetailBean;
import com.lbs.cheng.lbscampus.bean.UserDetailBean;
import com.lbs.cheng.lbscampus.bean.SectionBean;

/**
 * Created by cheng on 2019/4/13.
 */

public class SearchItem  implements MultiItemEntity {
    public static final int building = 1;
    public static final int section = 2;
    public static final int people = 3;
    private int itemType;

    BuildingBean buildingBean;
    DepartmentDetailBean sectionBean;
    UserDetailBean peopleBean;

    public SearchItem(UserDetailBean people, int itemType) {
        peopleBean = people;
        this.itemType = itemType;
    }
    public SearchItem(BuildingBean build, int itemType) {
        buildingBean = build;
        this.itemType = itemType;
    }
    public SearchItem(DepartmentDetailBean section, int itemType) {
        sectionBean = section;
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}