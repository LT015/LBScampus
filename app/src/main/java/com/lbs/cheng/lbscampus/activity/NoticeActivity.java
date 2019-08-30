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
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.BuildingBean;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;
import com.lbs.cheng.lbscampus.util.HttpUtil;
import com.lbs.cheng.lbscampus.util.LocationUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
    }
    private void initInfo() {
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
        collect.setOnClickListener(this);
        noticeTitle.setText(notice.getTitle());
        noticeContent.setText("  "+notice.getContent());
//        Date date=notice.getPublishTime();
//        SimpleDateFormat simleDateFormat=new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
//        String time=simleDateFormat.format(date);
        publishTime.setText("2019.04.06");
        if(notice.getPublisher()!=null){
            publisher.setText("发布人："+notice.getPublisher().getUserName());
        }else{
            publisher.setText("发布人：");
        }



    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.notice_collection:
                if(isCollected == 0){
                    isCollected = 1;
                    collect.setImageResource(R.mipmap.ic_collect);
                    Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                }else{
                    isCollected =0;
                    collect.setImageResource(R.mipmap.ic_uncollect);
                    Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
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

}
