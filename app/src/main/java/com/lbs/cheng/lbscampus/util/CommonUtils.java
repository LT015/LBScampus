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
    public static int buildingTypeId = 1;
    public static int isResume = 0;

//    public static int myNoticeTypeId = 0;//我的公告的类型  0是草稿 1是审核中 2是发布
//    public static int verifyNoticeTypeId = 0;//审核公告的类型 0是审核 1是完结

//    public static int userType = 1;//用户身份  1是学生 2是教师 3是其他

    public static int isLogin = 1;

    public static int noticeEditType = 0;//0是创建公告  1是从草稿箱编辑

//    public static  List<Integer> tagList;



}