package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.BuildingBean;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;
import com.lbs.cheng.lbscampus.bean.TagBean;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lbs.cheng.lbscampus.util.GlideUtil;
import com.lbs.cheng.lbscampus.view.RCImageView;
import com.lt.common.util.HttpUtil;
import com.lbs.cheng.lbscampus.util.LocationUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NoticeActivity extends BaseActivity {

    ImageView back;
    TextView titleName;
    private int isCollected = 0;
    public NoticeDetailBean notice;
    @BindView(R.id.notice_title)
    TextView noticeTitle;
    @BindView(R.id.notice_publisher)
    TextView publisher;
    @BindView(R.id.publish_time)
    TextView publishTime;
    @BindView(R.id.notice_collection)
    ImageView collect;
    @BindView(R.id.notice_content)
    TextView noticeContent;
    @BindView(R.id.notice_place)
    TextView noticePlace;
    @BindView(R.id.notice_place_tv)
    TextView noticePlaveTv;
    @BindView(R.id.notice_source)
    TextView noticeSource;
    @BindView(R.id.notice_image)
    RCImageView noticeImage;

    public String buindingName;
    private int status = 2;//status为2时表示查看当前notice是否被该用户收藏   收藏为1，未收藏为0


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("天津财经大学");
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent=getIntent();
        String json = intent.getStringExtra("noticeDetail");
        notice = new Gson().fromJson(json,NoticeDetailBean.class);
        //getNoticeDetail();

    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        collect.setOnClickListener(this);
        noticeTitle.setText(notice.getTitle());
        noticeContent.setText("  "+notice.getContent());
        Date date=notice.getPublishTime();
        SimpleDateFormat simleDateFormat=new SimpleDateFormat("yyy-MM-dd HH:mm");
        String time=simleDateFormat.format(date);
        publishTime.setText(time);
        if(notice.getPicturePath()!=null){
            String path = HttpUtil.HOME_PATH + HttpUtil.Image +"notice/"+ notice.getPicturePath();
            noticeImage.setRadius(15);
//            GlideUtil.REQUEST_OPTIONS.signature(new ObjectKey(System.currentTimeMillis()));//签名  用以重新获取图片
            GlideUtil.load(NoticeActivity.this, path, noticeImage, GlideUtil.REQUEST_OPTIONS);
        }
        if(notice.getPublisher()!=null){
            publisher.setText("发布人："+notice.getPublisher().getUserName());
            noticeSource.setVisibility(View.VISIBLE);
            if(notice.getPublisher().getType() == 1){
                noticeSource.setText("来源：学生个体");
            }else{
                noticeSource.setText("来源：老师");
            }
        }else{
            publisher.setText("发布人：");

        }
        if(notice.getBuildingId() != 0){
            noticePlace.setVisibility(View.VISIBLE);
            getBuildingName();
        }
        UserBean user = DataSupport.findLast(UserBean.class);
        if(user != null){
            getUserNoticeStatus();
        }



    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.notice_collection:
                UserBean user = DataSupport.findLast(UserBean.class);
                if(user != null){
                    getUserNoticeStatus();
                }else{
                    Toast.makeText(this, "登录后收藏", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.title_back:
                finish();
                break;
        }
    }
//    public void getNoticeDetail(){
//        List<String> list=new ArrayList<>();
//        list.add(String.valueOf(noticeId));
//        HttpUtil.sendOkHttpGetRequest(HttpUtil.GET_NOTICE_BY_ID, list, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                final String responseText = response.body().string();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        try{
//                            final JSONObject jsonObject = new JSONObject(responseText);
//                            notice = new Gson().fromJson(jsonObject.toString(),NoticeDetailBean.class);
//                            initInfo();
//
//                        }catch (JSONException e){
//                            Log.d("LoginActivity",e.toString());
//                            Toast.makeText(NoticeActivity.this, "获取信息失败!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//
//    }
    private void getBuildingName(){
        String url = HttpUtil.HOME_PATH + HttpUtil.GET_BUILDING_NAME + notice.getBuildingId();
        HttpUtil.sendOkHttpGetRequest( url, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NoticeActivity.this, "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
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

                        buindingName = responseText;
                        noticePlaveTv.setText(buindingName);
                    }
                });
            }
        });
    }

    private void getUserNoticeStatus(){//
        UserBean user= DataSupport.findLast(UserBean.class);
        List<String> list1 = new ArrayList<>();
        list1.add("user");
        list1.add(user.getUserId());
        list1.add("notice");
        list1.add(String.valueOf(notice.getNoticeId()));
        list1.add("status");
        list1.add(String.valueOf(status));
        String url = HttpUtil.HOME_PATH + HttpUtil.COLLECT_NOTICE;
        HttpUtil.sendOkHttpGetRequest( url, list1, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NoticeActivity.this, "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
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
                        int flag = 0;//用来判断要不要弹toast
                        if(status == 2){
                            flag = 1;
                        }
                        status = Integer.valueOf(responseText);
                        if(status == 0){
                            isCollected = 1;
                            collect.setImageResource(R.mipmap.ic_uncollect);
                            if(flag == 0){
                                Toast.makeText(NoticeActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                            }
                        }else if(status == 1){
                            isCollected =0;
                            collect.setImageResource(R.mipmap.ic_collect);
                            if(flag == 0){
                                Toast.makeText(NoticeActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
            }
        });
    }

}
