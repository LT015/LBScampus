package com.lbs.cheng.lbscampus.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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

import com.bumptech.glide.signature.ObjectKey;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.adapter.TagAdapter;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;
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
    ImageView testNext;
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
    private static final int REQUEST_Certification = 6; //实名认证
    private static final int REQUEST_PICK_IMAGE = 1; //相册选取
    private static final int REQUEST_CAPTURE = 2;  //拍照
    private static final int REQUEST_PICTURE_CUT = 3;  //剪裁图片
    private static final int REQUEST_PERMISSION = 4;  //权限请求
    public static final int REQUEST_LOGIN = 5;      //登录
    private Uri imageUri;//原图保存地址
    private Uri outputUri;//剪切的地址
    private String imagePath;
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
            if(noticeDetail.getImageList().size()>0){
                String url= HttpUtil.HOME_PATH + HttpUtil.Image+noticeDetail.getImageList().get(0).getImagePath();
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
                case 0:
                    radio0.setChecked(true);
                    break;
                case 1:
                    radio1.setChecked(true);
                    break;
                case 2:
                    radio2.setChecked(true);
                    break;
                case 3:
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
                            Toast.makeText(AddNoticeActivity.this, "获取信息失败!", Toast.LENGTH_SHORT).show();
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
                openCamera();
                break;
            case R.id.tx_2:
                dialog.dismiss();
                selectFromAlbum();
                break;
            case R.id.tx_3:
                dialog.dismiss();
                break;
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
        notice.setPublisher(userBean);
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
                            notice = new Gson().fromJson(jsonObject.toString(),NoticeBean.class);

                            if(imageJsonList.size()>0){
                                updateImage();
                            }
                            else{
                                if(saveType == 1){
                                    Toast.makeText(AddNoticeActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(AddNoticeActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                }

                                finish();
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
        String json = new Gson().toJson(imageJsonList);
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
                        final JSONArray jsonArray;
                        try {
                            jsonArray = new JSONArray(responseText);
                            list = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<NoticeBean>>(){}.getType());
                            if(saveType == 1){
                                Toast.makeText(AddNoticeActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AddNoticeActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                noticeType = 0;
            }
            if (checkedId == radio1.getId()) {
                noticeType = 1;
            }
            if (checkedId == radio2.getId()) {
                noticeType = 2;
            }
            if (checkedId == radio3.getId()) {
                noticeType = 3;
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
    private void openCamera() {
        File file = new FileStorage().createIconFile(); //用到了sd卡权限,运行时权限处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(AddNoticeActivity.this, "com.lbs.cheng.lbscampus.fileprovider", file);//通过FileProvider创建一个content类型的Uri
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
        if (DocumentsContract.isDocumentUri(AddNoticeActivity.this, uri)) {
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
                    imageJsonList.add("{\"data:image/png;base64,"+image+"\"}");
                    GlideUtil.load(AddNoticeActivity.this, imagePath, addImage, GlideUtil.REQUEST_OPTIONS);



                }
                break;

        }
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
