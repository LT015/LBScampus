package com.lt.common.util;

import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class HttpUtil {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String SERVER_HOST = "server_host";
    //public static String HOME_PATH = "http://192.168.43.209:8080";
    public static String HOME_PATH = "http://47.95.212.222:8080";
    public static String Banner = "/banner/";
    public static String Image = "/images/";
    private static final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/*");
    private HttpUtil(){}
    //Activity
    public static final String GET_LIST="/LBS/activity/list";
    public static final String GET_INTIME_LIST="/LBS/activity/list/inTime";
    public static final String GET_WITH_PAGER="/LBS/activity/list/range";
    public static final String CREATE_ACTIVITY="/LBS/activity";
    public static final String UPDATE_TITLE="/LBS/activity/update/title";
    public static final String UPDATE_CONTENT="/LBS/activity/update/content";
    public static final String UPDATE_ADMIN_ID="/LBS/activity/update/adminId";
    public static final String UPDATE_PICTURE_PATH="/LBS/activity/update/picturePath";
    public static final String UPDATE_START_TIME="/LBS/activity/update/startTime";
    public static final String UPDATE_END_TIME="/LBS/activity/update/endTime";
    public static final String UPDATE_STATUS="/LBS/activity/update/status";
    public static final String UPDATE_TAG_LIST="/LBS/activity/update/tag";
    public static final String UPDATE_IMAGE_LIST="/LBS/activity/update/image";
    public static final String EXAMINE="/LBS/activity/examine";
    public static final String DELETE_ACTIVITY="/LBS/activity";
    //Admin
    public static final String CREATE="/LBS/admin";
    public static final String DELETE="/LBS/admin";
    public static final String LIST="/LBS/admin/list";
    public static final String QUERY="/LBS/admin/department";
    public static final String UPDATE_ROLE="/LBS/admin/update";//"/update/{adminId:.+}/role/{role:.+}
    //Building
    public static final String GET_BUILDING_TYPE="/LBS/building/getbuildtype";
    public static final String GET_BUILDING_NAME="/LBS/building/getname/";//
    public static final String CREATE_BUILDING="/LBS/building";
    public static final String DELETE_BUILDING="/LBS/building";///{buildingId:.+}
    public static final String SEARCH_BUILDING="/LBS/building";///type/{type:.+}/name/{name:.+}
    public static final String SEARCH_BUILDING_BY_TYPE="/LBS/building";///type/{type:.+}
    public static final String BUILDING_LIST="/LBS/building/list";
    public static final String GET_BY_ID="/LBS/building";//{id:.+}
    public static final String UPDATE_BUILDING_PICTURE_PATH="/LBS/building/update/picturePath";///{id:.+}/{picturePath:.+}
    public static final String UPDATE_DESCRIPTION="/LBS/building/update/description";//{id:.+}/{description:.+}
    //Department  部门
    public static final String CREATE_REST="/LBS/department";
    public static final String DELETE_REST="/LBS/department";///{departmentId:.+}
    public static final String REST_LIST="/LBS/department/list";
    public static final String SEARCH_DEPART="/LBS/department";//
    public static final String UPDATE_DEPART_PICTURE_PATH="/LBS/department/update/picturePath";//{id:.+}/{picturePath:.+}
    public static final String UPDATE_DEPART_DESCRIPTION="/LBS/department/update/description";//{id:.+}/{description:.+}
    public static final String UPDATE_DEPART_NAME="/LBS/department/update/name";//{id:.+}/{name:.+}
    public static final String UPDATE_BUILDING_ID="/LBS/department/update/buildingId";//{id:.+}/{buildingId:.+}
    public static final String GET_DEPARTMENT_NAME="/LBS/department/deptType";//{id:.+}/{buildingId:.+}
    //notice
    public static final String GET_NOTICE_LIST="/LBS/notice/list";//得到所有notice  get请求
    public static final String GET_NOTICE_BY_ID="/LBS/notice/id";  //get请求
    public static final String COLLECT_NOTICE="/LBS/notice";  //get请
    //public static final String GET_BOTICE_WITH_PAGER="/LBS/notice/list/pager";///{userId:.+}
    public static final String GET_NOTICE_BY_TYPE="/LBS/notice/type";  //get请求 //type/{type:.+}/status/{status:.+}
    public static final String GET_VERTIFY_NOTICE="/LBS/notice/userVertify";
    public static final String GET_NOTICE_BY_HOBBY="/LBS/notice/userTag";
    public static final String GET_NOTICE_BY_TITLE="/LBS/notice";  //get请求 /type/{type:.+}/title/{title:.+}
    public static final String GET_USER_NOTICE="/LBS/notice";//我的收藏
    public static final String CREATE_NOTICE="/LBS/notice";//PUT
    public static final String GET_MY_NOTICE="/LBS/notice";///publisher/{publisher:.+}/status/{status:.+}
    public static final String UPDATE_NOTICE_TITLE="/LBS/notice/update/title";//{id:.+}/{title:.+}
    public static final String UPDATE_NOTICE_CONTENT="/LBS/notice/update/content";//{id:.+}/{content:.+}
    public static final String UPDATE_NOTICE_ADMAIN="/LBS/notice/update/adminId";//{id:.+}/{adminId:.+}
    public static final String UPDATE_NOTICE_PICTURE_PATH="/LBS/notice/update/picturePath";//{id:.+}/{picturePath:.+}
    public static final String UPDATE_NOTICE_STATUS="/LBS/notice/update/status";//{id:.+}/{status:.+}
    public static final String UPDATE_NOTICE_TAG_LIST="/LBS/notice/update/tag";//{id:.+}
    public static final String UPDATE_NOTICE_TYPE="/LBS/notice/update/type";//{id:.+}/{type:.+}
    public static final String UPDATE_PRIORITY="/LBS/notice/update/priority";//{id:.+}/{priority:.+}
    public static final String UPDATE_NOTICE_IMAGE_LIST="/LBS/notice/update/image";//{id:.+}
    public static final String NOTICE_EXAMINE="/LBS/notice/examine";//{id:.+}/{userId:,+}/{status:.+}
    public static final String DELETE_NOTICE="/LBS/notice";//  DELETE  /{id:.+}
    //Position
    public static final String UPDATE_POSITION="/LBS/position";//PUT
    public static final String GET_POSITION="/LBS/position";//GET {userId}
    //TagBean
    public static final String TAG_LIST="/LBS/tag";//GET
    public static final String CREAT_TAG="/LBS/tag";//PUT
    public static final String DELETE_TAG="/LBS/tag";//DELETE   tagId:.+}
    //User
    public static final String LOG_IN="/LBS/user/logIn";//{userId:.+}/{password:.+}
    public static final String GET_USER_INFO="/LBS/user/info";
    public static final String USER_QUERY="/LBS/user/userId";//{userId:.+}"  得到用户详情信息
    public static final String REGISTER="/LBS/user/register";//
    public static final String SEARCH_USER="/LBS/user";//
    public static final String DELETE_USER="/LBS/user";///{userId:.+}
    public static final String UPDATE_TEL_NUM="/LBS/user/update/tel";//{userId:.+}/{telNum:.+}
    public static final String UPDATE_IMAGE="/LBS/user/update/image";//{userId:.+}/{image:.+
    public static final String UPDATE_NICK_NAME="/LBS/user/update/nick";//{userId:.+}/{nickName:.+}
    public static final String UPDATE_EMAIL="/LBS/user/update/email";//{userId:.+}/{email:.+}
    public static final String UPDATE_SHARE_TIME="/LBS/user/update/shareTime";//{userId:.+}/{start:.+}/{end:.+}
    public static final String UPDATE_PORTRAIT_PATH="/LBS/user/update/portrait";//{userId:.+}/{portrait:.+}
    public static final String UPDATE_USER_STATUS="/LBS/user/update/status";//{userId:.+}/{status:.+}
    public static final String UPDATE_PASSWORD="/LBS/user/update/password";//{userId:.+}/{password:.+}/{oldPassword:.+}
    public static final String UPDATE_HOBBY="/LBS/user/update/hobby";//{userId:.+}
    //Room
    public static final String GET_ROOMS="/LBS/room";//{userId:.+}/{status:.+}
    public static final String GET_BUILDING_ROOMS="/LBS/user/update/password";//{userId:.+}/{password:.+}/{oldPassword:.+}
    public static final String COLLECT_ROOM="/LBS/room";//{userId:.+}
    //Class
    public static final String GET_CLASS_LIST="/LBS/timetable";
    //TimeTable
    public static final String GET_TIMETABLE="/LBS/timetable";//{name:.+}//type name为classId或place


    //image
    public static final String GET_IMAGE="/LBS/iamge";

    public static final String SAVE_USER_NAME = "";
    public static final String SAVE_USER_PHONE = "";
    public static final String SAVE_USER_EMAIL = "";

    public static void setHomePath(String path){
        HOME_PATH = path;
    }


    private static class SingHolder{
        private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
    }


    public static String GetUrl(String path,List<String>  list){


        StringBuilder url = new StringBuilder(path);
        Iterator iterator=list.iterator();

//        Set set = hashMap.keySet();
//        Iterator iterator = set.iterator();
        for(int i=0;i<list.size();i++){
            url.append("/");
            url.append(list.get(i));
        }
        Log.d("url", "GetUrl: ");
        return url.toString();

    }



    public static void sendOkHttpGetRequest(String url,  List<String> list,Callback callback){
        String address=url;
        if(list.size() != 0){
            address=GetUrl(url,list);
        }
        Request request = new Request.Builder()
                .url(address)
                .build();
        SingHolder.okHttpClient.newCall(request).enqueue(callback);
    }

    //第三个方法可以代替第二个方法
    public static void sendOkHttpPostRequest(String address, HashMap<String,String> hashMap, Callback callback){
        FormBody.Builder builder = new FormBody.Builder();
        Set set = hashMap.keySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            String key = (String)iterator.next();
            builder.add(key,hashMap.get(key));
        }
        //传递json字符串,后台用$_POST['json']获取,然后用json_decode()解析成数组或对象;
//        builder.add("json",new Gson().toJson());


        Request request = new Request.Builder()
                .url(address)
                .post(builder.build())
                .build();
        SingHolder.okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpPutRequest(String address,String json, Callback callback){

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .put(body)
                .url(address)
                .build();
        SingHolder.okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpDeleteRequest(String address,String json, Callback callback){

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .delete(body)
                .url(address)
                .build();
        SingHolder.okHttpClient.newCall(request).enqueue(callback);
    }

    //    public static void upLoadImgsRequest(String address, HashMap<String,String> hashMap, List<String> imgUrls, Callback callback){
//        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//        if (imgUrls!=null){
//            for (int i = 0; i <imgUrls.size() ; i++) {
//                File f = new File(imgUrls.get(i));
//                if (f != null) {
//                    String name=f.getName();
//                    builder.addFormDataPart("img"+i, f.getName(), RequestBody.create(MEDIA_TYPE_IMAGE, f));
//                }
//            }
//        }
////builder.addFormDataPart("json",json字符串); 也可以这样传递json字符串,一般json字符串是用new Gson().toJson(obj)把对象转换成的，也可以解析list，这样就解析成jsonArray字符串了
//        Set set = hashMap.keySet();
//        Iterator iterator = set.iterator();
//        while(iterator.hasNext()){
//            String key = (String)iterator.next();
//            builder.addFormDataPart(key,hashMap.get(key));
//        }
//
//        //构建请求
//        Request request = new Request.Builder()
//                .url(address)//地址
//                .post(builder.build())//添加请求体
//                .build();
//        SingHolder.okHttpClient.newCall(request).enqueue(callback);
//    }
    public static void upLoadImgsRequest(String address, String json, Callback callback){
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .post(body)
                .url(address)
                .build();
        SingHolder.okHttpClient.newCall(request).enqueue(callback);

    }
    public static void  getImage(String address,Callback callback){
        Request request = new Request.Builder()
                .url(address)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(100, TimeUnit.SECONDS)//设置读取超时时间
                .build();
        client.newCall(request).enqueue(callback);
    }

}
/*
 String json = new Gson().toJson(userBean);

http请求的response格式：

final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            final JSONObject jsonObject = new JSONObject(responseText);
                            notice = new Gson().fromJson(jsonObject.toString(),NoticeBean.class);
                            initInfo();

                        }catch (JSONException e){
                            Log.d("LoginActivity",e.toString());
                            Toast.makeText(NoticeActivity.this, "获取信息失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final JSONObject jsonObject = new JSONObject(responseText);
                Integer state = (Integer) jsonObject.get("status");

                final JSONArray jsonArray = new JSONArray(responseText);
                list = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<NoticeBean>>(){}.getType());


http请求的onFailure格式：

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
                        // progressBar.setVisibility(View.GONE);
                    }
                });
Glide加载图片
               String imagePath= HttpUtil.HOME_PATH + HttpUtil.Image+building.getPicturePath();
        if(imagePath != null){
            GlideUtil.load(BuildingDetailActivity.this, imagePath, buildingImage, GlideUtil.REQUEST_OPTIONS);
        }

 Glide在BasequickAdapter加载图片
                String path = HttpUtil.HOME_PATH + HttpUtil.Image + item.getPicturePath();
        ImageView imageView = helper.getView(R.id.item_department_image);
        GlideUtil.load(context, path, imageView, GlideUtil.REQUEST_OPTIONS);
 */