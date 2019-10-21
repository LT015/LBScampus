package com.lbs.cheng.lbscampus.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.Staff;
import com.lbs.cheng.lbscampus.bean.Student;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lbs.cheng.lbscampus.util.Base64Util;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lbs.cheng.lbscampus.util.FileStorage;
import com.lbs.cheng.lbscampus.util.GlideUtil;
import com.lt.common.util.HttpUtil;
import com.lbs.cheng.lbscampus.util.Permission.PermissionListener;
import com.lbs.cheng.lbscampus.util.Permission.PermissionUtil;
import com.lbs.cheng.lbscampus.view.CircleImageView;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class UserActivity extends BaseActivity {

    private static final String TAG = "UserActivity";
    private static final int REQUEST_ALTER_PHONE = 3001;
    private View rootView;
    private static final int REQUEST_Certification = 6; //实名认证
    private static final int REQUEST_PICK_IMAGE = 1; //相册选取
    private static final int REQUEST_CAPTURE = 2;  //拍照
    private static final int REQUEST_PICTURE_CUT = 3;  //剪裁图片
    private static final int REQUEST_PERMISSION = 4;  //权限请求
    public static final int REQUEST_LOGIN = 5;      //登录
    private Uri imageUri;//原图保存地址
    private Uri outputUri;//剪切的地址
    private String imagePath;

    private Dialog dialog;
    private TextView tv3;
    private TextView tv1;
    private TextView tv2;
    PermissionUtil permissionUtil;
    ImageView back;
    TextView titleName;
    @BindView(R.id.use_name_text)
    TextView userNameText;
    @BindView(R.id.user_nick_name)
    AutoRelativeLayout userNickName;
    @BindView(R.id.user_sex)
    AutoRelativeLayout userSex;
    @BindView(R.id.user_phone)
    AutoRelativeLayout userPhone;
    @BindView(R.id.user_email)
    AutoRelativeLayout userEmail;
//    @BindView(R.id.my_QQ)
//    AutoRelativeLayout myQQ;
//    @BindView(R.id.my_wechat)
//    AutoRelativeLayout myWechat;
    @BindView(R.id.user_password)//登录密码
    AutoRelativeLayout userPassword;
    @BindView(R.id.my_head_img)
    CircleImageView headImg;
    @BindView(R.id.user_set_up)//头像以下的布局
    AutoLinearLayout userSetUp;
    @BindView(R.id.user_nick_name_text)
    TextView userNickNameText;
    @BindView(R.id.user_type)
    TextView userType;
    @BindView(R.id.user_num_text)
    TextView userIdText;
    @BindView(R.id.user_phone_text)
    TextView userPhoneText;
    @BindView(R.id.user_sex_text)
    TextView userSexText;
    @BindView(R.id.user_email_text)
    TextView userEmailText;
    @BindView(R.id.sv)
    NestedScrollView sv;
    @BindView(R.id.ll)//整个布局
    AutoLinearLayout ll;
    @BindView(R.id.user_qq)
    TextView userQQ;
    @BindView(R.id.user_wechat)
    TextView userWecaht;
    @BindView(R.id.loading)
    ProgressBar progressBar;
    @BindView(R.id.alter_user_pwd)//修改登录密码
    TextView alterUserPwdTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

    }

    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("帐号管理");
    }

    @Override
    protected void initView(){
        userNickName.setOnClickListener(this);
        userSex.setOnClickListener(this);
        userPhone.setOnClickListener(this);
//        myQQ.setOnClickListener(this);
//        myWechat.setOnClickListener(this);
        userPassword.setOnClickListener(this);
        headImg.setOnClickListener(this);
        userEmail.setOnClickListener(this);
        setUserInfo();
        initTitle();
    }
    private void setUserInfo() {
        String userNumId = null;
        UserBean userBean= DataSupport.findAll(UserBean.class).get(0);
        if(userBean.getType() == 1){
            Student student= DataSupport.findAll(Student.class).get(0);
            userNumId = student.getStudentId();
            userType.setText("学号");
        }else{
            userType.setText("职工号");
            Staff staff= DataSupport.findAll(Staff.class).get(0);
            userNumId = staff.getStaffId();
        }

        //String nickName = new String(Base64.decode(userBean.getNickName().getBytes(), Base64.DEFAULT));
        userNameText.setText(userBean.getUserName());
        userNickNameText.setText(userBean.getNickName());
        userPhoneText.setText(userBean.getTelNumber());
        userIdText.setText(userNumId);
        userSexText.setText(userBean.getSex());
        if(userBean.getEmail() != null){
            userEmailText.setText(userBean.getEmail());
        }

        if (userBean.getUserImage()==null){
            headImg.setImageResource(R.mipmap.head_img);

        }else {
            String path = HttpUtil.HOME_PATH + HttpUtil.Image + "user/"+userBean.getUserImage();
            //GlideUtil.REQUEST_OPTIONS.signature(new ObjectKey(System.currentTimeMillis()));
            GlideUtil.load(UserActivity.this, path, headImg, GlideUtil.REQUEST_OPTIONS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_nick_name:
                alterNicknameDialog();
                break;
            case R.id.user_phone:
                Intent intentPhone = new Intent(UserActivity.this,AlterPhoneActivity.class);
                intentPhone.putExtra("phone",userPhoneText.getText().toString());
                startActivity(intentPhone);
                break;
            case R.id.user_sex:
                //alterUserSexDialog();
                break;
            case R.id.user_email:
                alterUserEmailDialog();
                break;
            case R.id.user_password:
                Intent intent = new Intent(UserActivity.this, AlterPwdActivity.class);
                startActivity(intent);
                break;

            case R.id.my_head_img:
                initPicPopWindow();
                break;


            case R.id.tx_1:
                dialog.dismiss();
                openCamera();
                break;
            case R.id.tx_2:
                dialog.dismiss();
                selectFromAlbum();
                break;
            case R.id.tx_3:
                dialog.dismiss();
                break;
            case R.id.title_back:
                finish();
                break;
        }
    }

    private void alterNicknameDialog(){
        View view = LayoutInflater.from(UserActivity.this).inflate(R.layout.alter_edit,null);
        final EditText edit = (EditText) view.findViewById(R.id.edit);
        AlertDialog alertDialog = new AlertDialog.Builder(UserActivity.this).setTitle(getResources().getString(R.string.user_name))
                .setView(view)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String input = edit.getText().toString();
                        if (!TextUtils.isEmpty(input)) {
//                            userNameText.setText(input);
//                            Toast.makeText(UserActivity.this, "用户名修改成功", Toast.LENGTH_SHORT).show();
                            final List<UserBean> list = DataSupport.findAll(UserBean.class);
                            if (list.size()>0){
                                UserBean userBean = list.get(0);
                                progressBar.setVisibility(View.VISIBLE);
                                String userId=String.valueOf(userBean.getUserId());
                                String nickname= null;
                                try {
                                    nickname = URLEncoder.encode(input,"utf-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                HashMap<String,String> hash = new HashMap<>();
                                hash.put("userId",userId);
                                hash.put("nickName",nickname);
                                List<String> list1=new ArrayList<>();
                                list1.add(userId);
                                list1.add(nickname);

                                String address=HttpUtil.GetUrl( HttpUtil.HOME_PATH + HttpUtil.UPDATE_NICK_NAME,list1);
                                HttpUtil.sendOkHttpPostRequest(address, hash, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(UserActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        final String responseText  = response.body().string();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE);
                                                userNickNameText.setText(input);
                                                //将更改保存到本地数据库
                                                UserBean userBean = list.get(0);
                                                userBean.setNickName(input);
                                                userBean.save();
                                                userNickNameText.setText(userBean.getNickName());

                                            }
                                        });


                                    }
                                });
                            }
                        }
                        else {
                            Toast.makeText(UserActivity.this, getResources().getString(R.string.nick_name_not_empty), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void alterUserSexDialog(){
        final View viewSex = LayoutInflater.from(UserActivity.this).inflate(R.layout.alter_sex,null);
        final RadioGroup selected = (RadioGroup) viewSex.findViewById(R.id.radio_group);
        RadioButton man = (RadioButton) viewSex.findViewById(R.id.man);
        RadioButton woman = (RadioButton) viewSex.findViewById(R.id.woman);
        if (userSexText.getText().toString().equals("男")){
            man.setChecked(true);
        }else{
            woman.setChecked(true);
        }
        new AlertDialog.Builder(UserActivity.this)
                .setTitle(getResources().getString(R.string.user_sex))
                .setView(viewSex)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RadioButton rb = (RadioButton) viewSex.findViewById(selected.getCheckedRadioButtonId());
                        final String sex = rb.getText().toString();
//                        userSexText.setText(sex);
                        //将修改保存到本地数据库
                        final List<UserBean> list = DataSupport.findAll(UserBean.class);
//                        if (list.size()>0){
//                            UserBean userBean = list.get(0);
//                            if (sex.equals("男")){
//                                userBean.setUserSex(1);
//                            }else{
//                                userBean.setUserSex(0);
//                            }
//                            userBean.save();
//                        }
//                        Toast.makeText(UserActivity.this, "用户性别修改成功", Toast.LENGTH_SHORT).show();
                        //将更改保存到远程数据库
                        progressBar.setVisibility(View.VISIBLE);
                        HashMap<String,String> hash = new HashMap<>();
                        hash.put("user_id",String.valueOf(list.get(0).getUserId()));
                        if (sex.equals("男")){
                            hash.put("user_sex","man");
                        }else{
                            hash.put("user_sex","woman");
                        }
//                        HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH+HttpUtil.SAVE_USER_SEX, hash, new Callback() {
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                Log.d("UserActivity",e.toString());
//                            }
//
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//                                String responseText = response.body().string();
//                                try{
//                                    JSONObject jsonObject = new JSONObject(responseText);
//                                    final String msg = jsonObject.getString("msg");
//                                    final int status = jsonObject.getInt("status");
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(UserActivity.this, msg, Toast.LENGTH_SHORT).show();
//                                            progressBar.setVisibility(View.GONE);
//                                            if (status != 0){
//                                                userSexText.setText(sex);
//                                                //保存到本地数据库
//                                                UserBean userBean = list.get(0);
//                                                if (sex.equals("男")){
//                                                    userBean.setSex("男");
//                                                }else{
//                                                    userBean.setSex("女");
//                                                }
//                                                userBean.save();
//                                            }
//                                        }
//                                    });
//                                }catch(JSONException e){
//
//                                }
//                            }
//                        });
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .show();
    }

    private void alterUserEmailDialog(){
        View view = LayoutInflater.from(UserActivity.this).inflate(R.layout.alter_email,null);
        final EditText email = (EditText) view.findViewById(R.id.email);
        AlertDialog alertDialog = new AlertDialog.Builder(UserActivity.this).setTitle(getResources().getString(R.string.user_email))
                .setView(view)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String input = email.getText().toString();
                        if (!TextUtils.isEmpty(input)) {
//                            userNameText.setText(input);
//                            Toast.makeText(UserActivity.this, "邮箱修改成功", Toast.LENGTH_SHORT).show();
                            final List<UserBean> list = DataSupport.findAll(UserBean.class);
                            if (list.size()>0){
                                UserBean userBean = list.get(0);
//                                将更改保存到远程数据库
                                progressBar.setVisibility(View.VISIBLE);
                                HashMap<String,String> hash = new HashMap<>();
                                hash.put("userId",String.valueOf(userBean.getUserId()));
                                hash.put("email",input);
                                List<String> list1=new ArrayList<>();
                                list1.add(userBean.getUserId());
                                list1.add(input);
                                String address=HttpUtil.GetUrl( HttpUtil.HOME_PATH + HttpUtil.UPDATE_EMAIL,list1);
                                HttpUtil.sendOkHttpPostRequest(address, hash, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(UserActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE);
                                                userEmailText.setText(input);
                                                //将更改保存到本地数据库
                                                UserBean userBean = list.get(0);
                                                userBean.setEmail(input);
                                                userBean.save();
                                                Toast.makeText(UserActivity.this, "保存成功", Toast.LENGTH_SHORT).show();

                                            }
                                        });


                                    }
                                });
                            }
                        }
                        else {
                            Toast.makeText(UserActivity.this, getResources().getString(R.string.user_email_not_empty), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void initPicPopWindow(){
        dialog = new Dialog(UserActivity.this,R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View view = LayoutInflater.from(UserActivity.this).inflate(R.layout.popup_slide_from_bottom, null);
        tv1 = (TextView)view.findViewById(R.id.tx_1);
        tv2 = (TextView)view.findViewById(R.id.tx_2);
        tv3 = (TextView)view.findViewById(R.id.tx_3);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        //将布局设置给Dialog
        dialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 0;//设置Dialog距离底部的距离
        //设置dialog宽度满屏
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay();
        lp.width = d.getWidth();
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    /**
     * 打开系统相机
     */
    private void openCamera() {
        File file = new FileStorage().createIconFile(); //用到了sd卡权限,运行时权限处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(UserActivity.this, "com.lbs.cheng.lbscampus.fileprovider", file);//通过FileProvider创建一个content类型的Uri
        } else {
            imageUri = Uri.fromFile(file);
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    /**
     * 从相册选择
     */
    private void selectFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");//打开指定URI目录下的照片
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    /**
     * 裁剪
     */
    private void cropPhoto() {
        outputUri = null;
        File file = new FileStorage().createCropFile(); //用到了sd卡权限，运行时权限处理
        outputUri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(imageUri, "image/*");  //打开指定URI目录下的照片
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);//将裁剪完的照片保存到指定URI
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_PICTURE_CUT);
    }

    @TargetApi(19)
    private String handleImgUri2String(Uri uri){
        String path = "";
        if (DocumentsContract.isDocumentUri(UserActivity.this, uri)) {
            //如果是document类型的uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                path = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                path = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的Uri，则使用普通方式处理
            path = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri,直接获取图片路径即可
            path = uri.getPath();
        }
        return path;
    }

    private String handleImgUri2StringBeforeKitKat(Uri uri){
        String path;
        path = getImagePath(uri, null);
        return path;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE://从相册选择
                if (resultCode == Activity.RESULT_OK){
                    if (data != null) {
                        imageUri = data.getData();
                        cropPhoto();
                    }
                }
                break;
            case REQUEST_CAPTURE://拍照
                if (resultCode == Activity.RESULT_OK) {
                    cropPhoto();
                }
                break;
            case REQUEST_PICTURE_CUT://裁剪完成
                if (resultCode == Activity.RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19) {
                        imagePath = handleImgUri2String(outputUri);
                    } else {
                        imagePath = handleImgUri2StringBeforeKitKat(outputUri);
                    }
                    String image = Base64Util.GetImageStr(imagePath);
                    //获取图片格式
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inJustDecodeBounds = true;
//                    BitmapFactory.decodeFile(imagePath, options);
//                    String type = options.outMimeType;

                    //将图片保存到MySql
                    progressBar.setVisibility(View.VISIBLE);

                    String userId=DataSupport.findAll(UserBean.class).get(0).getUserId();
                    List<String> list1=new ArrayList<>();
                    list1.add(userId);
                    String address=HttpUtil.GetUrl( HttpUtil.HOME_PATH + HttpUtil.UPDATE_IMAGE,list1);
                    String json="{\"data:image/png;base64,"+image+"\"}";
                    HttpUtil.upLoadImgsRequest(address, json, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("UserActivity",e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseText  = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);

                                    //将更改保存到本地数据库
                                    UserBean userBean = DataSupport.findAll(UserBean.class).get(0);
                                    userBean.setUserImage(responseText);
                                    userBean.save();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            UserBean userBean = DataSupport.findAll(UserBean.class).get(0);
                                            //保存本地图片头像的路径
                                            //PreferenceManager.getDefaultSharedPreferences(UserActivity.this).edit().putString(userBean.getUserId()+"",imagePath).commit();
                                            String path = HttpUtil.HOME_PATH + HttpUtil.Image + "user/"+ responseText;
                                            GlideUtil.REQUEST_OPTIONS.signature(new ObjectKey(System.currentTimeMillis()));//签名  用以重新获取图片
                                            GlideUtil.load(UserActivity.this, path, headImg, GlideUtil.REQUEST_OPTIONS);
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });

                                }
                            });
                        }
                    });

                }
                break;

        }
    }




}
