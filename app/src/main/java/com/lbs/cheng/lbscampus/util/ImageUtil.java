package com.lbs.cheng.lbscampus.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.baidu.mapapi.http.HttpClient;
import com.lbs.cheng.lbscampus.activity.UserActivity;
import com.lbs.cheng.lbscampus.util.GlideUtil;
import com.lbs.cheng.lbscampus.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by LT on 2019/4/2.
 */

public class ImageUtil {
    private static String path;

    public static String getImagePath(String key){

        List list=new ArrayList<>();
        list.add(key);

        final String address= HttpUtil.GetUrl( HttpUtil.HOME_PATH + HttpUtil.GET_IMAGE,list);


        HttpUtil.getImage(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("LoginActivity","获取图片失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("imageUtil", "onResponse: ");
                final String responseText  = response.body().string();
                String imageCode=responseText.replaceAll("\"","");
                String result=imageCode.replace("{","").replace("}","");
                path=Base64Util.GenerateImage(result);

            }
        });
        Log.d("imageUtil", "getImage: ");
        return path;
    }
}
