package com.lbs.cheng.lbscampus.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DateSorter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.signature.ObjectKey;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.adapter.TagAdapter;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;
import com.lbs.cheng.lbscampus.bean.Staff;
import com.lbs.cheng.lbscampus.bean.TagBean;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lbs.cheng.lbscampus.util.Base64Util;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lbs.cheng.lbscampus.util.DateUtil;
import com.lbs.cheng.lbscampus.util.FileStorage;
import com.lbs.cheng.lbscampus.util.GlideUtil;
import com.lt.common.util.HttpUtil;
import com.lbs.cheng.lbscampus.view.MyGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddNoticeActivity extends BaseActivity {
    ImageView back;
    TextView titleName;
    TagAdapter adapter;

    @BindView(R.id.select_interst_recycleview)
    RecyclerView recyclerView;
    @BindView(R.id.publish)
    TextView publish;
    @BindView(R.id.add_image)
    ImageView addImage;
    @BindView(R.id.notice_title)
    EditText noticeTitle;
    @BindView(R.id.notice_content)
    EditText noticeContent;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.radio0)
    RadioButton radio0;
    @BindView(R.id.radio1)
    RadioButton radio1;
    @BindView(R.id.radio2)
    RadioButton radio2;
    @BindView(R.id.radio3)
    RadioButton radio3;
    PopupWindow popupWindow;
    TextView popupContent;
    TextView popupCancle;
    TextView popupConfirm;

    private static final int REQUEST_PICK_IMAGE = 1; //相册选取
    private static final int REQUEST_CAPTURE = 2;  //拍照
    private static final int REQUEST_PICTURE_CUT = 3;  //剪裁图片
    private static final int REQUEST_PERMISSION = 4;  //权限请求
    public static final int REQUEST_LOGIN = 5;      //登录
    private static final int MY_ADD_CASE_CALL_PHONE = 6;//调取系统摄像头的请求码
    private static final int MY_ADD_CASE_CALL_PHONE2 = 7;//打开相册的请求码
    private Uri uriPhoto;//相机拍照的地址
    private Uri outputUri;//剪切的地址
    private String imagePath;//剪切后图片的地址
    private Bitmap newImage;//新图片
    private String imageBase64;//tu图片的Base64编码
    private List<String> imageJsonList = new ArrayList<>();//图片的编码转成的json

    private Dialog dialog;
    private TextView tv3;
    private TextView tv1;
    private TextView tv2;
    public NoticeDetailBean noticeDetail;//草稿箱传来的
    public NoticeBean notice=new NoticeBean();//要提交的notice
    UserBean userBean;
    private List<TagBean> tagBeanList ;//所有标签
    public List<Integer> list = new ArrayList<>();//选中的标签编号
    private List<TagBean> tagSelectedList=new ArrayList<>() ;//选中的标签
    public int saveType = 1;//1是发布 0是保存到草稿箱
    public int noticeType = 0;//notice类型
    private int imageState = 0;//0表示没有图片 1表示设置了新图片 2表示草稿有图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);
    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        if(CommonUtils.noticeEditType == 0){
            titleName.setText("创建公告");
        }else{
            titleName.setText("编辑公告");
        }

    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent=getIntent();
        if(CommonUtils.noticeEditType == 1) {
            String json = intent.getStringExtra("noticeDetail");
            if(json != null){
                noticeDetail = new Gson().fromJson(json,NoticeDetailBean.class);
            }
            if(noticeDetail.getPicturePath() != null){
                imageState = 2;
                String url= HttpUtil.HOME_PATH + HttpUtil.Image + "notice/" + noticeDetail.getPicturePath();
                GlideUtil.load(AddNoticeActivity.this,url , addImage, GlideUtil.REQUEST_OPTIONS);
            }
            noticeTitle.setText(noticeDetail.getTitle());
            noticeContent.setText(noticeDetail.getContent());
            List<TagBean> tagList=noticeDetail.getTagList();
            if(tagList != null){
                for(TagBean tag:tagList){
                    list.add(tag.getTagId());
                }
            }
            noticeType = noticeDetail.getType();
            switch (noticeType){
                case 1:
                    radio0.setChecked(true);
                    break;
                case 2:
                    radio1.setChecked(true);
                    break;
                case 3:
                    radio2.setChecked(true);
                    break;
                case 4:
                    radio3.setChecked(true);
                    break;
            }

        }
        userBean = DataSupport.findLast(UserBean.class);
        getTagList();

    }

    private void initRecycleView(){
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        adapter = new TagAdapter(R.layout.item_interest,tagBeanList,list,AddNoticeActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int i=  list.indexOf(tagBeanList.get(position).getTagId());

                if (i != -1){
                    list.remove(i);
                    TextView textView=view.findViewById(R.id.item_interst_tv);
                    textView.setBackgroundResource(R.drawable.checkbox_unselect);
                }
                else if(i == -1){
                    TextView textView=view.findViewById(R.id.item_interst_tv);
                    textView.setBackgroundResource(R.drawable.checkbox_select);
                    list.add(tagBeanList.get(position).getTagId());
                }


            }
        });

    }

    public void getTagList(){
        HttpUtil.sendOkHttpGetRequest( HttpUtil.HOME_PATH + HttpUtil.TAG_LIST, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddNoticeActivity.this, "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
                        // progressBar.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            final JSONArray jsonArray = new JSONArray(responseText);
                            tagBeanList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<TagBean>>(){}.getType());
                            initRecycleView();

                        }catch (JSONException e){
                            Log.d("LoginActivity",e.toString());
                            Toast.makeText(AddNoticeActivity.this, "获取标签失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }//通过网络请求得到所有标签

    void initListener(){
        publish.setOnClickListener(this);
        addImage.setOnClickListener(this);
    }
    @Override
    protected void initView() {
        super.initView();
        initTitle();
        initListener();
        initRadioButton();
        radioGroup.setOnCheckedChangeListener(mChangeRadio);


    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_back:
                showShadow("保存到草稿");
                break;
            case R.id. add_image:
                initPicPopWindow();
                break;
            case R.id.publish:
                saveType = 1;
                saveNotice();

                break;
            case R.id.tx_1:
                dialog.dismiss();
                if (ContextCompat.checkSelfPermission(AddNoticeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(AddNoticeActivity.this, new String[]{Manifest.permission.CAMERA}, MY_ADD_CASE_CALL_PHONE);

                    if(ContextCompat.checkSelfPermission(AddNoticeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AddNoticeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_ADD_CASE_CALL_PHONE2);

                    }
                }else {
                    try {
                        takePhoto();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.tx_2:
                if (ContextCompat.checkSelfPermission(AddNoticeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(AddNoticeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_ADD_CASE_CALL_PHONE2);

                } else {
                    choosePhoto();
                }
                dialog.dismiss();
                break;
            case R.id.tx_3:
                dialog.dismiss();
                break;
        }
    }

    private void initRadioButton(){
        if (userBean.getType() != 1){
            Staff staff = DataSupport.findLast(Staff.class);
            if(staff != null){
                if(staff.getRole().equals("2")){
                    radio0.setVisibility(View.GONE);
                }else if(staff.getRole().equals("3")){

                }
            }

        }else{
            radio0.setVisibility(View.GONE);
            radio1.setVisibility(View.GONE);
        }
    }

    private void showShadow(String content) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopupWindow = inflater.inflate(R.layout.popup_add, null, false);//引入弹窗布局
        popupWindow = new PopupWindow(vPopupWindow, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

        //设置背景透明
        addBackground();

        //引入依附的布局
        View parentView = LayoutInflater.from(AddNoticeActivity.this).inflate(R.layout.popup_add, null);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        popupConfirm = (vPopupWindow.findViewById(R.id.popup_add_confirm));
        popupCancle = (vPopupWindow.findViewById(R.id.popup_add_cancel));
        popupContent = (vPopupWindow.findViewById(R.id.popup_content));
        popupContent.setText(content);
        popupConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveType = 0;
                saveNotice();
                popupWindow.dismiss();

            }
        });
        popupCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                finish();

            }
        });
    }

    private void addBackground() {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;//调节透明度
        getWindow().setAttributes(lp);
        //dismiss时恢复原样
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }



    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showShadow("保存到草稿");
    }

    private void saveNotice(){
        if(CommonUtils.noticeEditType == 1){
            notice.setNoticeId(String.valueOf(noticeDetail.getNoticeId()));
        }else{
            notice.setNoticeId(null);
        }

        notice.setTitle(noticeTitle.getText().toString());
        notice.setContent(noticeContent.getText().toString());
        notice.setType(noticeType);
        if(list.size()>0){
            for(int i:list){
                tagSelectedList.add(tagBeanList.get(i-1));
            }

            notice.setTagList(tagSelectedList);
        }
        if(saveType == 1){
            notice.setStatus(1);
        }else{
            notice.setStatus(0);
        }
        long time=System.currentTimeMillis();//系统时间
        String publishTime = DateUtil.getDateToString(time,DateUtil.pattern);
        notice.setPublishTime(publishTime);
        notice.setPublisher(userBean.getUserId());
        if(imageState == 2){
            notice.setPicturePath(noticeDetail.getPicturePath());
        }
        String json = new Gson().toJson(notice);
        HttpUtil.sendOkHttpPutRequest( HttpUtil.HOME_PATH + HttpUtil.CREATE_NOTICE, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddNoticeActivity.this, "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
                        // progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            final JSONObject jsonObject = new JSONObject(responseText);
                            if(jsonObject.isNull("error")){
                                notice = new Gson().fromJson(jsonObject.toString(),NoticeBean.class);

                                if(imageState == 1){
                                    updateImage();
                                } else{
                                    if(saveType == 1){
                                        Toast.makeText(AddNoticeActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(AddNoticeActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                    }

                                    finish();
                                }
                            }else{
                                Toast.makeText(AddNoticeActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                            }


                        }catch (JSONException e){
                            Log.d("LoginActivity",e.toString());
                            Toast.makeText(AddNoticeActivity.this, "获取信息失败!", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        });
    }

    public void updateImage(){
        HashMap<String,String> hash = new HashMap<>();
        hash.put("notice_id",notice.getNoticeId());
        String url= HttpUtil.HOME_PATH + HttpUtil.UPDATE_NOTICE_IMAGE_LIST+"/"+notice.getNoticeId();
        String json="{\"data:image/png;base64,"+imageBase64+"\"}";
        HttpUtil.upLoadImgsRequest(url, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddNoticeActivity.this, "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
                        // progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            final JSONObject jsonObject = new JSONObject(responseText);
                            if(jsonObject.isNull("error")){
                                if(saveType == 1){
                                    Toast.makeText(AddNoticeActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(AddNoticeActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }
                        }catch (JSONException e){
                            Log.d("LoginActivity",e.toString());
                            Toast.makeText(AddNoticeActivity.this, "保存失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    private RadioGroup.OnCheckedChangeListener mChangeRadio = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if (checkedId == radio0.getId()) {
                noticeType = 1;
            }
            if (checkedId == radio1.getId()) {
                noticeType = 2;
            }
            if (checkedId == radio2.getId()) {
                noticeType = 3;
            }
            if (checkedId == radio3.getId()) {
                noticeType = 4;
            }
        }
    };

    private void initPicPopWindow(){
        dialog = new Dialog(AddNoticeActivity.this,R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View view = LayoutInflater.from(AddNoticeActivity.this).inflate(R.layout.popup_slide_from_bottom, null);
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
    private void takePhoto() throws IOException {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        // 获取文件
        File file = createFileIfNeed("UserIcon.png");
        //拍照后原图回存入此路径下
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uriPhoto = Uri.fromFile(file);
        } else {
            /**
             * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
             * 并且这样可以解决MIUI系统上拍照返回size为0的情况
             */
            uriPhoto = FileProvider.getUriForFile(this, "com.lbs.cheng.lbscampus.fileprovider", file);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPhoto);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }
    /**
     * 打开相册
     */
    private void choosePhoto() {
        //这是打开系统默认的相册(就是你系统怎么分类,就怎么显示,首先展示分类列表)
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, REQUEST_PICK_IMAGE);
    }

    // 在sd卡中创建一保存图片（原图和缩略图共用的）文件夹
    private File createFileIfNeed(String fileName) {
        File file = new File(getExternalCacheDir(),fileName);
        try{
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
        }catch(IOException e){
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 申请权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_ADD_CASE_CALL_PHONE) {//摄像头
            if(grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        takePhoto();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
                }
            }

        }


        if (requestCode == MY_ADD_CASE_CALL_PHONE2) {
            if(grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhoto();
                } else {
                    Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE://从相册选择
                getOutputUri();
                //得到图片路径
                if (resultCode == Activity.RESULT_OK){
                    Uri uri = data.getData();
                    //调用系统裁剪功能
                    Intent it = new Intent("com.android.camera.action.CROP");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    //得到照片进行裁剪
                    it.setDataAndType(uri, "image/*");
                    //设置是否支持裁剪
                    it.putExtra("crop", true);
                    //设置框的宽高比
                    it.putExtra("aspactX", 1);
                    it.putExtra("aspactY", 1);
                    //设置输出图片大小
                    it.putExtra("outputX", 250);
                    it.putExtra("outputY", 250);
                    it.putExtra("scale", true);
                    it.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                    it.putExtra("return-data", false);
                    it.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    it.putExtra("noFaceDetection", true); // no face detection

                    startActivityForResult(it, REQUEST_PICTURE_CUT);
                }

                break;
            case REQUEST_CAPTURE://拍照
                if (resultCode == Activity.RESULT_OK){
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    getOutputUri();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    //得到拍完的照片进行裁剪
                    intent.setDataAndType(uriPhoto, "image/*");
                    //设置是否支持裁剪
                    intent.putExtra("crop", true);
                    //设置框的宽高比
                    intent.putExtra("aspactX", 1);
                    intent.putExtra("aspactY", 1);
                    //设置输出的照片
                    intent.putExtra("outputX", 250);
                    intent.putExtra("outputY", 250);
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                    intent.putExtra("return-data", false);
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    intent.putExtra("noFaceDetection", true); // no face detection

                    startActivityForResult(intent, REQUEST_PICTURE_CUT);
                }

                break;
            case REQUEST_PICTURE_CUT://裁剪完成
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        newImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    imagePath = readpic();
                    imageBase64 = Base64Util.GetImageStr(imagePath);
                    addImage.setImageBitmap(newImage);
                    imageState = 1;
                }
                break;

        }
    }
        private void getOutputUri(){
            File CropPhoto = new File(getExternalCacheDir(),"crop.png");
            try{
                if(CropPhoto.exists()){
                    CropPhoto.delete();
                }
                CropPhoto.createNewFile();
            }catch(IOException e){
                e.printStackTrace();
            }
            outputUri = Uri.fromFile(CropPhoto);
        }

        /**
         * 从保存裁剪图片的地址读取图片
         */
        private String readpic() {
            String filePath = getExternalCacheDir() + "/" + "crop.png";
            return filePath;
        }

        @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
