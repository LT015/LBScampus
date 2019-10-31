package com.lbs.cheng.lbscampus.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.lbs.cheng.lbscampus.activity.UserActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by LT on 2019/3/16.
 */

public class FileStorage {
    private File cropIconDir;
    private File iconDir;

    public FileStorage() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File external = Environment.getExternalStorageDirectory();
            String rootDir = "/" + "restaurant";
            cropIconDir = new File(external, rootDir + "/crop");
            if (!cropIconDir.exists()) {
                cropIconDir.mkdirs();

            }
            iconDir = new File(external, rootDir + "/camera");
            if (!iconDir.exists()) {
                iconDir.mkdirs();

            }
        }
    }

    public File createCropFile() {
        String fileName = "";
        if (cropIconDir != null) {
            fileName = CommonUtils.GenerateUUID() + ".png";
        }
        return new File(cropIconDir, fileName);
    }

    public File createIconFile() {
        String fileName = "";
        if (iconDir != null) {
            fileName = CommonUtils.GenerateUUID() + ".png";
        }
        return new File(iconDir, fileName);
    }

    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 检测sdcard是否可用
     *
     * @return true为可用; false为不可用
     */
    public static boolean isSDAvailable()
    {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED))
            return false;
        return true;
    }

    public static void saveDataToFile(File fileToWrite, String data, boolean append) {
        FileOutputStream fOut = null;
        OutputStreamWriter myOutWriter = null;
        try {
            if (!fileToWrite.exists()) {
                if (!fileToWrite.getParentFile().exists())
                {
                    fileToWrite.getParentFile().mkdirs();
                }
                fileToWrite.createNewFile();
            }
            fOut = new FileOutputStream(fileToWrite, append);
            myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (myOutWriter != null) {
                try {
                    myOutWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除一个文件
     *
     * @param filePath
     *            要删除的文件路径名
     * @return true if this file was deleted, false otherwise
     */
    public static boolean deleteFile(String filePath)
    {
        try
        {
            File file = new File(filePath);
            if (file.exists())
            {
                return file.delete();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    // 复制文件
    public static void copyFile(File sourceFile, File targetFile) {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            // 关闭流
            if (inBuff != null)
                try {
                    inBuff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            if (outBuff != null)
                try {
                    outBuff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 删除 dir目录下的所有文件，包括删除文件夹
     *
     * @param dir
     */
    public static void deleteDirectory(File dir)
    {
        if (dir.isDirectory())
        {
            File[] listFiles = dir.listFiles();
            for (int i = 0; i < listFiles.length; i++)
            {
                deleteDirectory(listFiles[i]);
            }
        }
        dir.delete();
    }

    /**
     * @param imageUrl
     * @return Bitmap
     * 根据网络图片的url获取布局背景的对象,此方法要在子线程中执行
     */
    public static Drawable loadImageFromNetwork(String imageUrl)
    {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(
                    new URL(imageUrl).openStream(), "image.jpg");
        } catch (IOException e) {
            Log.d("loadImageFromNetwork", e.getMessage());
        }
        if (drawable == null) {
            Log.d("loadImageFromNetwork", "null drawable");
        }
        return drawable ;
    }

    //srcPath是本地手机中图片的路径
    public static Bitmap compressImageFromFile(String srcPath, float num) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = num;//
        float ww = num;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;
    }

    /**
     * 下面三个方法是把uri转换成filepath
     * @param context
     * @param uri
     * @return
     */
    @TargetApi(19)
    private String handleImgUri2String(Context context,Uri uri){
        String path = "";
        if (DocumentsContract.isDocumentUri(context, uri)) {
            //如果是document类型的uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                path = getImagePath(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                path = getImagePath(context,contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的Uri，则使用普通方式处理
            path = getImagePath(context,uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri,直接获取图片路径即可
            path = uri.getPath();
        }
        return path;
    }

    private String handleImgUri2StringBeforeKitKat(Context context,Uri uri){
        String path;
        path = getImagePath(context,uri, null);
        return path;
    }

    private String getImagePath(Context context,Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}

