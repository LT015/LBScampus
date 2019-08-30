package com.lbs.cheng.lbscampus.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by LT on 2019/4/2.
 */

public class Base64Util {
    static final String imageFile= Environment.getExternalStorageDirectory().getPath()
            +"/decodeImage.jpg";

    public static String GetImageStr(String imgFile)
    {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        return Base64.encodeToString(data,Base64.DEFAULT);//返回Base64编码过的字节数组字符串
    }

    public static String GenerateImage(String base64str)
    {
        //对字节数组字符串进行Base64解码并生成图片
        if (base64str == null) //图像数据为空
            return null;
        // System.out.println("开始解码");
//        Decoder decoder = new Base64.;
        try
        {
            byte[] decode = Base64.decode(base64str,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            //Base64解码
            OutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
            Log.d("imageFile", "GenerateImage: ");
        }
        catch (Exception e)
        {
            return null;
        }
        return imageFile;
    }
}
