package com.lbs.cheng.lbscampus.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.Preconditions;
import com.lbs.cheng.lbscampus.R;

import java.nio.charset.Charset;
import java.security.MessageDigest;


//Glide4.0 加载图片类
public class GlideUtil {

    /* 现在的头像是上传一个新头像时会清楚缓存重新用url获取，“我的”界面和“帐号管理”界面会读该url的缓存*/

    public static final RequestOptions REQUEST_OPTIONS = new RequestOptions()
            .placeholder(R.drawable.no_banner)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) //硬盘缓存,内存缓存是自动开启的
            .dontAnimate();

    public static void load(Context context, Object res, ImageView imageView){
        Glide.with(context)
                .load(res)
                .into(imageView);
    }

    public static void load(Context context, Object res, ImageView imageView, RequestOptions requestOptions){
        Glide.with(context)
                .load(res)
                .apply(requestOptions)
                .into(imageView);
    }

    public static Bitmap getBitmap(Context context, Object res){

        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context)
                    .asBitmap()
                    .load(res).submit(R.dimen.space_208px, R.dimen.space_144px).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;

    }



}
