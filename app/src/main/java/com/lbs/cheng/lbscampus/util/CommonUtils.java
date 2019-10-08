package com.lbs.cheng.lbscampus.util;

/**
 * Created by LT on 2019/3/16.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommonUtils {

    public static String GenerateUUID() {
        UUID uuid = UUID.randomUUID();
        String Uuid;
        Uuid = uuid.toString().replace('-', '_');
        return Uuid;
    }

    public static int noticeTypeId = 1;
    public static int buildingTypeId = 0;

    public static int noticeEditType = 0;//0是创建公告  1是从草稿箱编辑

    public static  List<Integer> tagList;



}